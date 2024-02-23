package com.example.chomg.data;

import org.json.JSONObject;
import android.util.Base64;

public class MotionDetectionDecoder {
    public static void main(String[] args) {
        MotionDetectionResponse motionDetectionResponse = new MotionDetectionResponse();
        String jsonString = motionDetectionResponse.getJson();

        // Split the JSON string into its parts
        String[] parts = jsonString.split("\\.");

        // Decode the payload
        byte[] decodedBytes = Base64.decode(parts[1], Base64.DEFAULT);
        String decodedPayload = new String(decodedBytes);

        try {
            // Parse the decoded payload into a JSON object
            JSONObject jsonObject = new JSONObject(decodedPayload);

            // Retrieve the desired values from the JSON object
            String userId = jsonObject.getString("user_id");
            boolean motionDetected = jsonObject.getBoolean("motion_detected");
            boolean personDetected = jsonObject.getBoolean("person_detected");
            long expirationTime = jsonObject.getLong("exp");

            // Use the retrieved values as needed
            System.out.println("User ID: " + userId);
            System.out.println("Motion Detected: " + motionDetected);
            System.out.println("Person Detected: " + personDetected);
            System.out.println("Expiration Time: " + expirationTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



