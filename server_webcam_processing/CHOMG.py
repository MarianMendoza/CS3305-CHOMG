from connect_to_database import Mongo
import os
from dotenv import load_dotenv
from sshtunnel import SSHTunnelForwarder
import video_frame_handling
import record_on_movement
import linked_list_file_saver
import paramiko
import post_data
from datetime import datetime, timedelta

class CHOMG(object):
    def __init__(self):
        load_dotenv()
        self.MONGO_USERNAME = os.getenv('MONGO_USERNAME')
        self.MONGO_PASSWORD = os.getenv('MONGO_PASSWORD')
        self.MONGO_HOST = os.getenv('MONGO_HOST')
        self.MONGO_PORT = os.getenv('MONGO_PORT')
        self.MONGO_DATABASE = os.getenv('MONGO_DATABASE')
        self.MONGO_COLLECTION  = os.getenv('MONGO_COLLECTION')
        self.SSH_USERNAME = os.getenv('SSH_USERNAME')
        self.SSH_HOST = os.getenv('SSH_HOST')
        self.SSH_PORT = os.getenv('SSH_PORT')
        self.CHOMG_USERNAME = os.getenv('CHOMG_USERNAME')
        self.SECRET_KEY = os.getenv('JWT_KEY')
        self.ssh_private_key = 'key' #Path to private key

    def run(self):
        self.frame_handler = video_frame_handling.VideoFrameHandler()
        self.frame_recorder = record_on_movement.Recorder(self.frame_handler.get_current_frame())
        self.linked_list = linked_list_file_saver.LinkedList()
        self.post_to_server_handler = post_data.Server_Poster(self.SSH_HOST, 443)
        self.person_detected_notification_sent = False
        first_run = True
        # Create an SSH tunnel
        with SSHTunnelForwarder(
            (self.SSH_HOST, int(self.SSH_PORT)),
            ssh_username=self.SSH_USERNAME,
            ssh_pkey=self.ssh_private_key,
            ssh_password = None,
            remote_bind_address=('localhost', int(self.MONGO_PORT))
        ) as tunnel:
            try:
                # Connect to MongoDB through the tunnel with authentication
                mongo_connection = Mongo(self.MONGO_HOST, self.MONGO_USERNAME, self.MONGO_PASSWORD, tunnel.local_bind_port, self.MONGO_DATABASE, self.MONGO_COLLECTION, self.CHOMG_USERNAME)
                mongo_connection.open_connection()
                # Wait 30 seconds before starting
                # time.sleep(30)
                while True:
                    if self.frame_handler.is_movement_detected() and not first_run:
                        self.frame_handler.handle_motion_detection_in_frame_using_contours()
                        if  self.frame_recorder.is_recording():
                            # Record based on significant movement detection
                            self.frame_recorder.record_frame(self.frame_handler.get_current_frame())
                        else:
                            self.send_notification()
                            # Record the 30 seconds before motion was detected
                            for frame in self.linked_list.get_list_of_frames_in_linked_list():
                                self.frame_recorder.record_frame(frame)
                            self.linked_list.clear_linked_list()
                            self.frame_recorder.record_frame(self.frame_handler.get_current_frame())
                    
                    else: # Stop recording after set time is passed
                        if self.frame_recorder.is_recording():
                            self.frame_recorder.stop_recording_if_time_elapsed(self.frame_handler.get_current_frame())
                            if not self.frame_recorder.is_recording():
                                filename = self.frame_recorder.get_last_recorded_files_name()
                                self.add_video_path_to_db(filename, mongo_connection)
                                # SCP (Secure Copy) the file to the remote server
                                self.upload_file(filename)
                                # Check if the file exists before attempting to delete
                                if os.path.exists(filename):
                                    # Delete the file
                                    os.remove(filename)
                                self.person_detected_notification_sent = False
                    # Displaying the current frame and optional foreground (Only for Debugging)
                    # self.frame_handler.display_current_frame()
                    # self.frame_handler.display_foreground()
                    if not self.person_detected_notification_sent and self.frame_handler.is_human_detected(): 
                        self.send_notification()
                        self.person_detected_notification_sent = True
                    if not self.frame_recorder.is_recording():
                        # Save frame in linked list
                        self.linked_list.add_frame(self.frame_handler.get_current_frame())
                    # Prepare for the next frame
                    self.frame_handler.set_next_frame_as_current()
                    
                    if self.frame_handler.stop_detecting():
                        break
                
                    first_run = False
            finally:
                # Cleanup and release resources on exit
                self.frame_recorder.cleanup()
                mongo_connection.close_connection()

    def upload_file(self, filename):
        with paramiko.Transport((self.SSH_HOST, int(self.SSH_PORT))) as transport:
            private_key = paramiko.RSAKey(filename=self.ssh_private_key)
            transport.connect(username=self.SSH_USERNAME, pkey=private_key)

            sftp = paramiko.SFTPClient.from_transport(transport)

            local_file_path = f"{filename}"
            # Destination path on the remote server
            remote_file_path = os.path.join(f"/root/CHOMG/recordedFootage/{self.CHOMG_USERNAME}/", os.path.basename(local_file_path))
            # Upload the file
            sftp.put(local_file_path, remote_file_path)

    def send_notification(self):
            expiration_time = datetime.utcnow() + timedelta(minutes=5)
            exp = int(expiration_time.timestamp())
            data = {"user_id": self.CHOMG_USERNAME, "is_movement_detected": self.frame_handler.is_movement_detected(), "is_human_detected": self.frame_handler.is_human_detected(), "exp": exp}
            self.post_to_server_handler.post_to_server(self.SECRET_KEY, data)

    def add_video_path_to_db(self, filename, mongo_connection):
        # Define the filter to find the document you want to update
        filename=filename[len("Recordings\\"):]
        # Define the update operation to append to the 'videos' array or create it
        add_video = {'$addToSet': {'videos': f'/root/CHOMG/recordedFootage/{self.CHOMG_USERNAME}.com/{filename}'}}
        mongo_connection.add_video_in_users_collection(add_video)

if __name__ == "__main__":
    chomg = CHOMG() 
    chomg.run()