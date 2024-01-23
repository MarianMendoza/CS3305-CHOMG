import greyscale 
import imagereader 
import backgroundseperator
import noisereduction
import boundingbox
import entityrecognition 
import cropframe

def run():
    video = imagereader.read_video_from_camera()
    separator = backgroundseperator.create_background_subtractor_object()
    kernel = noisereduction.create_noise_reduction_kernel()
    while True:
        frame = imagereader.capture_frame(video)
        grey_scale_frame = greyscale.get_frame_in_grey_scale(frame)
        foreground_of_frame = backgroundseperator.get_foreground_of_frame_using_subtractor_object(grey_scale_frame, separator)
        foreground_of_frame = noisereduction.erode_frame_using_kernel(foreground_of_frame, kernel)
        foreground_of_frame = noisereduction.dilate_frame_using_kernel(foreground_of_frame, kernel)

        contours = boundingbox.get_contours(foreground_of_frame)
        if contours:
            max_contour = boundingbox.get_max_contour(contours)
            approximate_polygonal_curve = boundingbox.get_approximate_curve_from_contour(max_contour)

            bounding_rectangle_coordinates = boundingbox.get_bounding_box_from_curve(approximate_polygonal_curve)

            cropped_frame = cropframe.crop_frame_to_bounding_box(frame, bounding_rectangle_coordinates)
            
            if entityrecognition.is_person_detected_in_frame(cropped_frame):
                print(True)

            boundingbox.draw_bounding_box_on_frame(frame, bounding_rectangle_coordinates)

        imagereader.display_frame(frame, "bg")
        imagereader.display_frame(foreground_of_frame, "fg")
        if imagereader.user_exit_request():
            imagereader.stop_reading(video)
            break

if __name__ == "__main__":
    run()