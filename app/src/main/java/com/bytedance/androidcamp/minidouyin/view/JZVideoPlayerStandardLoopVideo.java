package com.bytedance.androidcamp.minidouyin.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.jzvd.JZVideoPlayerStandard;

public class JZVideoPlayerStandardLoopVideo extends JZVideoPlayerStandard {
    public JZVideoPlayerStandardLoopVideo (Context context) {
        super(context);
    }

    public JZVideoPlayerStandardLoopVideo (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        startVideo();
    }
}
