from connect_to_server import CloudServer
from connect_to_database import Mongo
import os
from dotenv import load_dotenv
from pymongo import MongoClient
load_dotenv()

MONGO_USERNAME = os.getenv('MONGO_USERNAME')
MONGO_PASSWORD = os.getenv('MONGO_PASSWORD')
MONGO_HOST = os.getenv('MONGO_HOST')
MONGO_PORT = os.getenv('MONGO_PORT')
MONGO_DATABASE = os.getenv('MONGO_DATABASE')
SSH_USERNAME = os.getenv('SSH_USERNAME')
SSH_PASSWORD = os.getenv('SSH_PASSWORD')
SSH_HOST = os.getenv('SSH_HOST')
SSH_PORT = os.getenv('SSH_PORT')


server = CloudServer(SSH_USERNAME, SSH_PASSWORD, SSH_HOST, SSH_PORT)
try:
    server.connect_to_server()
    # client = Mongo(MONGO_HOST, MONGO_USERNAME, MONGO_PASSWORD, "AppUsers", "Sample")
    # client.open_connection()

    # print(client.get_all_database_names())

    # client.close_connection()

    
    # MongoDB Connection
    mongo_connection_string = f"mongodb://{MONGO_USERNAME}:{MONGO_PASSWORD}@{MONGO_HOST}:{MONGO_PORT}/{MONGO_DATABASE}"
    client = MongoClient(mongo_connection_string)
    db = client[MONGO_DATABASE]

    # Now you can perform MongoDB operations
    collection = db['your_collection']
    result = collection.insert_one({'key': 'value'})
    print(result)

finally:
    # Close the SSH connection
    server.close_connection()
