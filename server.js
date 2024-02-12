require('dotenv').config();

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
});

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

app.post('/register',
  // Validation rules
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
      const { username, password } = req.body;
      const hashedPassword = await bcrypt.hash(password, 10);
      const newUser = new User({ username, password: hashedPassword });
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
    const { username, password } = req.body;
    const user = await User.findOne({ username });

    if (user && await bcrypt.compare(password, user.password)) {
      const token = jwt.sign({ userId: user._id }, 'YourSecretKey', { expiresIn: '1h' });
      res.status(200).json({ token });
    } else {
      res.status(400).send('Invalid credentials');
    }
  } catch (error) {
    res.status(500).send('Error logging in');
  }

});

// Assuming you have a function to generate a password reset token
const generatePasswordResetToken = (user) => {
  // Ensure you have a secret key to sign the token; it should be a long, secure, and kept secret
  const secret = process.env.JWT_SECRET || 'your-very-secure-secret';
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
app.post('/forgot-password', async (req, res) => {
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

app.delete('/delete-user/:username', async (req, res) => {
  const { username } = req.params; // Extract the username from the route parameter

  try {
    // Use Mongoose to delete the user from the database
    const result = await User.deleteOne({ username: username });

    // If no user was found to delete, send a 404 response
    if (result.deletedCount === 0) {
      return res.status(404).send('User not found');
    }

    // Otherwise, send a success response
    res.status(200).send('User deleted successfully');
  } catch (error) {
    // If an error occurs, send a 500 response
    console.error('Error deleting user:', error);
    res.status(500).send('Error deleting user');
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

const httpsServer = https.createServer(credentials, app);

httpsServer.listen(port, () => {
  console.log(`HTTPS server running on port ${port}`);
});