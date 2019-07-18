package com.bytedance.androidcamp.minidouyin.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bytedance.androidcamp.minidouyin.MainActivity;
import com.bytedance.androidcamp.minidouyin.R;

public class LogoutFragment extends Fragment {

    private String userName;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_userinfo, container, false);
        Button logoutButton = v.findViewById(R.id.b_follow);
        logoutButton.setBackground(getResources().getDrawable(R.drawable.drawable_unfollowbutton));
        logoutButton.setText(R.string.exit);
        logoutButton.setOnClickListener(view -> new AlertDialog.Builder(getActivity())
                .setTitle("警告")
                .setMessage("确认要退出吗？")
                .setNegativeButton("取消", (dialogInterface, i) -> {
                    // Do nothing
                })
                .setPositiveButton("确认", (dialogInterface, i) -> ((MainActivity) getActivity()).logout())
                .create()
                .show());
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            ((TextView)getView().findViewById(R.id.tv_name)).setText("用户名：" + userName);
            ((TextView)getView().findViewById(R.id.tv_id)).setText("学号：" + userID);
        }
    }

    public void setUserInfo(String userName, String studentID) {
        this.userName = userName;
        this.userID = studentID;
    }
}
