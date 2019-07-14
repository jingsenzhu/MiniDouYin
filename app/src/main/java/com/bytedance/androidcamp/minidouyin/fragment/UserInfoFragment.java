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

public class UserInfoFragment extends Fragment {

    private String userName;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_userinfo, container, false);
        v.findViewById(R.id.b_follow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button)view;
                button.setText("已关注");
                button.setBackgroundColor(Color.GRAY);
                button.setTextColor(Color.DKGRAY);
                view.setEnabled(false);
            }
        });
        return v;
    }

    public void setUserInfo(String userName, String userID) {
        this.userName = userName;
        this.userID = userID;
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
