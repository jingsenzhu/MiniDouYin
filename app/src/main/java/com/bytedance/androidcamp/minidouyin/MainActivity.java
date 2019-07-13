package com.bytedance.androidcamp.minidouyin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.airbnb.lottie.LottieAnimationView;
import com.bytedance.androidcamp.minidouyin.fragment.DiscoverFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<DiscoverFragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;

    private RotateAnimation mRotateAnimation = new RotateAnimation(0, 360,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getSupportActionBar() != null)
//            getSupportActionBar().hide();
        setContentView(R.layout.layout_loading);
        LottieAnimationView animationView = findViewById(R.id.lav_loading);
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                MainActivity.this.setContentView(R.layout.activity_main);
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

    private void initBtns() {
        FloatingActionButton refreshButton = findViewById(R.id.fab_ref);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(200);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(mRotateAnimation);
                fragments.get(mViewPager.getCurrentItem()).fetchFeed();
            }
        });
    }

    private void initTab() {
        fragments.add(new DiscoverFragment());
        fragments.add(new DiscoverFragment());
        fragments.add(new DiscoverFragment());
        fragments.add(new DiscoverFragment());
        mViewPager = findViewById(R.id.vp_main);
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
                        title = "发现";
                        break;
                    case 1:
                        title = "关注";
                        break;
                    case 2:
                        title = "消息";
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
