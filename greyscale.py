# Based on tutorial - https://hackthedeveloper.com/motion-detection-opencv-python/
import cv2
def get_frame_in_grey_scale(frame):
    '''
    Convert the frame to grayscale
    '''
    return cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
