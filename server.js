const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const app = express();
const port = 3000;

// Replace with your MongoDB URI
const mongoURI = 'mongodb://localhost:27017/AppUsers';

mongoose.connect(mongoURI, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('MongoDB connected'))
  .catch(err => console.log(err));

const UserSchema = new mongoose.Schema({
  username: String,
  password: String,
});

const User = mongoose.model('User', UserSchema);

app.use(bodyParser.json());

app.post('/register', async (req, res) => {
  try {
    const { username, password } = req.body;
    const hashedPassword = await bcrypt.hash(password, 10);
    const newUser = new User({ username, password: hashedPassword });
    await newUser.save();
    res.status(201).send('User created');
  } catch (error) {
    res.status(500).send('Error registering new user');
  }
});

app.post('/login', async (req, res) => {
  try {
    const { username, password } = req.body;
    const user = await User.findOne({ username });
  // hashed version
  //   if (user && await bcrypt.compare(password, user.password)) {
  //     const token = jwt.sign({ userId: user._id }, 'YourSecretKey', { expiresIn: '1h' });
  //     res.status(200).json({ token });
  //   } else {
  //     res.status(400).send('Invalid credentials');
  //   }
  // } catch (error) {
  //   res.status(500).send('Error logging in');
  // }
  if (password == user.password) {
        const token = jwt.sign({ userId: user._id }, 'YourSecretKey', { expiresIn: '1h' });
        res.status(200).json({ token });
      } else {
        res.status(400).send('Invalid credentials');
      }
    } catch (error) {
      res.status(500).send('Error logging in');
    }
});

app.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
