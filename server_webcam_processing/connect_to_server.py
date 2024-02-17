import paramiko

class CloudServer(object):
    def __init__(self, username: str, password: str, host: str, port: int) -> None:
        # SSH Connection Details
        self.username = username
        self.password = password
        self.host = host
        self.port = port
        self.ssh_connection = None

    def connect_to_server(self):
                
        # Setup connection
        self.ssh_connection = paramiko.SSHClient()
        self.ssh_connection.set_missing_host_key_policy(paramiko.AutoAddPolicy())

        # Attempt connection
        self.ssh_connection.connect(self.host, port=self.port, username=self.username, password=self.password)

    def close_connection(self):
        self.ssh_connection.close()
        self.ssh_connection = None