import cv2
def getFrameInRGB(frame):
    '''
    Convert the frame to RGB
    '''
    return cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
