package com.bytedance.androidcamp.minidouyin.model;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("student_id") private String studentId;
    @SerializedName("user_name") private String userName;
    @SerializedName("image_url") private String imageUrl;
    @SerializedName("video_url") private String videoUrl;
    @SerializedName("createdAt") private String time;
    @SerializedName("image_w")   private int imageWidth;
    @SerializedName("image_h")   private int imageHeight;

    public int getImageWidth(){return imageWidth;}
    public void setImageWidth(int imageWidth){this.imageWidth = imageWidth;}

    public  int getImageHeight(){return imageHeight;}
    public  void setImageHeight(int imageHeight){this.imageHeight = imageHeight;}

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDate() {
        return time.substring(0, time.indexOf('T'));
    }

    public void setTime(String time) {
        this.time = time;
    }
}
