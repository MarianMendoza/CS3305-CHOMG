import cv2
import os
import datetime

recording = False
out = None
last_movement_time = None

def record_on_movement(frame, is_movement_detected, recordings_dir="Recordings", frame_rate=20):
    global recording, out, last_movement_time

    # Ensure the recordings directory exists
    if not os.path.exists(recordings_dir):
        os.makedirs(recordings_dir)

    # Define the codec and create VideoWriter object
    fourcc = cv2.VideoWriter_fourcc(*'avc1')

    # Start or continue a recording session
    if is_movement_detected:
        if not recording:
            # Start a new recording session
            recording = True
            last_movement_time = datetime.datetime.now()
            timestamp = last_movement_time.strftime("%Y%m%d_%H%M%S")
            filename = os.path.join(recordings_dir, f"{timestamp}.mp4")
            frame_width = int(frame.shape[1])
            frame_height = int(frame.shape[0])
            out = cv2.VideoWriter(filename, fourcc, frame_rate, (frame_width, frame_height))
        # Record the current frame
        out.write(frame)
        # Update the last movement time
        last_movement_time = datetime.datetime.now()

    # Check for end of movement
    elif recording:
        elapsed_time_without_movement = (datetime.datetime.now() - last_movement_time).total_seconds()
        if elapsed_time_without_movement > 2:  # Adjust as needed
            # Stop and save the current recording session
            recording = False
            out.release()
            out = None

def cleanup():
    global out
    if out is not None:
        out.release()
        out = None
        print("Resources have been cleaned up")