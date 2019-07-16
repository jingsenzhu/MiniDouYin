package com.bytedance.androidcamp.minidouyin.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bytedance.androidcamp.minidouyin.R;

public class RemindFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_remind, container, false);
        TextView remindTextView = mView.findViewById(R.id.tv_remind);
        SpannableString reminder = new SpannableString("需要登录以查看内容");
        reminder.setSpan(new UnderlineSpan(), 2, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        reminder.setSpan(new ForegroundColorSpan(Color.BLUE), 2, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        reminder.setSpan(new RelativeSizeSpan(1.5f), 2, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        reminder.setSpan(new UnderlineSpan(), 2, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        reminder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "登录中", Toast.LENGTH_SHORT).show();
            }
        }, 2, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        remindTextView.setMovementMethod(LinkMovementMethod.getInstance());
        remindTextView.setText(reminder);
        return mView;
    }
}
