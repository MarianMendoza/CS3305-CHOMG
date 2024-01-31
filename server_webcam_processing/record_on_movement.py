import cv2
import os
import datetime

recording = False
out = None
last_movement_time = None

def record_on_movement(frame, is_movement_detected, recordings_dir="Recordings", frame_rate=20):
    global recording, out, last_movement_time

    if not os.path.exists(recordings_dir):
        os.makedirs(recordings_dir)

    # Use a widely supported codec and container
    fourcc = cv2.VideoWriter_fourcc(*'avc1')

    # Start recording only if movement is detected and not already recording
    if is_movement_detected and not recording:
        recording = True
        last_movement_time = datetime.datetime.now()
        timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = os.path.join(recordings_dir, f"{timestamp}.mp4")
        frame_width = int(frame.shape[1])
        frame_height = int(frame.shape[0])
        out = cv2.VideoWriter(filename, fourcc, frame_rate, (frame_width, frame_height))

    # Record the frame if currently recording
    if recording:
        out.write(frame)
        # Stop recording if there's been no movement for a set duration
        if not is_movement_detected and (datetime.datetime.now() - last_movement_time).total_seconds() > 7:
            recording = False
            out.release()
            out = None

def cleanup():
    global out
    if out is not None:
        out.release()

