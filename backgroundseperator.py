import cv2

def create_background_subtractor_object():
    '''
    Creates object which distinguishes foreground from background of images using mog algorithm\n
    Works more efficiently with grey-scale frames
    '''
    return cv2.createBackgroundSubtractorMOG2()

def get_foreground_of_frame_using_subtractor_object(frame, subtractor):
    return subtractor.apply(frame)

def recolour_foreground_using_original_frame(foreground_of_frame, frame): #https://www.youtube.com/watch?v=YSLVAxgclCo
    return cv2.bitwise_and(frame, frame, mask=foreground_of_frame)