import image_reader
import background_seperator
import noise_reduction
import bounding_box
import entity_recognition 
import crop_frame
import colour_handling

class VideoFrameHandler(object):
    def __init__(self) -> None:
        self.video = image_reader.read_video_from_camera()
        self.separator = background_seperator.create_background_subtractor_object()
        self.kernel = noise_reduction.create_noise_reduction_kernel()
        self.current_frame = image_reader.capture_frame(self.video)
        self.current_foreground_of_frame = self.set_adjusted_foreground_of_current_frame()
    
    def get_adjusted_foreground_of_current_frame(self):
        return self.current_foreground_of_frame
    

    # TODO: Maybe clean this function
    def set_adjusted_foreground_of_current_frame(self):
        grey_scale_frame = colour_handling.get_frame_in_grey_scale(self.current_frame)
        foreground_of_frame = background_seperator.get_foreground_of_frame_using_subtractor_object(grey_scale_frame, self.separator)
        foreground_of_frame = noise_reduction.erode_frame_using_kernel(foreground_of_frame, self.kernel)
        foreground_of_frame = noise_reduction.dilate_frame_using_kernel(foreground_of_frame, self.kernel)
        self.current_foreground_of_frame = foreground_of_frame

    def get_current_frame(self):
        return self.current_frame
    
    # TODO: Clean this function
    def handle_motion_detection_in_frame_using_contours(self, contours):
        max_contour = bounding_box.get_max_contour(contours)
        approximate_polygonal_curve = bounding_box.get_approximate_curve_from_contour(max_contour)
        bounding_box_coordinates = bounding_box.get_bounding_box_from_curve(approximate_polygonal_curve)
        cropped_frame = crop_frame.crop_frame_to_bounding_box(self.current_frame, bounding_box_coordinates)
        if entity_recognition.is_person_detected_in_frame(cropped_frame):
            print(True)
        bounding_box.draw_bounding_box_on_frame(self.current_frame, bounding_box_coordinates)
    
    def get_contours_of_current_frame(self):
        return bounding_box.get_contours(self.current_foreground_of_frame)

    def display_current_frame(self):
        image_reader.display_frame(self.current_frame, "Full Frame")

    def display_foreground(self):
        image_reader.display_frame(self.current_foreground_of_frame, "Background Of Frame")

    def stop_reading(self):
        image_reader.stop_reading(self.video)

    def set_next_frame_as_current(self):
        self.current_frame = image_reader.capture_frame(self.video)