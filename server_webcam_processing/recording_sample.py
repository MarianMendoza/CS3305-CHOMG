import image_reader
import video_frame_handling
import record_on_movement
import bounding_box
import linked_list_file_saver
def run():
    frame_handler = video_frame_handling.VideoFrameHandler()
    frame_recorder = record_on_movement.Recorder(frame_handler.get_current_frame())
    linked_list = linked_list_file_saver.LinkedList()
    try:
        while True:
            contours = frame_handler.get_contours_of_current_frame()
            
            # Determine if significant movement is detected based on max_contour
            max_contour = bounding_box.get_max_contour(contours, min_area=1000)  # Adjust min_area as needed
            is_movement_detected = max_contour is not None
            
            if is_movement_detected:
                if not frame_recorder.is_recording():
                    if not linked_list.is_empty():
                        for frame in linked_list.get_list_of_frames_in_linked_list():
                            frame_recorder.record_frame(frame)
                        linked_list.clear_linked_list()
                else:
                    frame_handler.handle_motion_detection_in_frame_using_contours(contours)
                    # Record based on significant movement detection
                    frame_recorder.record_frame(frame_handler.get_current_frame())
            
            else:
                frame_recorder.stop_recording_if_time_elapsed(frame_handler.get_current_frame())
            # Displaying the current frame and optional foreground
            frame_handler.display_current_frame()
            # frame_handler.display_foreground()
            
            if not frame_recorder.is_recording():
                # Save frame in linked list
                linked_list.add_frame(frame_handler.get_current_frame())

            # Prepare for the next frame
            frame_handler.set_next_frame_as_current()
            
            if image_reader.user_exit_request():
                break
    finally:
        # Cleanup and release resources on exit
        frame_recorder.cleanup()

if __name__ == "__main__":
    run()
