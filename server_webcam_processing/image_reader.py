# Tutorial https://www.geeksforgeeks.org/python-opencv-capture-video-from-camera/?ref=lbp

# import the opencv library 
import cv2 

def read_video_from_camera():
    # define a video capture object 
    video = cv2.VideoCapture(0) 
    return video
    
def capture_frame(video):
    frame_returned_successfully, frame = video.read() 
    return frame

def display_video(video):
    while(True): 
        frame = capture_frame(video)
        display_frame(frame)
        if user_exit_request():
            break
    stop_reading(video)

def display_frame(frame, name_of_frame = None):
    if name_of_frame == None:
        name_of_frame = ""
    cv2.imshow(name_of_frame, frame) 
    
def user_exit_request():
    '''Return True if user presses q'''
    if cv2.waitKey(1)  == ord('q'): 
        return True
    
def stop_reading(video):
    '''
    After the loop release the cap object and destroy all the windows 
    '''
    video.release() 
    cv2.destroyAllWindows() 
