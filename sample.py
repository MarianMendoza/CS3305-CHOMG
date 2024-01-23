from greyscale import *
from imagereader import *
from backgroundseperator import *
from noisereduction import *
from boundingbox import *
from entityrecognition import *
from cropframe import *

def run():
    video = readVideoFromCamera()
    separator = createBackgroundSubtractor()
    kernel = createNoiseReductionKernel()
    
    while True:
        frame = captureFrame(video)
        greyScaleFrame = getFrameInGreyScale(frame)
        foregroundOfFrame = getForegroundOfFrame(greyScaleFrame, separator)
        foregroundOfFrame = erodeFrameUsingKernel(foregroundOfFrame, kernel)
        foregroundOfFrame = dilateFrameUsingKernel(foregroundOfFrame, kernel)

        contours = getContours(foregroundOfFrame)
        if contours:
            maxContour = getMaxContour(contours)
            approxPolygonalCurve = getApproximateCurve(maxContour)

            boundingRectangleCoordinates = getBoundingRectangleCoordinates(approxPolygonalCurve)

            colouredForegroundOfFrame = recolourForegroundUsingOriginalFrame(foregroundOfFrame, frame)
            
            if isPersonDetected(colouredForegroundOfFrame):
                print(True)
            drawBoundaryRectangle(frame, boundingRectangleCoordinates)

        displayFrame(frame, "bg")
        displayFrame(colouredForegroundOfFrame, "fg")
        if userExitRequest():
            stopReading(video)
            break

if __name__ == "__main__":
    run()