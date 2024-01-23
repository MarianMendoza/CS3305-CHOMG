import cv2
# Reference - https://stackoverflow.com/questions/60869306/how-to-simple-crop-the-bounding-box-in-python-opencv
def crop_frame_to_bounding_box(frame, bounding_box):
    x = bounding_box.x1
    y = bounding_box.y1
    height= bounding_box.y2
    width = bounding_box.x2
    cropped_frame = frame[y: y + height, x: x + width]
    return cropped_frame