import cv2
import time
import os
import datetime
from greyscale import *
from imagereader import *
from backgroundseperator import *
from noisereduction import *
from boundingbox import *
from entityrecognition import *


def run():
    video = read_video_from_camera()
    if not video.isOpened():
        raise IOError("Cannot open webcam")

    separator = create_background_subtractor_object()
    kernel = create_noise_reduction_kernel()

    # Adjust this value to ignore smaller movements
    min_contour_area = 5000  # Adjust based on your specific needs

    # Additional parameters for continuous movement detection
    movement_frames_threshold = 5  # Number of consecutive frames with significant movement to start recording
    movement_frames_count = 0      # Counter for consecutive frames with significant movement

    # Setup for recording
    frame_width = int(video.get(cv2.CAP_PROP_FRAME_WIDTH))
    frame_height = int(video.get(cv2.CAP_PROP_FRAME_HEIGHT))
    frame_rate = 20
    recording = False
    last_movement_time = None
    record_duration_after_movement = 7
    recordings_dir = "Recordings"
    if not os.path.exists(recordings_dir):
        os.makedirs(recordings_dir)
    fourcc = cv2.VideoWriter_fourcc(*'avc1')
    out = None

    while True:
        frame = capture_frame(video)
        greyScaleFrame = get_frame_in_grey_scale(frame)
        foregroundOfFrame = get_foreground_of_frame_using_subtractor_object(greyScaleFrame, separator)
        foregroundOfFrame = erode_frame_using_kernel(foregroundOfFrame, kernel)
        foregroundOfFrame = dilate_frame_using_kernel(foregroundOfFrame, kernel)

        contours = get_contours(foregroundOfFrame)
        significant_movement_detected = False

        for contour in contours:
            if cv2.contourArea(contour) > min_contour_area:
                significant_movement_detected = True
                maxContour = get_max_contour([contour])  # Assuming getMaxContour expects a list of contours
                approxPolygonalCurve = get_approximate_curve_from_contour(maxContour)
                boundingRectangleCoordinates = get_bounding_box_from_curve(approxPolygonalCurve)
                draw_bounding_box_on_frame(frame, boundingRectangleCoordinates)
                break  # Break after finding the first significant movement

        if significant_movement_detected:
            movement_frames_count += 1
        else:
            movement_frames_count = 0  # Reset counter if no significant movement

        # Check if the number of consecutive frames with significant movement is enough to start recording
        if movement_frames_count >= movement_frames_threshold and not recording:
            # Start recording
            last_movement_time = time.time()
            timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = os.path.join(recordings_dir, f"{timestamp}.mp4")
            out = cv2.VideoWriter(filename, fourcc, frame_rate, (frame_width, frame_height))
            recording = True

        if recording:
            out.write(frame)
            if not significant_movement_detected and time.time() - last_movement_time > record_duration_after_movement:
                recording = False
                out.release()
                out = None

        display_frame(frame, "Camera Output")

        if user_exit_request():
            break

    stop_reading(video)
    if out:
        out.release()

if __name__ == "__main__":
    run()