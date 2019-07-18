package com.bytedance.androidcamp.minidouyin.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Follow.class}, version = 1)
public abstract class FollowDatabase extends RoomDatabase {
    abstract public FollowDao followDao();
}
