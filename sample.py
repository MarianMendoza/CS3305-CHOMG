from greyscale import *
from imagereader import *
from backgroundseperator import *

def run():
    video = readVideoFromCamera()
    separator = createBackgroundSubtractor()

    while True:
        frame = captureFrame(video)
        greyScaleFrame = getFrameInGreyScale(frame)
        foregroundOfFrame = getForegroundOfFrame(greyScaleFrame, separator)
        displayFrame(foregroundOfFrame)
        if userExitRequest():
            stopReading()

if __name__ == "__main__":
    run()