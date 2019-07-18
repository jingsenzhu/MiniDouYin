package com.bytedance.androidcamp.minidouyin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.androidcamp.minidouyin.MainActivity;
import com.bytedance.androidcamp.minidouyin.R;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class VideoFragment extends Fragment {

    private RecyclerView mrvVideo;
    private List<String> urlList;
    private List<String> imgList;
    private ListVideoAdapter videoAdapter;
    private PagerSnapHelper snapHelper;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_video, container, false);

        initUrlList();
        initImgList();
        initView(mView);
        addListener();
        
        return mView;
    }

    private void initView(View view){

        view.findViewById(R.id.tv_discover).setOnClickListener(v -> ((MainActivity)getActivity()).goToDiscover());

        mrvVideo = view.findViewById(R.id.rv_video);
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mrvVideo);

        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mrvVideo.setLayoutManager(layoutManager);

        mrvVideo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mrvVideo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                /*这么写是为了获取RecycleView的宽高*/
                // 创建Adapter
                videoAdapter = new ListVideoAdapter(getActivity(), mrvVideo.getWidth(), mrvVideo.getHeight());
                // 设置Adapter
                mrvVideo.setAdapter(videoAdapter);
            }
        });

    }

    private void initUrlList(){
        urlList = new ArrayList<>();
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201811/26/09/5bfb4c55633c9.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201805/100651/201805181532123423.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803151735198462.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150923220770.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150922255785.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150920130302.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803141625005241.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803141624378522.mp4");
        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803131546119319.mp4");
    }

    private void initImgList()
    {
        imgList = new ArrayList<>();
        // TODO 3: 增加图片
    }

    private void addListener() {

        mrvVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://停止滚动
                        View view = snapHelper.findSnapView(layoutManager);
                        JZVideoPlayer.releaseAllVideos();
                        RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                        if (viewHolder instanceof VideoViewHolder) {
                            ((VideoViewHolder) viewHolder).mp_video.startVideo();
                        }
                        break;

                    case RecyclerView.SCROLL_STATE_DRAGGING://拖动
                        break;

                    case RecyclerView.SCROLL_STATE_SETTLING://惯性滑动
                        break;
                }

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }


    class ListVideoAdapter extends RecyclerView.Adapter <VideoViewHolder> {

        private Context mContext;
        private int mheight;
        private int mwidth;

        private List<Boolean> likeList = new ArrayList<>();


        public ListVideoAdapter(Context context, int width, int height) {
            mContext = context;
            mwidth = width;
            mheight = height;
            for (int i = 0; i < urlList.size(); i++) {
                likeList.add(false);
            }
        }

        @Override
        public void onBindViewHolder(final VideoViewHolder holder, final int position) {

            ImageButton imageButton = holder.itemView.findViewById(R.id.b_like);
            imageButton.setOnClickListener(v -> {
                ImageButton button = (ImageButton)v;
                boolean like = !likeList.get(position);
                likeList.set(position, like);
                button.setImageDrawable(getResources().getDrawable(like ? R.drawable.ic_heart : R.drawable.ic_like));
                if (like) {
                    ScaleAnimation animation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                            ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(200);
                    animation.setRepeatCount(1);
                    animation.setRepeatMode(Animation.REVERSE);
                    button.startAnimation(animation);
                }
            });

            holder.mp_video.setOnClickListener(new OnDoubleClickListener() {
                @Override
                public void onDoubleClick(View view) {
                    likeList.set(position, true);
                    holder.itemView.findViewById(R.id.iv_like).setVisibility(View.VISIBLE);
                }
            });

            holder.mp_video.setUp(urlList.get(position), JZVideoPlayerStandard.CURRENT_STATE_NORMAL);
            // 一开始播放第一个视频
            if (position == 0){
                holder.mp_video.startVideo();
            }
//            holder.mtextView.setText(urlList.get(position));

            // TODO : 设置缩略图
            //Glide.with(getActivity()).load(imgList.get(position)).into(holder.mp_video.thumbImageView);

        }

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.layout_recycler_vedio, null);

            JZVideoPlayerStandard videoPlayer = view.findViewById(R.id.videoplayer);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) videoPlayer.getLayoutParams();
            layoutParams.width = mwidth;
            layoutParams.height = mheight;
            videoPlayer.setLayoutParams(layoutParams);

            // 创建一个ViewHolder
            VideoViewHolder holder = new VideoViewHolder(view);
            return holder;

        }

        @Override
        public int getItemCount() {
            return urlList.size();
        }

    }


    public class VideoViewHolder extends RecyclerView.ViewHolder {

        private JZVideoPlayerStandard mp_video;
//        private TextView mtextView;

        public VideoViewHolder(View rootView) {
            super(rootView);
            this.mp_video = rootView.findViewById(R.id.videoplayer);
//            this.mtextView = rootView.findViewById(R.id.videoplayer);
        }

    }

    public abstract static class OnDoubleClickListener implements View.OnClickListener {

        long lastClickTime = 0;
        static final int TIME_INTERVAL = 500;
//        Handler handler = new Handler();

        public abstract void onDoubleClick(View view);

        @Override
        public void onClick(View view) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < TIME_INTERVAL) {
                onDoubleClick(view);
            }
            lastClickTime = clickTime;
        }
    }

}
