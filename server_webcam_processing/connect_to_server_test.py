from connect_to_database import Mongo
import os
from dotenv import load_dotenv
from sshtunnel import SSHTunnelForwarder
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
# SSH server configuration
ssh_private_key = 'C:\\Users\\Admin\\Desktop\\Team Software\\CS3305-CHOMG\\server_webcam_processing\\key'


# Create an SSH tunnel
with SSHTunnelForwarder(
    (SSH_HOST, int(SSH_PORT)),
    ssh_username=SSH_USERNAME,
    ssh_pkey=ssh_private_key,
    ssh_password = None,
    remote_bind_address=('localhost', int(MONGO_PORT))
) as tunnel:
    

    # Connect to MongoDB through the tunnel with authentication
    mongo_connection = Mongo(MONGO_HOST, MONGO_USERNAME, MONGO_PASSWORD, tunnel.local_bind_port, MONGO_DATABASE, MONGO_COLLECTION)
    mongo_connection.open_connection()
    # Define the filter to find the document you want to update
    get_user= {'username': CHOMG_USERNAME}
    # Define the update operation to append to the 'videos' array or create it
    add_video = {'$addToSet': {'videos': 'test.mp4'}}
    mongo_connection.add_video_in_users_collection(get_user, add_video)
    mongo_connection.close_connection()
