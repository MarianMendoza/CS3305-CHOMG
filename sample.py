import greyscale 
import imagereader 
import backgroundseperator
import noisereduction
import boundingbox
import entityrecognition 
import cropframe

def run():
    video = imagereader.readVideoFromCamera()
    separator = backgroundseperator.createBackgroundSubtractor()
    kernel = noisereduction.createNoiseReductionKernel()
    while True:
        frame = imagereader.captureFrame(video)
        grey_scale_frame = greyscale.getFrameInGreyScale(frame)
        foreground_of_frame = backgroundseperator.getForegroundOfFrame(grey_scale_frame, separator)
        foreground_of_frame = noisereduction.erodeFrameUsingKernel(foreground_of_frame, kernel)
        foreground_of_frame = noisereduction.dilateFrameUsingKernel(foreground_of_frame, kernel)

        contours = boundingbox.getContours(foreground_of_frame)
        if contours:
            max_contour = boundingbox.getMaxContour(contours)
            approximate_polygonal_curve = boundingbox.getApproximateCurve(max_contour)

            bounding_rectangle_coordinates = boundingbox.getBoundingRectangleCoordinates(approximate_polygonal_curve)

            cropped_frame = cropframe.cropFrameToBoundingBox(frame, bounding_rectangle_coordinates)
            
            if entityrecognition.isPersonDetected(cropped_frame):
                print(True)

            boundingbox.drawBoundaryRectangle(frame, bounding_rectangle_coordinates)

        imagereader.displayFrame(frame, "bg")
        imagereader.displayFrame(foreground_of_frame, "fg")
        if imagereader.userExitRequest():
            imagereader.stopReading(video)
            break

if __name__ == "__main__":
    run()