package com.bytedance.androidcamp.minidouyin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bytedance.androidcamp.minidouyin.fragment.DiscoverFragment;
import com.bytedance.androidcamp.minidouyin.fragment.FollowFragment;
import com.bytedance.androidcamp.minidouyin.fragment.RemindFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Fragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;

    private ActionBar mActionBar;

    private boolean hasLogin;
    private String loginName;
    private String loginID;

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
    protected void onDestroy() {
        SharedPreferences preferences = getSharedPreferences(
                getResources().getString(R.string.login_pref_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("has_login", hasLogin);
        editor.putString("login_name", loginName);
        editor.putString("login_id", loginID);
        editor.apply();
        super.onDestroy();
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

    private void initBtns() {

    }

    private void initTab() {
        fragments.add(new DiscoverFragment());
        fragments.add(new DiscoverFragment());
        if (!hasLogin) {
            fragments.add(new RemindFragment());
            fragments.add(new RemindFragment());
        } else {
            fragments.add(new FollowFragment());
            fragments.add(new FollowFragment());
        }
        mViewPager = findViewById(R.id.vp_main);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position > 1 && !hasLogin) {
                    Toast.makeText(MainActivity.this, "需要登录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
        TabLayout mTabLayout = findViewById(R.id.tl_main);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
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
        });
        mTabLayout.setupWithViewPager(mViewPager);

        // Set transform-page animation
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            private static final float MIN_SCALE = 0.85f;
            private static final float MIN_ALPHA = 0.5f;

            @Override
            public void transformPage(@NonNull View page, float position) {
                //a slide to b, position changes are a: 0 -> -1 ; b: 1 -> 0.
                int pageWidth = page.getWidth();
                int pageHeight = page.getHeight();
                if (position < -1 || position > 1) {
                    // This page is way off-screen to the left.
                    page.setAlpha(0); // Not shown
                } else {
                    // -1 <= position <= 1
                    // Modify the default slide transition to shrink the page as well
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                    if (position < 0) {
                        page.setTranslationX(horzMargin - vertMargin / 2);
                    } else {
                        page.setTranslationX(-horzMargin + vertMargin / 2);
                    }
                    // Scale the page down (between MIN_SCALE and 1)
                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);
                    // Fade the page relative to its size.
                    page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
                            / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
                }
            }
        });
    }

}
