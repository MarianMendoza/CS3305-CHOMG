# Tutorial https://www.geeksforgeeks.org/python-opencv-capture-video-from-camera/?ref=lbp

# import the opencv library 
import cv2 

def readVideoFromCamera():
    # define a video capture object 
    video = cv2.VideoCapture(0) 
    
def captureFrame(video):
    frameReturnedSuccessfully, frame = video.read() 
    return frame

def displayFrame(frame):
    cv2.imshow('frame', frame) 
    
def stopReading(video):
    '''
    After the loop release the cap object and destroy all the windows 
    '''
    video.release() 
    cv2.destroyAllWindows() 

def userExitRequest():
    '''Return True if user presses q'''
    if cv2.waitKey(1) & 0xFF == ord('q'): 
        return True

def displayVideo(video):
    while(True): 
        frame = captureFrame(video)
        displayFrame(frame)
        if userExitRequest():
            break
