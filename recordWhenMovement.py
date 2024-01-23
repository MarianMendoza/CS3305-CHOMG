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
    video = readVideoFromCamera()
    if not video.isOpened():
        raise IOError("Cannot open webcam")

    separator = createBackgroundSubtractor()
    kernel = createNoiseReductionKernel()

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
        frame = captureFrame(video)
        greyScaleFrame = getFrameInGreyScale(frame)
        foregroundOfFrame = getForegroundOfFrame(greyScaleFrame, separator)
        foregroundOfFrame = erodeFrameUsingKernel(foregroundOfFrame, kernel)
        foregroundOfFrame = dilateFrameUsingKernel(foregroundOfFrame, kernel)

        contours = getContours(foregroundOfFrame)
        movement_detected = False
        if contours:
            movement_detected = isPersonDetected(frame)
            maxContour = getMaxContour(contours)
            approxPolygonalCurve = getApproximateCurve(maxContour)
            boundingRectangleCoordinates = getBoundingRectangleCoordinates(approxPolygonalCurve)
            drawBoundaryRectangle(frame, boundingRectangleCoordinates)

        if movement_detected and not recording:
            last_movement_time = time.time()
            timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = os.path.join(recordings_dir, f"{timestamp}.mp4")
            out = cv2.VideoWriter(filename, fourcc, frame_rate, (frame_width, frame_height))
            recording = True

        if recording:
            out.write(frame)
            if not movement_detected and time.time() - last_movement_time > record_duration_after_movement:
                recording = False
                out.release()
                out = None

        displayFrame(frame, "Camera Output")

        if userExitRequest():
            break

    stopReading(video)
    if out:
        out.release()

if __name__ == "__main__":
    run()
