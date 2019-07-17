package com.bytedance.androidcamp.minidouyin.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FollowDao {

    @Query("SELECT * FROM follow WHERE user_id = :userID")
    public List<Follow> getFollowList(String userID);

    @Insert
    public void follow(Follow follow);

    @Delete
    public void unfollow(Follow follow);

}
