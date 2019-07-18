package com.bytedance.androidcamp.minidouyin.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bytedance.androidcamp.minidouyin.MainActivity;
import com.bytedance.androidcamp.minidouyin.R;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MeFragment extends Fragment {

    private String userName;
    private String userID;

    public MeFragment() {
        super();
    }

    public MeFragment(String userName, String userID) {
        super();
        this.userName = userName;
        this.userID = userID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_me, container, false);
        v.findViewById(R.id.b_exit).setOnClickListener(view -> new AlertDialog.Builder(getActivity())
                .setTitle("警告")
                .setMessage("确认要退出吗？")
                .setNegativeButton("取消", (dialogInterface, i) -> {
                    // Do nothing
                })
                .setPositiveButton("确认", (dialogInterface, i) -> ((MainActivity) getActivity()).logout())
                .create()
                .show());
        ((TextView)v.findViewById(R.id.tv_username)).setText(userName);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.fl_placeholder, new DiscoverFragment(userName))
                .commit();
        return v;
    }



}
