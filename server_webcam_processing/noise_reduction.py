import cv2

def create_noise_reduction_kernel():
    '''
    Creates a kernel in the shape of an ellipse of size 3 up and across to help remove noise in the frame
    '''
    return cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (3, 3))

def erode_frame_using_kernel(frame, kernel):
    return cv2.erode(frame, kernel)

def dilate_frame_using_kernel(frame, kernel):
    return cv2.dilate(frame, kernel)