package com.bytedance.androidcamp.minidouyin.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;

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

    public Follow() {}

    @Ignore
    public Follow(@NotNull String userID, String followName, @NotNull String followID) {
        this.userID = userID;
        this.followName = followName;
        this.followID = followID;
    }
}
