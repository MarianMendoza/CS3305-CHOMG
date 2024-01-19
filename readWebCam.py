# Tutorial https://www.geeksforgeeks.org/python-opencv-capture-video-from-camera/?ref=lbp

# import the opencv library 
import cv2 

def readVideoFromCamera():
    # define a video capture object 
    video = cv2.VideoCapture(0) 
    # if not video.isOpened():
    #     print("Error: Could not open webcam.")
    #     exit()
    while(True): 
        frame = captureFrame(video)
        displayFrame(frame)
        if userExitRequest():
            break
    stopReading(video)

def displayFrame(frame):
    cv2.imshow('frame', frame) 
        
def captureFrame(video):
    frameReturnedSuccessfully, frame = video.read() 
    return frame

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

readVideoFromCamera()