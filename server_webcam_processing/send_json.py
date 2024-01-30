# Reference - https://reqbin.com/code/python/m2g4va4a/python-requests-post-json-example
import requests

class JsonSender(object):

    def __init__(self, destination_ip_address: str) -> None:
        self.destination_ip_address = destination_ip_address

    def send_is_motion_detected(self, motion_detected: bool, person_detected: bool, connection_lost: bool, program_terminated: bool):
        requests.post(self.destination_ip_address, (motion_detected, person_detected , connection_lost , program_terminated ))

    # Reference
    def get_user_information(self):
        response_data = requests.get(self.destination_ip_address)
        return ...