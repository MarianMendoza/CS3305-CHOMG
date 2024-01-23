import cv2

def createBackgroundSubtractor():
    '''
    Creates object which distinguishes foreground from background of images using mog algorithm\n
    Works more efficiently with grey-scale frames
    '''
    return cv2.createBackgroundSubtractorMOG2()

def getForegroundOfFrame(greyscaleFrame, subtractor):
    return subtractor.apply(greyscaleFrame)

def recolourForegroundUsingOriginalFrame(foregroundOfFrame, frame):
    return cv2.bitwise_and(frame, frame, mask=foregroundOfFrame)