import image_reader
import video_frame_handling
import record_on_movement
import bounding_box

def run():
    frame_handler = video_frame_handling.VideoFrameHandler()
    try:
        while True:
            contours = frame_handler.get_contours_of_current_frame()
            
            # Determine if significant movement is detected based on max_contour
            max_contour = bounding_box.get_max_contour(contours, min_area=1000)  # Adjust min_area as needed
            is_movement_detected = max_contour is not None
            
            if contours and is_movement_detected:
                frame_handler.handle_motion_detection_in_frame_using_contours(contours)
            
            # Record based on significant movement detection
            record_on_movement.record_on_movement(frame_handler.current_frame, is_movement_detected)
            
            # Displaying the current frame and optional foreground
            frame_handler.display_current_frame()
            # frame_handler.display_foreground()
            
            # Prepare for the next frame
            frame_handler.set_next_frame_as_current()
            frame_handler.set_adjusted_foreground_of_current_frame()
            
            if image_reader.user_exit_request():
                break
    finally:
        # Cleanup and release resources on exit
        record_on_movement.cleanup()

if __name__ == "__main__":
    run()
