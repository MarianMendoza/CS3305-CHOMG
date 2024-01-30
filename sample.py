import image_reader
import video_frame_handling
# import display
# import recordWhenMovement

def run():
    # display.turn_off_monitor_display()
    frame_handler=video_frame_handling.VideoFrameHandler()
    while True:
        contours = frame_handler.get_contours_of_current_frame()
        if contours:
            frame_handler.handle_motion_detection_in_frame_using_contours(contours)

        
        frame_handler.display_current_frame()
        frame_handler.display_foreground()
        frame_handler.set_next_frame_as_current()
        # recordWhenMovement.record_on_movement(video, separator, kernel)  # Record when movement

        if image_reader.user_exit_request():
            frame_handler.stop_reading()
            break

if __name__ == "__main__":
    run()