package com.bytedance.androidcamp.minidouyin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bytedance.androidcamp.minidouyin.MainActivity;
import com.bytedance.androidcamp.minidouyin.R;

public class MeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_me, container, false);
        mView.findViewById(R.id.b_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).logout();
            }
        });
        return mView;
    }
}
