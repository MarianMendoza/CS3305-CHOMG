# tutorial https://www.geeksforgeeks.org/detect-an-object-with-opencv-python/
# xml file - https://github.com/anaustinbeing/haar-cascade-files/blob/master/haarcascade_fullbody.xml
import cv2
import greyscale as gs
import rgbframe as rgb

def isPersonDetected(frame):
    '''
    Takes frame as input
    Returns True if person detected in frame otherwise returns False
    '''
    rgbFrame = rgb.getFrameInRGB(frame) 
    greyScaleFrame = gs.getFrameInGreyScale(rgbFrame)

    xmlDataForHumanDetection = cv2.CascadeClassifier('CS3305/haarcascade_fullbody.xml')

    found = xmlDataForHumanDetection.detectMultiScale(greyScaleFrame)
    
    # Return False if no people detected else return True
    return len(found) == 0