package com.bytedance.androidcamp.minidouyin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.Animator;
import android.app.ActionBar;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.bytedance.androidcamp.minidouyin.activity.CustomCameraActivity;
import com.bytedance.androidcamp.minidouyin.activity.LoginActivity;
import com.bytedance.androidcamp.minidouyin.db.Follow;
import com.bytedance.androidcamp.minidouyin.db.FollowDatabase;
import com.bytedance.androidcamp.minidouyin.fragment.DiscoverFragment;
import com.bytedance.androidcamp.minidouyin.fragment.FollowFragment;
import com.bytedance.androidcamp.minidouyin.fragment.MeFragment;
import com.bytedance.androidcamp.minidouyin.fragment.RemindFragment;
import com.bytedance.androidcamp.minidouyin.fragment.VideoFragment;
import com.bytedance.androidcamp.minidouyin.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.bytedance.androidcamp.minidouyin.utils.ZoomOutPageTransformer;
import com.google.android.material.tabs.TabLayout;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_REQUEST_CODE = 6342;
    private static final int REQUEST_EXTERNAL_STORAGE = 101;
    public static final int USER_REQUEST_CODE = 1314;

    private List<Fragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;

    private ActionBar mActionBar;
    private FollowDatabase database;
    private List<Follow> followList = new ArrayList<>();

    private boolean hasLogin;
    private String loginName;
    private String loginID;
    private FloatingActionButton maddButton;
    //todo :在这里申请相机、存储的权限
    String[] permissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };


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
        if (Utils.isPermissionsReady(MainActivity.this,permissions)) {

        } else {
            Utils.reuqestPermissions(MainActivity.this,permissions,REQUEST_EXTERNAL_STORAGE);
        }

        LottieAnimationView animationView = findViewById(R.id.lav_loading);
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                MainActivity.this.setContentView(R.layout.activity_main);
                checkLogin();
                initBtns();
                initTab();
                initDB();
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
    protected void onDestroy() {
        database.close();
        database = null;
        super.onDestroy();
    }

    private long lastBackTime = System.currentTimeMillis();

    @Override
    public void onBackPressed() {
        long backTime = System.currentTimeMillis();
        if (backTime - lastBackTime < 2000) {
            super.onBackPressed();
        } else {
            lastBackTime = backTime;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            loginName = data.getStringExtra("user_name");
            loginID = data.getStringExtra("user_id");
            if (loginName != null && loginID != null) {
                hasLogin = true;
                new FetchFollowListTask(this).execute();
                writeLoginStatus();
                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                fragments.remove(3);
                fragments.remove(2);
                fragments.add(new FollowFragment());
                fragments.add(new MeFragment(loginName, loginID));
                mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
                mViewPager.getAdapter().notifyDataSetChanged();
            }
        } else if (requestCode == USER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean followChanged = data.getBooleanExtra("follow_changed", false);
            boolean followState = data.getBooleanExtra("follow_state", false);
            String followName = data.getStringExtra("follow_name");
            String followID = data.getStringExtra("follow_id");
            updateFollowFragmentFlag = true;
            if (followChanged && loginID != null && followID != null) {
                updateFollow(followState, new Follow(loginID, followName, followID));
            }
        }
        else if (requestCode == CustomCameraActivity.REQUEST_UPLOAD && resultCode == RESULT_OK && data != null) {
            String returnPath = data.getStringExtra("path");
            if (returnPath != null) {
                ((DiscoverFragment)fragments.get(1)).postVideo(loginName, loginID,returnPath);
            }
        }
    }

    public void updateFollow(boolean followState, Follow follow) {
        if (followState) {
            new FollowTask(this).execute(follow);
        } else {
            new UnFollowTask(this).execute(follow);
        }
    }

    public String getLoginName() {
        return loginName;
    }

    public String getLoginID() {
        return loginID;
    }

    public boolean isHasLogin() {
        return hasLogin;
    }

    public boolean checkFollowState(String userName) {
        for (Follow follow : followList) {
            if (follow.followName.equals(userName)) {
                return true;
            }
        }
        return false;
    }

    private void initDB() {
        database = Room.databaseBuilder(MainActivity.this,
                FollowDatabase.class, "follow-db").build();
        new FetchFollowListTask(this).execute();
    }

    static private class UnFollowTask extends AsyncTask<Follow, Follow, Integer> {

        private WeakReference<MainActivity> mActivity;

        private UnFollowTask(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        protected Integer doInBackground(Follow... follows) {
            Integer ret = 0;
            for (Follow follow : follows) {
                MainActivity activity = mActivity.get();
                if (activity != null && activity.hasLogin) {
                    ret += activity.database.followDao().unfollow(follow);
                }
            }
            return ret;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            MainActivity activity = mActivity.get();
            if (activity != null) {
                new FetchFollowListTask(activity).execute();
            }
        }
    }

    private boolean updateFollowFragmentFlag = false;

    static private class FollowTask extends AsyncTask<Follow, Follow, Long> {

        private WeakReference<MainActivity> mActivity;

        private FollowTask(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        protected Long doInBackground(Follow... follows) {
            long ret = 0L;
            for (Follow follow : follows) {
                MainActivity activity = mActivity.get();
                if (activity != null && activity.hasLogin) {
                    ret += activity.database.followDao().follow(follow);
                }
            }
            return ret;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            MainActivity activity = mActivity.get();
            if (activity != null) {
                new FetchFollowListTask(activity).execute();
            }
        }
    }

    static private class FetchFollowListTask extends AsyncTask<Void, Void, List<Follow>>  {

        private WeakReference<MainActivity> mActivity;

        private FetchFollowListTask(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        protected List<Follow> doInBackground(Void... voids) {
            MainActivity activity = mActivity.get();
            if (activity != null && activity.hasLogin) {
                return activity.database.followDao().getFollowList(activity.loginID);
            }
            return Collections.emptyList();
        }

        @Override
        protected void onPostExecute(List<Follow> follows) {
            super.onPostExecute(follows);
            MainActivity activity = mActivity.get();
            if (activity != null && activity.hasLogin) {
                activity.followList = follows;
                if (activity.updateFollowFragmentFlag == true) {
                    ((FollowFragment) activity.fragments.get(2)).updateFollowList(follows);
                    activity.updateFollowFragmentFlag = false;
                }
            }
        }
    }

    public List<Follow> getFollowList() {
        return followList;
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
        followList.clear();
    }

    private void initBtns() {
        findViewById(R.id.fab_add).setOnClickListener(view -> {
            if (!hasLogin) {
                login();
            } else {
                Intent i = new Intent(MainActivity.this, CustomCameraActivity.class);

                startActivityForResult(i, CustomCameraActivity.REQUEST_UPLOAD);
            }
        });
        findViewById(R.id.fab_add).setVisibility(View.GONE);
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

    public void goToDiscover() {
        mViewPager.setCurrentItem(1, true);
    }

    private void initTab() {
        fragments.add(new VideoFragment());
        fragments.add(new DiscoverFragment());
        if (!hasLogin) {
            fragments.add(new RemindFragment());
            fragments.add(new RemindFragment());
        } else {
            fragments.add(new FollowFragment());
            fragments.add(new MeFragment(loginName, loginID));
        }
        mViewPager = findViewById(R.id.vp_main);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    findViewById(R.id.tl_main).startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.tab_out));
                    findViewById(R.id.tl_main).setVisibility(View.GONE);

                    AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                    alphaAnimation.setDuration(300);
                    alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                    findViewById(R.id.fab_add).startAnimation(alphaAnimation);
                    findViewById(R.id.fab_add).setVisibility(View.GONE);
                } else {
                    if (findViewById(R.id.tl_main).getVisibility() != View.VISIBLE) {
                        findViewById(R.id.tl_main).startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.tab_in));
                        findViewById(R.id.tl_main).setVisibility(View.VISIBLE);
                    }
                    if (findViewById(R.id.fab_add).getVisibility() != View.VISIBLE) {
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                        alphaAnimation.setDuration(300);
                        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                        findViewById(R.id.fab_add).startAnimation(alphaAnimation);
                        findViewById(R.id.fab_add).setVisibility(View.VISIBLE);
                    }
                }
                if (position > 1 && !hasLogin) {
                    login();
                } else if (position == 2 && fragments.get(2) instanceof FollowFragment) {
                    ((FollowFragment)fragments.get(2)).updateFollowList(followList);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
        TabLayout mTabLayout = findViewById(R.id.tl_main);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setVisibility(View.GONE);
        // Set transform-page animation
        mViewPager.setPageTransformer(false, new ZoomOutPageTransformer());
    }

}
