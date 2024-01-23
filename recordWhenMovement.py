import cv2
import time
import os
import datetime
import readWebCam

# Initialize camera
video = readWebCam.readVideoFromCamera()

# Check if the webcam is opened correctly
if not video.isOpened():
    raise IOError("Cannot open webcam")

# Get frame size and frame rate
frame_width = int(video.get(cv2.CAP_PROP_FRAME_WIDTH))
frame_height = int(video.get(cv2.CAP_PROP_FRAME_HEIGHT))
frame_rate = 20

# Background subtractor
fgbg = cv2.createBackgroundSubtractorMOG2()

# Recording control variables
recording = False
last_movement_time = None
record_duration_after_movement = 7  # 7 seconds after movement stops
first_movement_detected = False  # Flag to track the first movement

# Directory for recordings
recordings_dir = "Recordings"
if not os.path.exists(recordings_dir):
    os.makedirs(recordings_dir)

# Video writer setup
fourcc = cv2.VideoWriter_fourcc(*'avc1')
out = None

# Increase this value to ignore smaller movements
min_contour_area = 2000  # Adjust this value based on your specific needs

while True:
    frame = readWebCam.captureFrame(video)

    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    fgmask = fgbg.apply(gray)

    # Apply thresholding
    _, thresh = cv2.threshold(fgmask, 25, 255, cv2.THRESH_BINARY)

    # Morphological operations to close gaps
    kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (3, 3))
    thresh = cv2.dilate(thresh, kernel, iterations=2)

    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    movement_detected = False
    for contour in contours:
        if cv2.contourArea(contour) > min_contour_area:
            movement_detected = True
            if not first_movement_detected:
                first_movement_detected = True  # Mark the first movement
                continue  # Skip the rest of the loop for the first movement

            if not recording:
                last_movement_time = time.time()
                # Generate timestamped filename
                timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
                filename = os.path.join(recordings_dir, f"{timestamp}.mp4")
                # Start recording
                out = cv2.VideoWriter(filename, fourcc, frame_rate, (frame_width, frame_height))
                recording = True
            x, y, w, h = cv2.boundingRect(contour)
            cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

    if recording:
        out.write(frame)
        if not movement_detected and time.time() - last_movement_time > record_duration_after_movement:
            recording = False
            out.release()
            out = None

    readWebCam.displayFrame(frame)

    if readWebCam.userExitRequest():
        break

readWebCam.stopReading(video)
if out:
    out.release()
