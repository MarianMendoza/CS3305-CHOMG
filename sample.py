import grey_scale 
import image_reader 
import background_seperator
import noise_reduction
import bounding_box
import entity_recognition 
import crop_frame
# import display
# import recordWhenMovement

def run():
    video = image_reader.read_video_from_camera()
    separator = background_seperator.create_background_subtractor_object()
    kernel = noise_reduction.create_noise_reduction_kernel()
    # display.turn_off_monitor_display()
    while True:
        frame, foreground_of_frame = handle_video_frame(video, separator, kernel)

        contours = bounding_box.get_contours(foreground_of_frame)
        if contours:
            frame = handle_motion_detection_in_frame_using_contours(contours, frame)

        image_reader.display_frame(frame, "bg")
        image_reader.display_frame(foreground_of_frame, "fg")
        # recordWhenMovement.record_on_movement(video, separator, kernel)  # Record when movement

        if image_reader.user_exit_request():
            image_reader.stop_reading(video)
            break



def handle_video_frame(video, separator, kernel):
        frame = image_reader.capture_frame(video)
        grey_scale_frame = grey_scale.get_frame_in_grey_scale(frame)
        foreground_of_frame = background_seperator.get_foreground_of_frame_using_subtractor_object(grey_scale_frame, separator)
        foreground_of_frame = noise_reduction.erode_frame_using_kernel(foreground_of_frame, kernel)
        foreground_of_frame = noise_reduction.dilate_frame_using_kernel(foreground_of_frame, kernel)
        return frame, foreground_of_frame

def handle_motion_detection_in_frame_using_contours(contours, frame):
            max_contour = bounding_box.get_max_contour(contours)
            approximate_polygonal_curve = bounding_box.get_approximate_curve_from_contour(max_contour)

            bounding_box_coordinates = bounding_box.get_bounding_box_from_curve(approximate_polygonal_curve)

            cropped_frame = crop_frame.crop_frame_to_bounding_box(frame, bounding_box_coordinates)
            
            if entity_recognition.is_person_detected_in_frame(cropped_frame):
                print(True)

            bounding_box.draw_bounding_box_on_frame(frame, bounding_box_coordinates)
            return frame

if __name__ == "__main__":
    run()