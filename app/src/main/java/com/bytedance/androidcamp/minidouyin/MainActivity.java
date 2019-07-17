package com.bytedance.androidcamp.minidouyin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.bytedance.androidcamp.minidouyin.activity.CameraActivity;
import com.bytedance.androidcamp.minidouyin.activity.LoginActivity;
import com.bytedance.androidcamp.minidouyin.fragment.DiscoverFragment;
import com.bytedance.androidcamp.minidouyin.fragment.FollowFragment;
import com.bytedance.androidcamp.minidouyin.fragment.MeFragment;
import com.bytedance.androidcamp.minidouyin.fragment.RemindFragment;
import com.bytedance.androidcamp.minidouyin.fragment.VideoFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.bytedance.androidcamp.minidouyin.utils.DepthPageTransformer;
import com.bytedance.androidcamp.minidouyin.utils.ZoomOutPageTransformer;
import com.google.android.material.tabs.TabLayout;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_REQUEST_CODE = 6342;

    private List<Fragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;

    private ActionBar mActionBar;

    private boolean hasLogin;
    private String loginName;
    private String loginID;
    private FloatingActionButton maddButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getSupportActionBar() != null)
//            getSupportActionBar().hide();
        setContentView(R.layout.layout_loading);
        mActionBar = getActionBar();
        if (mActionBar != null){
            mActionBar.hide();
        }

        LottieAnimationView animationView = findViewById(R.id.lav_loading);
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                MainActivity.this.setContentView(R.layout.activity_main);
                checkLogin();
                initBtns();
                initTab();
            }
            @Override
            public void onAnimationStart(Animator animator) {}
            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            loginName = data.getStringExtra("user_name");
            loginID = data.getStringExtra("user_id");
            if (loginName != null && loginID != null) {
                hasLogin = true;
                writeLoginStatus();
                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                fragments.remove(3);
                fragments.remove(2);
                fragments.add(new FollowFragment());
                fragments.add(new MeFragment());
                mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
                mViewPager.getAdapter().notifyDataSetChanged();
            }
        }
    }

    private void writeLoginStatus() {
        SharedPreferences preferences = getSharedPreferences(
                getResources().getString(R.string.login_pref_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("has_login", hasLogin);
        editor.putString("login_name", loginName);
        editor.putString("login_id", loginID);
        editor.apply();
    }

    private void checkLogin() {
        SharedPreferences preferences = getSharedPreferences(
                getString(R.string.login_pref_name), MODE_PRIVATE);
        hasLogin = preferences.getBoolean("has_login", false);
        if (hasLogin) {
            loginName = preferences.getString("login_name", "");
            loginID = preferences.getString("login_id", "");
        }
    }

    public void login() {
        if (!hasLogin) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class),
                        MainActivity.LOGIN_REQUEST_CODE);
            } else {
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class),
                        MainActivity.LOGIN_REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        }
    }

    public void logout() {
        if (!hasLogin)
            return;
        hasLogin = false;
        loginID = null;
        loginName = null;
        fragments.remove(3);
        fragments.remove(2);
        fragments.add(new RemindFragment());
        fragments.add(new RemindFragment());
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        mViewPager.getAdapter().notifyDataSetChanged();
        writeLoginStatus();
    }

    private void initBtns() {
        findViewById(R.id.fab_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasLogin) {
                    login();
                } else {

                }
            }
        });
    }

    class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title;
            switch (position) {
                case 0:
                    title = "推荐";
                    break;
                case 1:
                    title = "发现";
                    break;
                case 2:
                    title = "关注";
                    break;
                default:
                    title = "我";
                    break;
            }
            return title;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private void initTab() {
        fragments.add(new VideoFragment());
        fragments.add(new DiscoverFragment());
        if (!hasLogin) {
            fragments.add(new RemindFragment());
            fragments.add(new RemindFragment());
        } else {
            fragments.add(new FollowFragment());
            fragments.add(new MeFragment());
        }
        mViewPager = findViewById(R.id.vp_main);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position > 1 && !hasLogin) {
                    login();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
        TabLayout mTabLayout = findViewById(R.id.tl_main);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

        // Set transform-page animation
        mViewPager.setPageTransformer(false, new ZoomOutPageTransformer());
    }

}
