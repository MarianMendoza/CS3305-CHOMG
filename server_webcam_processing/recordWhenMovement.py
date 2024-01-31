import cv2
import time
import os
import datetime
import image_reader
import colour_handling
import background_seperator
import noise_reduction
import bounding_box

def record_on_movement(video, separator, kernel):
    # Check if video capture is successfully opened
    if not video.isOpened():
        raise IOError("Cannot open webcam")

    # Define the minimum area of contours to be considered for movement
    min_contour_area = 5000  # Adjust this value based on your specific needs

    # Parameters for detecting continuous movement
    movement_frames_threshold = 5  # Number of consecutive frames with significant movement required to start recording
    movement_frames_count = 0      # Counter for consecutive frames with significant movement

    # Recording setup
    frame_width = int(video.get(cv2.CAP_PROP_FRAME_WIDTH))  # Video frame width
    frame_height = int(video.get(cv2.CAP_PROP_FRAME_HEIGHT))  # Video frame height
    frame_rate = 20  # Frame rate for recording
    recording = False  # Flag to indicate if recording is currently happening
    last_movement_time = None  # Time of the last movement detected
    record_duration_after_movement = 7  # Duration to keep recording after the last movement
    recordings_dir = "Recordings"  # Directory to save recordings
    if not os.path.exists(recordings_dir):
        os.makedirs(recordings_dir)  # Create the directory if it doesn't exist
    fourcc = cv2.VideoWriter_fourcc(*'avc1')  # Codec for video writing
    out = None  # VideoWriter object

    # Main loop for processing video frames
    while True:
        frame = image_reader.capture_frame(video)  # Capture a frame from the video
        greyScaleFrame = colour_handling.get_frame_in_grey_scale(frame)  # Convert frame to grayscale
        foregroundOfFrame = background_seperator.get_foreground_of_frame_using_subtractor_object(greyScaleFrame, separator)  # Separate foreground
        foregroundOfFrame = noise_reduction.erode_frame_using_kernel(foregroundOfFrame, kernel)  # Erode the foreground to reduce noise
        foregroundOfFrame = noise_reduction.dilate_frame_using_kernel(foregroundOfFrame, kernel)  # Dilate the foreground to restore eroded parts

        contours = bounding_box.get_contours(foregroundOfFrame)  # Get contours from the foreground
        significant_movement_detected = False  # Flag to indicate significant movement

        # Loop through contours to find significant movements
        for contour in contours:
            if cv2.contourArea(contour) > min_contour_area:
                significant_movement_detected = True  # Set flag if significant movement is found
                maxContour = bounding_box.get_max_contour([contour])  # Get the largest contour
                approxPolygonalCurve = bounding_box.get_approximate_curve_from_contour(maxContour)  # Approximate the contour curve
                boundingRectangleCoordinates = bounding_box.get_bounding_box_from_curve(approxPolygonalCurve)  # Get bounding box for the contour
                bounding_box.draw_bounding_box_on_frame(frame, boundingRectangleCoordinates)  # Draw bounding box on the frame
                break  # Exit loop after finding the first significant movement

        # Update movement frame count based on detection
        if significant_movement_detected:
            movement_frames_count += 1
        else:
            movement_frames_count = 0  # Reset counter if no significant movement

        # Check if enough consecutive frames with movement are detected to start recording
        if movement_frames_count >= movement_frames_threshold and not recording:
            last_movement_time = time.time()  # Update the last movement time
            timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")  # Generate timestamp for filename
            filename = os.path.join(recordings_dir, f"{timestamp}.mp4")  # Create filename with path
            out = cv2.VideoWriter(filename, fourcc, frame_rate, (frame_width, frame_height))  # Initialize VideoWriter
            recording = True  # Start recording

        # Write the frame to the file if recording
        if recording:
            out.write(frame)  # Write frame to video file
            # Stop recording if no significant movement detected for a specified duration
            if not significant_movement_detected and time.time() - last_movement_time > record_duration_after_movement:
                recording = False  # Stop recording
                out.release()  # Release the VideoWriter object
                out = None  # Reset the VideoWriter object

        image_reader.display_frame(frame, "Camera Output")  # Display the processed frame

        # Check for user request to exit
        if image_reader.user_exit_request():
            break  # Exit the loop if user requested to stop

    image_reader.stop_reading(video)  # Stop reading from the video
