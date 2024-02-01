# tutorial https://www.geeksforgeeks.org/detect-an-object-with-opencv-python/
# xml file - https://github.com/anaustinbeing/haar-cascade-files/blob/master/haarcascade_fullbody.xml
import cv2
import colour_handling
import os 
import sys

def is_person_detected_in_frame(frame):
    '''
    Takes frame as input
    Returns True if person detected in frame otherwise returns False
    '''
    rgb_frame = colour_handling.get_frame_in_rgb(frame) 
    grey_scale_frame = colour_handling.get_frame_in_grey_scale(rgb_frame)
    
    xml_data_for_human_detection = cv2.CascadeClassifier(os.path.join(sys._MEIPASS, 'haarcascade_fullbody.xml')) if getattr(sys, 'frozen', False) else cv2.CascadeClassifier('haarcascade_fullbody.xml')


    list_of_people_detected_in_frame = xml_data_for_human_detection.detectMultiScale(grey_scale_frame)
 

    # Return False if no people detected else return True
    return len(list_of_people_detected_in_frame) != 0