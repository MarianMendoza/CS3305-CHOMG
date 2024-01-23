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
        greyScaleFrame = greyscale.getFrameInGreyScale(frame)
        foregroundOfFrame = backgroundseperator.getForegroundOfFrame(greyScaleFrame, separator)
        foregroundOfFrame = noisereduction.erodeFrameUsingKernel(foregroundOfFrame, kernel)
        foregroundOfFrame = noisereduction.dilateFrameUsingKernel(foregroundOfFrame, kernel)

        contours = boundingbox.getContours(foregroundOfFrame)
        if contours:
            maxContour = boundingbox.getMaxContour(contours)
            approxPolygonalCurve = boundingbox.getApproximateCurve(maxContour)

            boundingRectangleCoordinates = boundingbox.getBoundingRectangleCoordinates(approxPolygonalCurve)

            croppedFrame = cropframe.cropFrameToBoundingBox(frame, boundingRectangleCoordinates)
            
            if entityrecognition.isPersonDetected(croppedFrame):
                print(True)

            boundingbox.drawBoundaryRectangle(frame, boundingRectangleCoordinates)

        imagereader.displayFrame(frame, "bg")
        imagereader.displayFrame(foregroundOfFrame, "fg")
        if imagereader.userExitRequest():
            imagereader.stopReading(video)
            break

if __name__ == "__main__":
    run()