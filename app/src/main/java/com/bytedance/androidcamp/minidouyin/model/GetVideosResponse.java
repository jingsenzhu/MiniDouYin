package com.bytedance.androidcamp.minidouyin.model;

import com.bytedance.androidcamp.minidouyin.model.Video;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetVideosResponse {

    @SerializedName("success") private boolean success;
    @SerializedName("feeds") private List<Video> videos;

    public List<Video> getVideos() {
        return videos;
    }

    public List<Video> getUserVideos(String userName) {
        List<Video> result = new ArrayList<>();
        for (Video video : videos) {
            if (video.getUserName().equals(userName)) {
                result.add(video);
            }
        }
        return result;
    }

    public boolean isSuccess() {
        return success;
    }
}
