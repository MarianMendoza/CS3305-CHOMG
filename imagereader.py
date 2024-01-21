# Tutorial https://www.geeksforgeeks.org/python-opencv-capture-video-from-camera/?ref=lbp

#######
# How to run in new file:
    # import CS3305.readWebCam as rWC 

    # video = rWC.readVideoFromCamera()
    # rWC.displayVideo(video)
########

# import the opencv library 
import cv2 

def readVideoFromCamera():
    # define a video capture object 
    video= cv2.VideoCapture(0) 
    return video
    
def captureFrame(video):
    frameReturnedSuccessfully, frame = video.read() 
    return frame

def displayVideo(video):
    while(True): 
        frame = captureFrame(video)
        displayFrame(frame)
        if userExitRequest():
            break
    stopReading(video)

def displayFrame(frame, nameOfFrame=None):
    if nameOfFrame == None:
        nameOfFrame =""
    cv2.imshow(nameOfFrame, frame) 
    
def userExitRequest():
    '''Return True if user presses q'''
    if cv2.waitKey(1)  == ord('q'): 
        return True
    
def stopReading(video):
    '''
    After the loop release the cap object and destroy all the windows 
    '''
    video.release() 
    cv2.destroyAllWindows() 
