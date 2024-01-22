import cv2
# Reference - https://stackoverflow.com/questions/60869306/how-to-simple-crop-the-bounding-box-in-python-opencv
def cropFrameToBoundingBox(frame, boundingBox):
    x = boundingBox.x1
    y = boundingBox.y1
    height= boundingBox.y2
    width = boundingBox.x2
    croppedFrame = frame[y: y + height, x: x + width]
    return croppedFrame