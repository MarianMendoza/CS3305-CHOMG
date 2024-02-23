package com.example.chomg.userinterface;

import com.google.gson.annotations.SerializedName;

public class MotionDetectionResponse {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("motion_detected")
    private boolean motionDetected;

    @SerializedName("person_detected")
    private boolean personDetected;

    @SerializedName("exp")
    private long expirationTime;

    public MotionDetectionResponse(String userId, boolean motionDetected, boolean personDetected, long expirationTime) {
        this.userId = userId;
        this.motionDetected = motionDetected;
        this.personDetected = personDetected;
        this.expirationTime = expirationTime;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isMotionDetected() {
        return motionDetected;
    }

    public void setMotionDetected(boolean motionDetected) {
        this.motionDetected = motionDetected;
    }

    public boolean isPersonDetected() {
        return personDetected;
    }

    public void setPersonDetected(boolean personDetected) {
        this.personDetected = personDetected;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
