import image_reader
import video_frame_handling
# import display
import record_on_movement

# Run following in terminal to convert folder to .exe
# pyinstaller --onefile     --add-data "image_reader.py;."     --add-data "background_seperator.py;."     --add-data "bounding_box.py;."     --add-data "colour_handling.py;."     --add-data "haarcascade_fullbody.xml;."     --add-data "crop_frame.py;."     --add-data "entity_recognition.py;."     --add-data "noise_reduction.py;."     --add-data "record_on_movement.py;."     --add-data "video_frame_handling.py;."     --hidden-import cv2  --hidden-import os --hidden-import sys   sample.py
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

        if image_reader.user_exit_request():
            frame_handler.stop_reading()
            record_on_movement.cleanup()  # Ensure resources are properly released
            break

if __name__ == "__main__":
    run()