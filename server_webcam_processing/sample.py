import image_reader
import video_frame_handling
# import display
import record_on_movement

def run():
    # display.turn_off_monitor_display()
    frame_handler=video_frame_handling.VideoFrameHandler()
    while True:
        contours = frame_handler.get_contours_of_current_frame()
        is_movement_detected = bool(contours)
        if contours:
            frame_handler.handle_motion_detection_in_frame_using_contours(contours)
            record_on_movement.record_on_movement(frame_handler.current_frame, is_movement_detected)

        frame_handler.display_current_frame()
        frame_handler.display_foreground()

        frame_handler.set_next_frame_as_current()
        frame_handler.set_adjusted_foreground_of_current_frame()

        if image_reader.user_exit_request():
            frame_handler.stop_reading()
            record_on_movement.cleanup()  # Ensure resources are properly released
            break

if __name__ == "__main__":
    run()