package com.bytedance.androidcamp.minidouyin.model;

import com.bytedance.androidcamp.minidouyin.model.Video;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetVideosResponse {

    @SerializedName("success") private boolean success;
    @SerializedName("feeds") private List<Video> videos;

    public List<Video> getVideos() {
        return videos;
    }

    public boolean isSuccess() {
        return success;
    }
}
