package com.bytedance.androidcamp.minidouyin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bytedance.androidcamp.minidouyin.MainActivity;
import com.bytedance.androidcamp.minidouyin.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MeFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<Fragment> fragments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_me, container, false);
        mViewPager = v.findViewById(R.id.vp_user);
        mTabLayout = v.findViewById(R.id.tl_user);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity parent = (MainActivity)getActivity();
        if (getView() != null)
            ((TextView)getView().findViewById(R.id.tv_username)).setText(parent.getLoginName());
        initTab(parent.getLoginName(), parent.getLoginID());
    }

    private void initTab(String userName, String studentID) {
        fragments.clear();
        fragments.add(new LogoutFragment());
        fragments.add(new DiscoverFragment());
        ((DiscoverFragment)fragments.get(1)).setUserName(userName);
        ((LogoutFragment)fragments.get(0)).setUserInfo(userName, studentID);
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "我的资料" : "我的视频";
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
