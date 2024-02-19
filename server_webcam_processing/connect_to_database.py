# Reference - https://www.w3schools.com/python/python_mongodb_getstarted.asp

from pymongo import MongoClient

class Mongo(object):
    def __init__(self, hostname: str, username: str, password: str, port: int, database_name: str, collection_name: str) -> None:
        self.hostname = hostname
        self.username = username
        self.password = password
        self.port = port
        self.client = None
        self.database_name = database_name
        self.collection_name = collection_name
        
    def open_connection(self):
        mongo_uri = f"mongodb://{self.username}:{self.password}@{self.hostname}:{self.port}"
        client = MongoClient(mongo_uri)
        self.client = client

    def get_all_database_names(self):
        return self.client.list_database_names()

    def get_database_by_name(self):
        return self.client[self.database_name]
    
    def set_database_name(self, database_name: str):
        self.database_name = database_name
        
    def set_collection_name(self, collection_name: str):
        self.collection_name = collection_name
    def create_collection_in_database(self, collection_name: str):
        self.client[self.database_name][collection_name]

    def get_all_collection_names_in_database(self):
        return self.get_database_by_name().list_collection_names()

    def get_collection_in_database_by_name(self):
        return self.client[self.database_name][self.collection_name]

    def add_video_in_users_collection(self, user_filter, add_video_command):
        self.client[self.database_name][self.collection_name].update_one(user_filter, add_video_command)

    def query_database_by_date(self, date: str):
        date_query = { "__id": f'{date}' }
        return self.client[self.database_name][self.collection_name].find(date_query)

    def close_connection(self):
        self.client.close()
        self.client = None