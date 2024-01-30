# Reference - https://stackoverflow.com/questions/60869306/how-to-simple-crop-the-bounding-box-in-python-opencv
def crop_frame_to_bounding_box(frame, bounding_box):
    x = bounding_box.x
    y = bounding_box.y
    height= bounding_box.height_from_y
    width = bounding_box.width_from_x
    cropped_frame = frame[y: y + height, x: x + width]
    return cropped_frame