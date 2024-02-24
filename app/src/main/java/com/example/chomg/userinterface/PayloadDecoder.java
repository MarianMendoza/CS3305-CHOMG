package com.example.chomg.userinterface;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Base64;

public class PayloadDecoder {

    public void decodeJsonPayload(String jsonPayload) {
        try {
            int startIndex = jsonPayload.indexOf("json:");

            String payloadSubstring = jsonPayload.substring(startIndex + 6); // 6 is the length of "json: "

            JSONObject jsonObject = new JSONObject(payloadSubstring);

            // Extract values from the JSON object
            String userId = jsonObject.optString("user_id");
            boolean motionDetected = jsonObject.optBoolean("motion_detected");
            boolean personDetected = jsonObject.optBoolean("person_detected");
            long expirationTime = jsonObject.optLong("exp");

            // Now you can use these values as needed in your code
            System.out.println("User ID: " + userId);
            System.out.println("Motion Detected: " + motionDetected);
            System.out.println("Person Detected: " + personDetected);
            System.out.println("Expiration Time: " + expirationTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PayloadDecoder decoder = new PayloadDecoder();
        String jsonPayload = "json: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiQW15IiwibW90aW9uX2RldGVjdGVkIjp0cnVlLCJwZXJzb25fZGV0ZWN0ZWQiOmZhbHNlLCJleHAiOjE3MDg0Mzk3MTd9.QwVgEm-qb-J6-Six2bsPPysdBPsjpshy95742vKM44E'";
        decoder.decodeJsonPayload(jsonPayload);
    }
}
