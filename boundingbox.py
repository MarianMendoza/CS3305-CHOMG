# Tutorial Reference:
# https://www.analyticsvidhya.com/blog/2021/08/getting-started-with-object-tracking-using-opencv/
import cv2


class BoundingBox(object):
    def __init__(self, x, y, width, height) -> None:
        self.x = x
        self.y = y
        self.width_from_x = x + width
        self.height_from_y = y + height

def get_contours(frame_mask):
        return cv2.findContours(frame_mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE) [0]

def get_max_contour(contours) -> float:
    max_contour = contours[0]
    for contour in contours:
        if cv2.contourArea(contour) > cv2.contourArea(max_contour):
            max_contour = contour
    return max_contour

def get_bounding_box_from_curve(approx):
    return BoundingBox(*cv2.boundingRect(approx))

def draw_bounding_box_on_frame(frame, bounding_box):
    black_rgb_value = (0, 0, 0)
    boundary_box_width = 4
    cv2.rectangle(frame, (bounding_box.x, bounding_box.y), 
                        (bounding_box.width_from_x, bounding_box.height_from_y), 
                    black_rgb_value, boundary_box_width)    
    
def get_approximate_curve_from_contour(max_contour):
     return cv2.approxPolyDP(max_contour, 0.01 * cv2.arcLength(max_contour, True), True)