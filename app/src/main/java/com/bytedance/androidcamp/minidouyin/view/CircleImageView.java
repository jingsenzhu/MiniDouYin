package com.bytedance.androidcamp.minidouyin.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class CircleImageView extends AppCompatImageView {

    float mWidth, mHeight, mRadius;
    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Matrix mMatrix = new Matrix();

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mRadius = Math.min(mWidth, mHeight) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (!(drawable instanceof BitmapDrawable)) {
            super.onDraw(canvas);
            return;
        }
        BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
        mPaint.setShader(getBitmapShader(bitmapDrawable));
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mPaint);
    }

    private BitmapShader getBitmapShader(BitmapDrawable drawable) {
        Bitmap bitmap = drawable.getBitmap();
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = Math.max(mWidth / bitmap.getWidth(), mHeight / bitmap.getHeight());
        mMatrix.setScale(scale, scale);
        shader.setLocalMatrix(mMatrix);
        return shader;
    }
}
