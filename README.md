# CHOMG

CHOMG is our security app that uses _motion detection technology_, turning webcams that are connected to a _Raspberry Pi_ into advanced security devices. It monitors your space for unexpected movements, notifying you of potential security breaches. Our system processes and sends data to a _digital ocean cloud server_, allowing users to receive real-time alerts through a sleek, _user-friendly app_. This ensures customers can monitor their property's safety from anywhere, offering peace of mind with ease!

## Description

To use CHOMG, you simple have to connect your raspberry pi (with all motion-detection scripts pre-installed on) to your USB camera. Once this is complete, simply place the camera in an appropriate location create an account on our app, log in and reap the security benefits!

## Getting Started

The directories featured in this master branch are all the branches we utilized in CHOMG's development

### Branches (Seen as directories)

* imageDetection
* appBackEnd
* appDev
* emailSending

### Code Featured
## Image Detection

For this "branch" to run you can just run CHOMG.py. When this being ran, valid server certificarte, public key algortihm key and environment file with all the users username, password and encryption key.

## AppBackEnd

This "branch" contains all the necessary components to run the Node.js server that hosts the app. 

## AppDev

This branch features everything need to run in android studio. This includes a lot of files and dependencies. Gradle is the build model we used and would need to be installed.

## emailSending

Small branch that jut hosts dailyEmail.py script which runs on the Node.js server. The file is hardcoded as it is for development purposes, however commented out at the bottom is the mongoDB implementation that would be used in true deployment.

## Help

For any queries or concerns we are reachable on our email address; <chomgscs3305@gmail.com>

## Authors

Contributors names and student numbers

```text
Amy Marie Craven        121401096
Liam Healy              121447212
Luca Gahan              121352981
Marian Angeles Mendoza  121374793
Jack O Sullivan         121316523
```

## Acknowledgments

Inspiration, code snippets, etc.

* [_Haar-based Detectors For Pedestrian Detection" by Hannes Kruppa and Bernt Schiele_ from ETH Zurich, Switzerland](https://www.myexperiment.org/files/423.html)
* [_README-Template.md by DomPizzie_](https://gist.github.com/DomPizzie/7a5ff55ffa9081f2de27c315f5018afc)
