package com.example.chomg.data;

import java.util.List;

public class MotionDetectionResponse {
    private String userId;
    private List<String> videos;
    private String json;

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public void setJson(String json) {
        this.json = json;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public List<String> getVideos() {
        return videos;
    }

    public String getJson() {
        return json;
    }
}

