import cv2

def createBackgroundSubtractor():
    '''
    Creates object which distinguishes foreground from background of images using mog algorithm\n
    Works more efficiently with grey-scale frames
    '''
    return cv2.createBackgroundSubtractorMOG2()

def getForegroundOfFrame(greyscaleFrame, subtractor):
    return subtractor.apply(greyscaleFrame)