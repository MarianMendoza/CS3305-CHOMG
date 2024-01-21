# Tutorial Reference:
# https://www.analyticsvidhya.com/blog/2021/08/getting-started-with-object-tracking-using-opencv/
import cv2


class Rectangle(object):
    def __init__(self, x, y, width, height) -> None:
        self.x1 = x
        self.y1 = y
        self.x2 = x + width
        self.y2 = y + height

def getContours(frameMask):
        return cv2.findContours(frameMask, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE) [0]

def getMaxContour(contours) -> float:
    max_contour = contours[0]
    for contour in contours:
        if cv2.contourArea(contour) > cv2.contourArea(max_contour):
            max_contour = contour
    return max_contour

def getBoundingRectangleCoordinates(approx):
    return Rectangle(*cv2.boundingRect(approx))

def drawBoundaryRectangle(frame, boundingRectangleCoordinates):
    blackRGBCode = (255, 255, 255)
    boundaryWidth = 4
    cv2.rectangle(frame, (boundingRectangleCoordinates.x1, boundingRectangleCoordinates.y1), 
                    (boundingRectangleCoordinates.x2, boundingRectangleCoordinates.y2), 
                    blackRGBCode, boundaryWidth)    
    
def getApproximateCurve(maxContour):
     return cv2.approxPolyDP(maxContour, 0.01 * cv2.arcLength(maxContour, True), True)