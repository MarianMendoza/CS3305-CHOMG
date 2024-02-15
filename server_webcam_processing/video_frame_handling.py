# Tutorial https://www.geeksforgeeks.org/python-opencv-capture-video-from-camera/?ref=lbp
# tutorial https://www.geeksforgeeks.org/detect-an-object-with-opencv-python/
# xml file - https://github.com/anaustinbeing/haar-cascade-files/blob/master/haarcascade_fullbody.xml

import image_reader
import background_seperator
import noise_reduction
import bounding_box
import crop_frame
import colour_handling
import cv2
import os 
import sys


class VideoFrameHandler(object):
    def __init__(self) -> None:
        self.__video = image_reader.read_video_from_camera()
        self.__separator = background_seperator.create_background_subtractor_object()
        self.kernel = noise_reduction.create_noise_reduction_kernel()
        self.current_frame = image_reader.capture_frame(self.__video)
        self.__set_adjusted_foreground_of_current_frame()
        self.__current_foreground_of_frame = self.get_adjusted_foreground_of_current_frame()
        self.__xml_data_for_human_detection = cv2.CascadeClassifier(os.path.join(sys._MEIPASS, 'haarcascade_fullbody.xml')) if getattr(sys, 'frozen', False) else cv2.CascadeClassifier('haarcascade_fullbody.xml')
        self.__set_contours_of_current_frame()
        self.__set_max_contour_of_current_frame()
        self.__set_movement_detected()

    def get_adjusted_foreground_of_current_frame(self):
        return self.__current_foreground_of_frame
    
    def __set_adjusted_foreground_of_current_frame(self):
        '''
        Convert the frame to subtract the background only displaying movement in white
        '''
        grey_scale_frame = colour_handling.get_frame_in_grey_scale(self.current_frame)
        foreground_of_frame = background_seperator.get_foreground_of_frame_using_subtractor_object(grey_scale_frame, self.__separator)
        foreground_of_frame = noise_reduction.erode_frame_using_kernel(foreground_of_frame, self.kernel)
        foreground_of_frame = noise_reduction.dilate_frame_using_kernel(foreground_of_frame, self.kernel)
        self.__current_foreground_of_frame = foreground_of_frame

    def get_current_frame(self):
        return self.current_frame
    
    
    def handle_motion_detection_in_frame_using_contours(self):
        '''
        Detect if a person is in the frames motion and draws box around motion in frame
        '''
        approximate_polygonal_curve = bounding_box.get_approximate_curve_from_contour(self.max_contour)
        bounding_box_coordinates = bounding_box.get_bounding_box_from_curve(approximate_polygonal_curve)
        cropped_frame = crop_frame.crop_frame_to_bounding_box(self.current_frame, bounding_box_coordinates)

        if self.__is_person_detected_in_cropped_frame(cropped_frame):
            self.__update_message_sent_to_phone()

        bounding_box.draw_bounding_box_on_frame(self.current_frame, bounding_box_coordinates)
    
    def __update_message_sent_to_phone(self):
            '''
            Sends a json to the phone for notification purposes
            '''
            print(True)

    def get_contours_of_current_frame(self):
        return self.contours_of_current_frame

    def __set_contours_of_current_frame(self):
        self.contours_of_current_frame = bounding_box.get_contours(self.__current_foreground_of_frame)
        

    def __set_max_contour_of_current_frame(self):
        self.max_contour = bounding_box.get_max_contour(self.contours_of_current_frame, min_area=1000) 

    def __set_movement_detected(self):
        if self.max_contour is not None:
            self.movement_detected = True
        else:
            self.movement_detected = False

    def get_max_contour_of_current_frame(self):
        return self.max_contour
        
    def __is_person_detected_in_cropped_frame(self, cropped_frame):
        '''
        Takes cropped frame as input
        Returns True if person detected in frame otherwise returns False
        '''
        rgb_frame = colour_handling.get_frame_in_rgb(cropped_frame) 
        grey_scale_frame = colour_handling.get_frame_in_grey_scale(rgb_frame)
        list_of_people_detected_in_frame = self.__xml_data_for_human_detection.detectMultiScale(grey_scale_frame)
    
        # Return False if no people detected else return True
        return len(list_of_people_detected_in_frame) != 0


    def display_current_frame(self):
        image_reader.display_frame(self.current_frame, "Full Frame")

    def display_foreground(self):
        image_reader.display_frame(self.__current_foreground_of_frame, "Background Of Frame")

    def stop_reading(self):
        image_reader.stop_reading(self.__video)

    def set_next_frame_as_current(self):
        '''
        Sets the next frame in the display as the current and updates variables associated with it.
        '''
        self.current_frame = image_reader.capture_frame(self.__video)
        self.__set_adjusted_foreground_of_current_frame()
        self.__set_contours_of_current_frame()
        self.__set_max_contour_of_current_frame()
        self.__set_movement_detected()
        
    def is_movement_detected(self):
        return self.movement_detected
    
    def stop_detecting(self):
        return image_reader.user_exit_request()