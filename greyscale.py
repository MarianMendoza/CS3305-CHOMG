# Based on tutorial - https://hackthedeveloper.com/motion-detection-opencv-python/
import cv2
def getFrameInGreyScale(frame):
    '''
    Convert the frame to grayscale
    '''
    return cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
