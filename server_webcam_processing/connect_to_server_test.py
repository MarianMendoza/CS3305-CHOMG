from connect_to_database import Mongo
import os
from dotenv import load_dotenv
from sshtunnel import SSHTunnelForwarder
import video_frame_handling
import record_on_movement
import linked_list_file_saver
import paramiko
import time
import send_json
load_dotenv()

MONGO_USERNAME = os.getenv('MONGO_USERNAME')
MONGO_PASSWORD = os.getenv('MONGO_PASSWORD')
MONGO_HOST = os.getenv('MONGO_HOST')
MONGO_PORT = os.getenv('MONGO_PORT')
MONGO_DATABASE = os.getenv('MONGO_DATABASE')
MONGO_COLLECTION  = os.getenv('MONGO_COLLECTION')
SSH_USERNAME = os.getenv('SSH_USERNAME')
SSH_PASSWORD = os.getenv('SSH_PASSWORD')
SSH_HOST = os.getenv('SSH_HOST')
SSH_PORT = os.getenv('SSH_PORT')
CHOMG_USERNAME = os.getenv('CHOMG_USERNAME')
JWT_KEY = os.getenv('JWT_KEY')
# SSH server configuration
ssh_private_key = 'key'

def run():
    frame_handler = video_frame_handling.VideoFrameHandler()
    frame_recorder = record_on_movement.Recorder(frame_handler.get_current_frame())
    json_generator = send_json.JsonCreator(CHOMG_USERNAME, JWT_KEY)
    linked_list = linked_list_file_saver.LinkedList()
    # Create an SSH tunnel
    with SSHTunnelForwarder(
        (SSH_HOST, int(SSH_PORT)),
        ssh_username=SSH_USERNAME,
        ssh_pkey=ssh_private_key,
        ssh_password = None,
        remote_bind_address=('localhost', int(MONGO_PORT))
    ) as tunnel:
        try:
            # Connect to MongoDB through the tunnel with authentication
            mongo_connection = Mongo(MONGO_HOST, MONGO_USERNAME, MONGO_PASSWORD, tunnel.local_bind_port, MONGO_DATABASE, MONGO_COLLECTION, CHOMG_USERNAME)
            mongo_connection.open_connection()
            # Wait 30 seconds before starting
            time.sleep(30)
            while True:
                current_time_seconds = int(time.time())  # Get current time in seconds as an integer
                if current_time_seconds % 30 == 0 \
                    or frame_handler.is_movement_detected() \
                    or frame_handler.is_human_detected():
                
                    update_users_json(json_generator, frame_handler.is_movement_detected(), frame_handler.is_human_detected(), mongo_connection)
                if frame_handler.is_movement_detected():
                    if  frame_recorder.is_recording():
                        # frame_handler.handle_motion_detection_in_frame_using_contours() # Draw bounding box entity recognition etc
                        # Record based on significant movement detection
                        frame_recorder.record_frame(frame_handler.get_current_frame())
                    else:
                        # Record the 30 seconds before motion was detected
                        for frame in linked_list.get_list_of_frames_in_linked_list():
                            frame_recorder.record_frame(frame)
                        linked_list.clear_linked_list()
                
                else: # Stop recording after set time is passed
                    if frame_recorder.is_recording():
                        frame_recorder.stop_recording_if_time_elapsed(frame_handler.get_current_frame())
                        if not frame_recorder.is_recording():
                            filename = frame_recorder.get_last_recorded_files_name()
                            add_video_path_to_db(filename, mongo_connection)
                            # SCP (Secure Copy) the file to the remote server
                            upload_file(filename)
                            # Check if the file exists before attempting to delete
                            if os.path.exists(filename):
                                # Delete the file
                                os.remove(filename)
                                print(f"File '{filename}' deleted successfully.")
                # Displaying the current frame and optional foreground
                frame_handler.display_current_frame()
                frame_handler.display_foreground()
    
                if not frame_recorder.is_recording():
                    # Save frame in linked list
                    linked_list.add_frame(frame_handler.get_current_frame())
                # Prepare for the next frame
                frame_handler.set_next_frame_as_current()
                
                if frame_handler.stop_detecting():
                    break
            

        finally:
            # Cleanup and release resources on exit
            frame_recorder.cleanup()
            mongo_connection.close_connection()

def upload_file(filename):
    with paramiko.Transport((SSH_HOST, int(SSH_PORT))) as transport:
        private_key = paramiko.RSAKey(filename=ssh_private_key)
        transport.connect(username=SSH_USERNAME, pkey=private_key)

        sftp = paramiko.SFTPClient.from_transport(transport)

        local_file_path = f"{filename}"
        # Destination path on the remote server
        remote_file_path = os.path.join("/root/CHOMG/recordedFootage/liam@healy.com/", os.path.basename(local_file_path))

        # Upload the file
        sftp.put(local_file_path, remote_file_path)

def add_video_path_to_db(filename, mongo_connection):
    # Define the filter to find the document you want to update
    filename=filename[len("Recordings\\"):]
    # Define the update operation to append to the 'videos' array or create it
    add_video = {'$addToSet': {'videos': f'/root/CHOMG/recordedFootage/liam@healy.com/{filename}'}}
    mongo_connection.add_video_in_users_collection(add_video)

def update_users_json(json_generator, motion_detected, human_detected, mongo_connection):
    json = json_generator.generate_jwt(motion_detected, human_detected)
    # Define the filter to find the document you want to update
    mongo_connection.add_video_in_users_collection({"$set":{"json": json}})

if __name__ == "__main__":
    run()