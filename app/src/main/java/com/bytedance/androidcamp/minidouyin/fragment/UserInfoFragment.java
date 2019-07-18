package com.bytedance.androidcamp.minidouyin.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bytedance.androidcamp.minidouyin.R;
import com.bytedance.androidcamp.minidouyin.activity.UserActivity;

import java.util.Collections;

public class UserInfoFragment extends Fragment {

    private String userName;
    private String userID;
    private boolean followState = false;

    private static final int FOLLOW_BACKGROUND = 0xffdddddd;
    private static final int UNFOLLOW_BACKGROUND = 0xFFD81B60;
    private static final int FOLLOW_TEXTCOLOR = 0xffaaaaaa;
    private static final int UNFOLLOW_TEXTCOLOR = Color.WHITE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_userinfo, container, false);
        Button followButton = v.findViewById(R.id.b_follow);
        followButton.setTextColor(followState ? FOLLOW_TEXTCOLOR : UNFOLLOW_TEXTCOLOR);
        followButton.setBackgroundColor(followState ? FOLLOW_BACKGROUND : UNFOLLOW_BACKGROUND);
        followButton.setText(followState ? R.string.follow_text : R.string.unfollow_text);
        v.findViewById(R.id.b_follow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button)view;
                followState = !followState;
                UserActivity activity = (UserActivity)getActivity();
                activity.setFollowState(followState);
                activity.setFollowChanged(!activity.isFollowChanged());
                button.setTextColor(followState ? FOLLOW_TEXTCOLOR : UNFOLLOW_TEXTCOLOR);
                button.setBackgroundColor(followState ? FOLLOW_BACKGROUND : UNFOLLOW_BACKGROUND);
                button.setText(followState ? R.string.follow_text : R.string.unfollow_text);
            }
        });
        return v;
    }

    public void setUserInfo(String userName, String userID, boolean followState) {
        this.userName = userName;
        this.userID = userID;
        this.followState = followState;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            ((TextView)getView().findViewById(R.id.tv_name)).setText("用户名：" + userName);
            ((TextView)getView().findViewById(R.id.tv_id)).setText("学号：" + userID);
        }
    }
}
