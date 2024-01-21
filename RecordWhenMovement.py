# Record when movement detect and save as a video

import cv2
import time

# Use 0 for the default webcam
cap = cv2.VideoCapture(0)

# Check if the webcam is opened correctly
if not cap.isOpened():
    raise IOError("Cannot open webcam")

# Get frame size and frame rate from the webcam
frame_width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
frame_height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
# frame_rate = cap.get(cv2.CAP_PROP_FPS)
frame_rate = 30

# Create a background subtractor
fgbg = cv2.createBackgroundSubtractorMOG2()

recording = False
last_movement_time = None
record_duration_after_movement = 2  # 2 seconds

# Use 'avc1' codec for MP4 format
fourcc = cv2.VideoWriter_fourcc(*'avc1')
out = None

while True:
    ret, frame = cap.read()
    if not ret:
        break

    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    fgmask = fgbg.apply(gray)

    contours, _ = cv2.findContours(fgmask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    movement_detected = False
    for contour in contours:
        if cv2.contourArea(contour) > 500:
            movement_detected = True
            last_movement_time = time.time()
            x, y, w, h = cv2.boundingRect(contour)
            cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

    if movement_detected:
        if not recording:
            recording = True
            out = cv2.VideoWriter('output.mp4', fourcc, frame_rate, (frame_width, frame_height))
        if out:
            out.write(frame)

    elif recording and (time.time() - last_movement_time > record_duration_after_movement):
        recording = False
        out.release()
        out = None

    cv2.imshow('Frame', frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
if out:
    out.release()
cv2.destroyAllWindows()
