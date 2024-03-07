import requests
import jwt

class Server_Poster(object):
    def __init__(self, ip_address: str, port: int) -> None:
        '''
        Initialized with ip_address and port number
        '''
        self.url = f"https://{ip_address}/send-notification"

    def post_to_server(self, secret_key, dic_of_data):

        # Generate the JWT
        token = jwt.encode(payload=dic_of_data, key=secret_key, algorithm='HS256')

        headers = {
            'Content-Type': 'application/json',
            'Authorization': f'Bearer {token}'
        }
        requests.post(url=self.url, headers=headers, json=dic_of_data, verify="server.crt")