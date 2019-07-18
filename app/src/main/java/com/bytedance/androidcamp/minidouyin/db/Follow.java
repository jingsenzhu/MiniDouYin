package com.bytedance.androidcamp.minidouyin.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "follow", primaryKeys = {"user_id", "follow_id"})
public class Follow {
    @NonNull
    @ColumnInfo(name = "user_id")
    public String userID;

    @ColumnInfo(name = "follow_name")
    public String followName;

    @NonNull
    @ColumnInfo(name = "follow_id")
    public String followID;
}
