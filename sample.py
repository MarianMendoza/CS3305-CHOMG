from greyscale import *
from imagereader import *
from backgroundseperator import *
from noisereduction import *

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
        displayFrame(foregroundOfFrame)
        if userExitRequest():
            stopReading(video)
            break

if __name__ == "__main__":
    run()