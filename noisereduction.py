import cv2

def createNoiseReductionKernel():
    '''
    Creates a kernel in the shape of an ellipse of size 10 up and across to help remove noise in the frame
    '''
    return cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (3, 3))

def erodeFrameUsingKernel(frame, kernel):
    return cv2.erode(frame,kernel)

def dilateFrameUsingKernel(frame, kernel):
    return cv2.dilate(frame, kernel)