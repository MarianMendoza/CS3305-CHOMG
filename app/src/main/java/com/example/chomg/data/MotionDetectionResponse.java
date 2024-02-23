package com.example.chomg.data;

import java.util.List;

public class MotionDetectionResponse {
    private String id;
    private String username;
    private String password;
    private int version;
    private List<String> videos;
    private String json;

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public void setJson(String json) {
        this.json = json;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getVersion() {
        return version;
    }

    public List<String> getVideos() {
        return videos;
    }

    public String getJson() {
        return json;
    }
}
