import requests

class Server_Poster(object):
    def __init__(self, ip_address: str, port: int) -> None:
        '''
        Initialized with ip_address and port number
        '''
        self.url = f"https://{ip_address}/send-notification"

    def post_to_server(self, dic_of_data):
        #  Post data to endpoint

        # cert = ("server.crt", "server.key")
        requests.post(url=self.url, verify=False, json=dic_of_data)
