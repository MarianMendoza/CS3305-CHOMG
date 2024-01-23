import cv2

def get_frame_in_rgb(frame):
    '''
    Convert the frame to RGB
    '''
    return cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)