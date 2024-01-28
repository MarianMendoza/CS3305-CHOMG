from pymongo import MongoClient

class Mongo(object):
    def __init__(self, hostname, username, password) -> None:
        self.hostname = hostname
        self.username = username
        self.password = password
        self.client = ""
        
    def open_connection(self):
        hostname= self.hostname
        port = 27017  
        username = self.username
        password =  self.password
        client = MongoClient(hostname, port, username=username, password=password)
        self.client = client

    def get_database_by_name(self, database_name:str):
        return self.client[database_name]

    def get_collection_in_database_by_name(self, database_name : str, collection_name : str):
        return self.client[database_name][collection_name]

    def close_connection(self):
        self.client.close()
        self.client = ""