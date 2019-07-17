package com.bytedance.androidcamp.minidouyin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bytedance.androidcamp.minidouyin.R;
import com.bytedance.androidcamp.minidouyin.fragment.DiscoverFragment;
import com.bytedance.androidcamp.minidouyin.fragment.UserInfoFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private List<Fragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;
    boolean hasLogin;
    boolean followState;
    boolean followChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");
        String studentID = intent.getStringExtra("id");
        hasLogin = intent.getBooleanExtra("has_login", false);
        if (hasLogin) {
            setContentView(R.layout.activity_user);
            followState = intent.getBooleanExtra("follow_state", false);
            ((TextView)findViewById(R.id.tv_username)).setText(userName);
            initTab(userName, studentID);
        } else {
            setContentView(R.layout.activity_user_unlog);
            DiscoverFragment fragment = new DiscoverFragment();
            fragment.setUserName(userName);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_placeholder, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (hasLogin) {
            Intent i = new Intent();
            i.putExtra("follow_state", followState);
            i.putExtra("follow_changed", followChanged);
        }
        super.onBackPressed();
    }

    private void initTab(String userName, String studentID) {
        fragments.add(new UserInfoFragment());
        fragments.add(new DiscoverFragment());
        ((DiscoverFragment)fragments.get(1)).setUserName(userName);
        ((UserInfoFragment)fragments.get(0)).setUserInfo(userName, studentID);
        mViewPager = findViewById(R.id.vp_user);
        TabLayout mTabLayout = findViewById(R.id.tl_user);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "个人资料" : "发布视频";
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
