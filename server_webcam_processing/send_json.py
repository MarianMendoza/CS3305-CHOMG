# Reference - https://reqbin.com/code/python/m2g4va4a/python-requests-post-json-example
import jwt
from datetime import datetime, timedelta

class JsonCreator(object):
    def __init__(self, username, key) -> None:
        self.username = username
        self.__key = key

    def generate_jwt(self, motion_detected: bool, person_detected: bool):
        # Set expiration time for the token (e.g., 1 hour)
        expiration_time = datetime.utcnow() + timedelta(minutes=5)
        
        # Create token payload
        payload = {
            "user_id": self.username,
            "motion_detected": motion_detected,
            "person_detected": person_detected,
            "exp": expiration_time
        }
        
        # Generate JWT token
        token = jwt.encode(payload, self.__key, algorithm="HS256")
        
        return token