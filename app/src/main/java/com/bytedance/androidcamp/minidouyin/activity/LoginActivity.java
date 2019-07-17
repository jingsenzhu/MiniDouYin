package com.bytedance.androidcamp.minidouyin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bytedance.androidcamp.minidouyin.R;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            getWindow().setEnterTransition(slide);
        }

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.98f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.activity_login);

        findViewById(R.id.ib_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.fab_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = ((EditText)findViewById(R.id.et_name)).getText().toString();
                String userID = ((EditText)findViewById(R.id.et_id)).getText().toString();
                if (userID.isEmpty() || userName.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent();
                i.putExtra("user_name", userName);
                i.putExtra("user_id", userID);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }
}
