import cv2
import os
import datetime

class Recorder(object):

    def __init__(self, reference_frame) -> None:    

        self.recording = False
        self.out = None
        self.last_movement_time = None
        self.recordings_dir = "Recordings"
        self.frame_width = int(reference_frame.shape[1])
        self.frame_height = int(reference_frame.shape[0])
        self.framerate = 20
        self.out = None
        self.last_movement_time = None
        self.four_character_code = cv2.VideoWriter_fourcc(*'avc1') # Define the codec and create VideoWriter object
        self.__create_directory_if_not_existing()

    def __create_directory_if_not_existing(self):
        # Ensure the recordings directory exists
        if not os.path.exists(self.recordings_dir):
            os.makedirs(self.recordings_dir)
    
    def set_new_video_recorder(self):
        last_movement_time = datetime.datetime.now()
        timestamp = last_movement_time.strftime("%Y%m%d_%H%M%S")
        filename = os.path.join(self.recordings_dir, f"{timestamp}.mp4")
        self.out = cv2.VideoWriter(filename, self.four_character_code, self.frame_rate, (self.frame_width, self.frame_height))

    def stop_recording(self):
        # Stop and save the current recording session
        self.recording = False
        self.out.release()
        self.out = None


    def record_frame(self, frame): 
        if not self.recording:
            # Start a new recording session
            self.recording = True
            self.set_new_video_recorder()
        # Record the current frame
        self.out.write(frame)
        # Update the last movement time
        self.last_movement_time = datetime.datetime.now()

    def stop_recording_if_time_elapsed(self):
        if self.last_movement_time is None:
            return
        
        # Check for end of movement
        elapsed_time_without_movement = (datetime.datetime.now() - self.last_movement_time).total_seconds()
        if elapsed_time_without_movement > 2:  # Adjust as needed
            self.stop_recording()

    def cleanup():
        global out
        if out is not None:
            out.release()
            out = None
            print("Resources have been cleaned up")