# Reference - https://www.w3schools.com/python/python_mongodb_getstarted.asp

from pymongo import MongoClient

class Mongo(object):
    def __init__(self, hostname: str, username: str, password: str) -> None:
        self.hostname = hostname
        self.username = username
        self.password = password
        self.client = None
        
    def open_connection(self):
        hostname = self.hostname
        port = 27017  
        username = self.username
        password =  self.password
        client = MongoClient(hostname, port, username=username, password=password)
        self.client = client

    def get_all_database_names(self):
        return self.client.list_database_names()

    def get_database_by_name(self, database_name: str):
        return self.client[database_name]
    
    def create_collection_in_database(self, collection_name: str, database_name: str):
        self.client[collection_name][database_name]

    def get_all_collection_names_in_database(self, database_name: str):
        return self.get_database_by_name(database_name).list_collection_names()

    def get_collection_in_database_by_name(self, database_name: str, collection_name: str):
        return self.client[database_name][collection_name]

    def close_connection(self):
        self.client.close()
        self.client = None