require('dotenv').config();

const admin = require('firebase-admin');
const express = require('express');
const morgan = require('morgan');
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
const { spawn } = require('child_process');

const app = express();
const port = 443;

const mongoURI = `mongodb://${process.env.MONGO_USER}:${process.env.MONGO_PASSWORD}@${process.env.MONGO_HOST}:27017/${process.env.MONGO_DB}?authSource=admin`;

// Function to start the Python watcher script
function startPythonWatcher() {
  const pythonProcess = spawn('python3', ['dailyEmail.py']);

  pythonProcess.stdout.on('data', (data) => {
    console.log(`Python stdout: ${data}`);
  });

  pythonProcess.stderr.on('data', (data) => {
    console.error(`Python stderr: ${data}`);
  });

  pythonProcess.on('close', (code) => {
    console.log(`Python script exited with code ${code}`);
  });
}


mongoose.connect(mongoURI, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => {console.log('MongoDB connected'); startPythonWatcher();})
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

// use helmet for security
app.use(helmet());

// Rate limiter
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 10000,
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

  const isBlacklisted = await TokenBlacklist.findOne({ token: token });
  if (isBlacklisted) {
    return res.status(401).send('Token invalidated');
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET || 'YourSecretKey');
    req.user = decoded;
    next();
  } catch (error) {
    res.status(401).send(error.message);
  }

};

const unless = (middleware, ...paths) => {
    return (req, res, next) => {
        const pathCheck = paths.some(path => path === req.path);
        pathCheck ? next() : middleware(req, res, next);
    };
};

app.use(unless(verifyToken, '/login', '/register', '/send-notification'));
app.use(morgan('dev'));


app.post('/send-notification', async (req, res) => {
    const { user_id, is_movement_detected, is_human_detected, exp } = req.body;
    const user = await User.findOne({username: user_id});

    if (!user) {
        return res.status(404).send('User not found.');
    }
    // Retrieve fcm token from user document
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

    if (is_human_detected) {
        message.notification.title = 'Person Detected';
        message.notification.body = 'A person has been detected in your monitored area.';
    } else if (is_movement_detected) {
        message.notification.title = 'Motion Detected';
        message.notification.body = 'Motion has been detected in your monitored area.';
    } else {
        return res.status(400).send('Invalid detection type.');
    }

    // Send a message to the device corresponding to the provided FCM token
    admin.messaging().send(message)
        .then((response) => {
            console.log(message);
            res.send('Notification sent successfully');
        })
        .catch((error) => {
            console.log('Error sending message:', error);
            res.status(500).send('Error sending notification');
        });
});

app.post('/update-fcm-token', verifyToken, async (req, res) => {
    const { fcmToken } = req.body;
    if (!fcmToken) {
        return res.status(400).send('FCM token is required');
    }

    try {
        const userId = req.user.userId;
        const user = await User.findById(userId);

        if (!user) {
            return res.status(404).send('User not found');
        }

        user.fcmToken = fcmToken;
        await user.save();

        res.send('FCM token updated successfully');
    } catch (error) {
        console.error('Error updating FCM token:', error);
        res.status(500).send('Internal Server Error');
    }
});

app.post('/register',

  [
    body('username').isEmail(),
    body('password').isLength({ min: 6 })
  ],
  async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    try {
      const { username, password, token: fcmToken } = req.body;
      const hashedPassword = await bcrypt.hash(password, 10);
      const newUser = new User({ fcmToken,  username, password: hashedPassword });
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
    const { username, password, token: fcmToken } = req.body;
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

const generatePasswordResetToken = (user) => {
  const secret = process.env.JWT_SECRET || 'YourSecretKey';
  const expiresIn = '1h';

  const payload = {
    id: user._id,
    email: user.username,
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

app.post('/forgot-password', verifyToken, async (req, res) => {
  const { email } = req.body;

  const user = await User.findOne({ username: email });
  if (!user) {
    return res.status(404).send('User not found');
  }

  const token = generatePasswordResetToken(user);

  try {
    await sendEmail(user.username, token);
    res.status(200).send('Password reset email sent');
  } catch (error) {
    console.error('Failed to send password reset email:', error);
    res.status(500).send('Failed to send password reset email');
  }
});

app.get('/user-details', verifyToken,  async (req, res) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).send('Authorization header missing or incorrect');
  }

  const token = authHeader.substring(7);

  try {
    const decoded = jwt.verify(token, 'YourSecretKey');
    const user = await User.findById(decoded.userId).select('-password'); // Exclude password from the result
    if (!user) return res.status(404).send('User not found');

    res.json({ username: user.username});

  } catch (error) {
    console.error("Error fetching user details:", error.message);
    const statusCode = error.name === 'JsonWebTokenError' ? 401 : 500;
    res.status(statusCode).send('Error fetching user details' + error.message);
  }
});

app.delete('/delete-account', verifyToken, async (req, res) => {
  try {
    const userId = req.user.userId;
    const deleteResult = await User.deleteOne({ _id: userId });

    if (deleteResult.deletedCount === 0) {
      return res.status(404).send('User not found');
    }

    res.send('Account deleted successfully');
  } catch (error) {
    console.error('Account deletion error:', error);
    res.status(500).send('Error deleting account');
  }
});

app.post('/change-email', verifyToken, async (req, res) => {
    const { email: newEmail } = req.body;
    if (!newEmail) {
        return res.status(400).send('New email is required.');
    }

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

        user.username = newEmail;
        await user.save();

        res.send('Email updated successfully.');
    } catch (error) {
        console.error('Error changing email:', error);
        res.status(500).send('Error updating email.');
    }
});

app.post('/set-new-password', verifyToken, async (req, res) => {
    const { newPassword, confirmPassword } = req.body;

    if (!newPassword || !confirmPassword) {
        return res.status(400).send('Both new password and confirmation are required.');
    }

    if (newPassword !== confirmPassword) {
        return res.status(400).send('New password and confirmation do not match.');
    }

    try {
        const userId = req.user.userId;

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

async function getSortedVideoFiles(videosDir) {
    try {
        // Read the directory's contents
        const files = await fsPromises.readdir(videosDir);
        const fileDetails = await Promise.all(files.map(async (file) => {
            const filePath = path.join(videosDir, file);
            const stat = await fsPromises.stat(filePath);
            return { name: file, time: stat.mtime.getTime() };
        }));

        // Sort files by last modified time in descending order
        fileDetails.sort((a, b) => b.time - a.time);

        // Return sorted file names
        return fileDetails.map(file => file.name);
    } catch (error) {
        console.error('Error getting sorted video files:', error);
        throw error;
    }
}

module.exports = getSortedVideoFiles;

app.get('/get-video', verifyToken, async (req, res) => {
    const index = req.query.index ? parseInt(req.query.index, 10) : 0; // Default to the most recent if no index

    try {
        const userId = req.user.userId;
        const user = await User.findById(userId);
        if (!user) {
            return res.status(404).send('User not found.');
        }

        const videosDir = path.join('/root/CHOMG/recordedFootage', user.username);
        const videoFiles = await getSortedVideoFiles(videosDir);
        if (index < 0 || index >= videoFiles.length) {
            return res.status(404).send('Video not found.');
        }

        const videoPath = path.join(videosDir, videoFiles[index]);
        const videoName = path.basename(videoPath);
        res.setHeader('Content-Disposition', `inline; filename="${videoName}"`);
        console.log(`Sending video with Content-Disposition: ${res.getHeader('Content-Disposition')}`);
        res.sendFile(videoPath);
    } catch (error) {
        console.error('Error fetching video:', error);
        res.status(500).send('Internal Server Error');
    }
});

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
      const videoName = path.basename(videoPath);

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
          console.log(`Sending video with Content-Disposition: ${res.getHeader('Content-Disposition')}`);
          res.writeHead(206, head);
          file.pipe(res);
      } else {
          const head = {
              'Content-Length': fileSize,
              'Content-Type': 'video/mp4',
              'Content-Disposition': `inline; filename="${videoName}"`
          };
          console.log(`Sending video with Content-Disposition: ${res.getHeader('Content-Disposition')}`);
          res.writeHead(206, head);
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