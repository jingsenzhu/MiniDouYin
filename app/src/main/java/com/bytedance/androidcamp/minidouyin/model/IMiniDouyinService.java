package com.bytedance.androidcamp.minidouyin.model;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IMiniDouyinService {
    @GET("video") Call<GetVideosResponse> getVideos();
}
