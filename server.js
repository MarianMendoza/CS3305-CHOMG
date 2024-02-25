require('dotenv').config();

const admin = require('firebase-admin');
const express = require('express');
const mongoose = require('mongoose');
const nodemailer = require('nodemailer');
const bcrypt = require('bcryptjs');
const fs = require('fs');
const https = require('https');
const { exec } = require('child_process');
const jwt = require('jsonwebtoken');
const { body, validationResult } = require('express-validator');
const rateLimit = require('express-rate-limit');
const helmet = require('helmet');
const path = require('path');
const fsPromises = require('fs').promises;

const app = express();
const port = 443;

// Replace with your MongoDB URI
const mongoURI = `mongodb://${process.env.MONGO_USER}:${process.env.MONGO_PASSWORD}@${process.env.MONGO_HOST}:27017/${process.env.MONGO_DB}?authSource=admin`;

mongoose.connect(mongoURI, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('MongoDB connected'))
  .catch(err => console.log(err));

const UserSchema = new mongoose.Schema({
  username: { type: String, unique: true },
  password: String,
  fcmToken: { type: String, required: false}
});

const tokenBlacklistSchema = new mongoose.Schema({
  token: { type: String, unique: true },
  expireAt: { type: Date, required: true }
});
tokenBlacklistSchema.index({ expireAt: 1 }, { expireAfterSeconds: 0 });
const TokenBlacklist = mongoose.model('TokenBlacklist', tokenBlacklistSchema);

const User = mongoose.model('User', UserSchema);

const privateKey = fs.readFileSync('server.key', 'utf8');
const certificate = fs.readFileSync('server.crt', 'utf8');
const credentials = { key: privateKey, cert: certificate };

app.use(helmet());

// Rate limiter
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // Limit each IP to 100 requests per windowMs
});

app.use(limiter);

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

let serviceAccount = require('./chomgFirebaseServiceKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const verifyToken = async (req, res, next) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).send('Authorization required');
  }

  const token = authHeader.substring(7);
  // Check if the token is blacklisted
  const isBlacklisted = await TokenBlacklist.findOne({ token: token });
  if (isBlacklisted) {
    return res.status(401).send('Token invalidated');
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET || 'YourSecretKey');
    req.user = decoded;
    next();
  } catch (error) {
    res.status(401).send('Invalid Token');
  }
};

app.post('/send-notification', verifyToken, async (req, res) => {
    const { user_id, motion_detected, person_detected, exp } = req.body;

    // Assuming `user_id` can be used to retrieve the user's FCM token
    const user = await User.findById(user_id);
    if (!user) {
        return res.status(404).send('User not found.');
    }

    // Assuming you store the user's FCM token in their document
    const fcmToken = user.fcmToken;

    if (!fcmToken) {
        return res.status(404).send('FCM token not found for user.');
    }

    let message = {
        notification: {
            title: '',
            body: ''
        },
        token: fcmToken
    };

    if (motion_detected) {
        message.notification.title = 'Motion Detected';
        message.notification.body = 'Motion has been detected in your monitored area.';
    } else if (person_detected) {
        message.notification.title = 'Person Detected';
        message.notification.body = 'A person has been detected in your monitored area.';
    } else {
        // Handle other types of notifications or errors
        return res.status(400).send('Invalid detection type.');
    }

    // Send a message to the device corresponding to the provided FCM token
    admin.messaging().send(message)
        .then((response) => {
            // Response is a message ID string
            console.log('Successfully sent message:', response);
            res.send('Notification sent successfully');
        })
        .catch((error) => {
            console.log('Error sending message:', error);
            res.status(500).send('Error sending notification');
        });
});

app.post('/register',
  // Validation rules
  [
    body('username').isEmail(),
    body('password').isLength({ min: 6 })
  ],
  async (req, res) => {
    console.log('Request Body:', req.body);
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    try {
      const { username, password, fcmToken } = req.body;
      const hashedPassword = await bcrypt.hash(password, 10);
      const newUser = new User({ username, password: hashedPassword, fcmToken });
      await newUser.save();
      res.status(201).send('User created');
    } catch (error) {
      if (error.code === 11000) {
        res.status(400).send('Username already exists');
      } else {
        res.status(500).send('Error registering new user');
      }
    }
  }
);

app.post('/login', async (req, res) => {
  try {
    const { username, password, fcmToken } = req.body;
    const user = await User.findOne({ username });

    if (user && await bcrypt.compare(password, user.password)) {
      user.fcmToken = fcmToken;
      await user.save();

      const token = jwt.sign({ userId: user._id }, 'YourSecretKey', { expiresIn: '1h' });
      res.status(200).json({ token });
    } else {
      res.status(400).send('Invalid credentials');
    }
  } catch (error) {
    res.status(500).send('Error logging in');
  }

});

app.post('/logout', verifyToken, async (req, res) => {
  const authHeader = req.headers.authorization;
  const token = authHeader.substring(7);
  const decoded = jwt.verify(token, process.env.JWT_SECRET || 'YourSecretKey');

  const expireAt = new Date(decoded.exp * 1000); // Convert JWT expiry from seconds to milliseconds

  try {
    await new TokenBlacklist({ token, expireAt }).save();
    res.send('Logged out successfully');
  } catch (error) {
    console.error('Error logging out:', error);
    res.status(500).send('Error during logout');
  }
});

// Assuming you have a function to generate a password reset token
const generatePasswordResetToken = (user) => {
  // Ensure you have a secret key to sign the token; it should be a long, secure, and kept secret
  const secret = process.env.JWT_SECRET || 'YourSecretKey';
  const expiresIn = '1h'; // Token expires in 1 hour

  // Payload can include any user-specific information; here, we're using the user's ID
  const payload = {
    id: user._id,
    email: user.username, // Assuming 'username' field is used for the email
  };

  // Sign the token with the user's payload and expiry time
  const token = jwt.sign(payload, secret, { expiresIn });

  return token;
};

const sendEmail = (email, token) => {
  const resetLink = `http://10.0.2.2:3000/reset-password?token=${token}`;
  const command = `python send_email.py '${email}' '${resetLink}'`;


  exec(command, (error, stdout, stderr) => {
    if (error) {
      console.error(`exec error: ${error}`);
      return;
    }
    console.log(`stdout: ${stdout}`);
    console.error(`stderr: ${stderr}`);
  });
};

// Endpoint to handle forgot password request
app.post('/forgot-password', verifyToken, async (req, res) => {
  const { email } = req.body; // Assuming EmailWrapper wraps the email in a JSON object with key 'email'

  const user = await User.findOne({ username: email });
  if (!user) {
    return res.status(404).send('User not found');
  }

  // Generate a password reset token or link
  const token = generatePasswordResetToken(user); // Implement this function based on your logic

  // Send the email
  try {
    await sendEmail(user.username, token); // Adjust this function to match your email sending logic
    res.status(200).send('Password reset email sent');
  } catch (error) {
    console.error('Failed to send password reset email:', error);
    res.status(500).send('Failed to send password reset email');
  }
});



app.post('/motion-detected', async (req, res) => {
  try {
    // Extract data from request body
    const { motion_detected, person_detected, connection_lost, program_terminated } = req.body;

    // Example logging
    console.log(`Motion detected: ${motion_detected}, Person detected: ${person_detected}, Connection lost: ${connection_lost}, Program terminated: ${program_terminated}`);

    // Here, implement the notification logic, e.g., calling a function to handle notifications.
    // sendNotificationToApp(motion_detected, person_detected, connection_lost, program_terminated);

    res.status(200).send('Notification processed');
  } catch (error) {
    console.error('Error handling motion detection:', error);
    res.status(500).send('Internal Server Error');
  }
});

app.get('/user-details', verifyToken,  async (req, res) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).send('Authorization header missing or incorrect');
  }

  const token = authHeader.substring(7);

  try {
    const decoded = jwt.verify(token, 'YourSecretKey'); // Use the same secret key as when signing the token
    const user = await User.findById(decoded.userId).select('-password'); // Exclude password from the result
    if (!user) return res.status(404).send('User not found');

    res.json({ username: user.username}); // Assuming username is the email
    console.log('Fetched user details:', user);

  } catch (error) {
    console.error("Error fetching user details:", error.message);
    const statusCode = error.name === 'JsonWebTokenError' ? 401 : 500;
    res.status(statusCode).send('Error fetching user details' + error.message);
  }
});

app.delete('/delete-account', verifyToken, async (req, res) => {
  // Assuming `verifyToken` middleware extracts user ID from the token and adds it to `req.user`
  try {
    const userId = req.user.userId; // or whatever property you have the ID stored under
    const deleteResult = await User.deleteOne({ _id: userId });

    if (deleteResult.deletedCount === 0) {
      return res.status(404).send('User not found');
    }

    // Optionally, also handle cleanup of any other user-related data here

    res.send('Account deleted successfully');
  } catch (error) {
    console.error('Account deletion error:', error);
    res.status(500).send('Error deleting account');
  }
});

app.post('/change-email', verifyToken, async (req, res) => {
    const { email: newEmail } = req.body; // Extract newEmail from the body using the EmailWrapper structure
    if (!newEmail) {
        return res.status(400).send('New email is required.');
    }

    // Optionally, validate the new email format here

    try {
        const userId = req.user.userId;
        const user = await User.findById(userId);
        const emailExists = await User.findOne({ username: newEmail });

        if (emailExists) {
            return res.status(400).json({error: 'Email already exists.'});
        }

        if (!user) {
            return res.status(404).send('User not found.');
        }

        user.username = newEmail; // Update the email
        await user.save();

        res.send('Email updated successfully.');
    } catch (error) {
        console.error('Error changing email:', error);
        res.status(500).send('Error updating email.');
    }
});

app.post('/set-new-password', verifyToken, async (req, res) => {
    console.log(req.body);
    const { newPassword, confirmPassword } = req.body;

    // Check if newPassword and confirmPassword are provided
    if (!newPassword || !confirmPassword) {
        return res.status(400).send('Both new password and confirmation are required.');
    }

    // Check if newPassword and confirmPassword match
    if (newPassword !== confirmPassword) {
        return res.status(400).send('New password and confirmation do not match.');
    }

    try {
        const userId = req.user.userId; // User ID is extracted from the token by verifyToken middleware

        // Find the user by ID
        const user = await User.findById(userId);
        if (!user) {
            return res.status(404).send('User not found.');
        }

        // Hash the new password and update it in the database
        const hashedPassword = await bcrypt.hash(newPassword, 10);
        user.password = hashedPassword;
        await user.save();

        res.send('Password has been updated successfully.');
    } catch (error) {
        console.error('Error setting new password:', error);
        res.status(500).send('Error updating password.');
    }
});

async function getMostRecentFile(dir) {
  const files = await fsPromises.readdir(dir);
  const sortedFiles = files
      .map(fileName => ({
          name: fileName,
          time: fs.statSync(`${dir}/${fileName}`).mtime.getTime()
      }))
      .sort((a, b) => b.time - a.time)
      .map(file => file.name);
  return sortedFiles.length ? sortedFiles[0] : null;
}

app.get('/get-recent-video', verifyToken, async (req, res) => {
  try {
      const userId = req.user.userId;
      const user = await User.findById(userId);
      if (!user) {
          return res.status(404).send('User not found.');
      }

      const videosDir = path.join('/root', 'CHOMG', 'recordedFootage', user.username);
      const mostRecentVideo = await getMostRecentFile(videosDir);

      if (!mostRecentVideo) {
          return res.status(404).send('No videos found.');
      }

      const videoPath = path.join(videosDir, mostRecentVideo);

      // Stream the video
      const stat = fs.statSync(videoPath);
      const fileSize = stat.size;
      const range = req.headers.range;

      if (range) {
          const parts = range.replace(/bytes=/, "").split("-");
          const start = parseInt(parts[0], 10);
          const end = parts[1] ? parseInt(parts[1], 10) : fileSize-1;
          const chunksize = (end-start)+1;
          const file = fs.createReadStream(videoPath, {start, end});
          const head = {
              'Content-Range': `bytes ${start}-${end}/${fileSize}`,
              'Accept-Ranges': 'bytes',
              'Content-Length': chunksize,
              'Content-Type': 'video/mp4',
          };
          res.writeHead(206, head);
          file.pipe(res);
      } else {
          const head = {
              'Content-Length': fileSize,
              'Content-Type': 'video/mp4',
          };
          res.writeHead(200, head);
          fs.createReadStream(videoPath).pipe(res);
      }
  } catch (error) {
      console.error('Error fetching recent video:', error);
      res.status(500).send('Error fetching recent video');
  }
});

const httpsServer = https.createServer(credentials, app);

httpsServer.listen(port, () => {
  console.log(`HTTPS server running on port ${port}`);
});