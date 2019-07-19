package com.bytedance.androidcamp.minidouyin.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bytedance.androidcamp.minidouyin.MainActivity;
import com.bytedance.androidcamp.minidouyin.R;
import com.bytedance.androidcamp.minidouyin.activity.UserActivity;
import com.bytedance.androidcamp.minidouyin.model.GetVideosResponse;
import com.bytedance.androidcamp.minidouyin.model.IMiniDouyinService;
import com.bytedance.androidcamp.minidouyin.model.Video;
import com.bytedance.androidcamp.minidouyin.view.JZVideoPlayerStandardLoopVideo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoFragment extends Fragment {

    private RecyclerView mrvVideo;
//    private List<String> urlList;
//    private List<String> imgList;

    private final String BASE_URL = "http://test.androidcamp.bytedance.com/mini_douyin/invoke/";

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private IMiniDouyinService miniDouyinService = retrofit.create(IMiniDouyinService.class);

    private List<Video> urlList = new ArrayList<>();
    private ListVideoAdapter videoAdapter;
    private SnapHelper snapHelper;
    private LinearLayoutManager layoutManager;
    private List<Boolean> likeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_video, container, false);

//        initUrlList();
//        initImgList();
        initView(mView);
        fetchFeed();
        addListener();
        
        return mView;
    }

    private void initView(View view){

        view.findViewById(R.id.tv_discover).setOnClickListener(v -> ((MainActivity) Objects.requireNonNull(getActivity())).goToDiscover());

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

//    private void initUrlList(){
//        urlList = new ArrayList<>();
//        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201811/26/09/5bfb4c55633c9.mp4");
//        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201805/100651/201805181532123423.mp4");
//        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803151735198462.mp4");
//        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150923220770.mp4");
//        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150922255785.mp4");
//        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150920130302.mp4");
//        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803141625005241.mp4");
//        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803141624378522.mp4");
//        urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803131546119319.mp4");
//    }
//
//    private void initImgList()
//    {
//        imgList = new ArrayList<>();
//
//    }

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
                        RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                        if (viewHolder instanceof VideoViewHolder && !((VideoViewHolder) viewHolder).mp_video.isCurrentPlay()) {
                            ((VideoViewHolder) viewHolder).mp_video.seekToInAdvance = 0;
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


        public ListVideoAdapter(Context context, int width, int height) {
            mContext = context;
            mwidth = width;
            mheight = height;
        }

        @Override
        public void onBindViewHolder(final VideoViewHolder holder, final int position) {

            holder.itemView.findViewById(R.id.rl_author).setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("username", urlList.get(position).getUserName());
                intent.putExtra("id", urlList.get(position).getStudentId());
                boolean hasLogin = ((MainActivity)getActivity()).isHasLogin();
                intent.putExtra("has_login", hasLogin);
                intent.putExtra("follow_state", ((MainActivity)getActivity()).checkFollowState(urlList.get(position).getUserName()));
                if (hasLogin)
                    ((Activity)getActivity()).startActivityForResult(intent, MainActivity.USER_REQUEST_CODE);
                else
                    getActivity().startActivity(intent);
            });

            TextView authorTextView = holder.itemView.findViewById(R.id.tv_author);
            authorTextView.setText(urlList.get(position).getUserName());

            TextView authorIDTextView = holder.itemView.findViewById(R.id.tv_author_id);
            authorIDTextView.setText(urlList.get(position).getStudentId());

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
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            final ImageView heart = holder.itemView.findViewById(R.id.iv_like);
                            heart.setVisibility(View.VISIBLE);
                            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.like_anim);
                            heart.startAnimation(anim);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    button.startAnimation(animation);
                }
            });


            holder.mp_video.setUp(urlList.get(position).getVideoUrl(), JZVideoPlayerStandard.CURRENT_STATE_NORMAL);
            // 一开始播放第一个视频
            if (position == 0){
                holder.mp_video.startVideo();
            }
//            holder.mtextView.setText(urlList.get(position));

            // 设置缩略图
            GlideBuilder builder = new GlideBuilder(getActivity());
            int diskSizeInBytes = 1024 * 1024 * 100;
            int memorySizeInBytes = 1024 * 1024 * 60;
            builder.setDiskCache(new InternalCacheDiskCacheFactory(getActivity(), diskSizeInBytes));
            builder.setMemoryCache(new LruResourceCache(memorySizeInBytes));
            Glide.with(getActivity())
                    .load(urlList.get(position).getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mp_video.thumbImageView);
            holder.mp_video.thumbImageView.setVisibility(View.VISIBLE);
        }

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.layout_recycler_vedio, null);

            JZVideoPlayerStandardLoopVideo videoPlayer = view.findViewById(R.id.videoplayer);
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

        private JZVideoPlayerStandardLoopVideo mp_video;
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

    private void fetchFeed() {
        miniDouyinService.getVideos().enqueue(new Callback<GetVideosResponse>() {
            @Override
            public void onResponse(Call<GetVideosResponse> call, Response<GetVideosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetVideosResponse getVideosResponse = response.body();
                    if (getVideosResponse.isSuccess()) {
                        urlList = getVideosResponse.getVideos();
                        for (int i = 0; i < urlList.size(); i++) {
                            likeList.add(false);
                        }
                        mrvVideo.getAdapter().notifyDataSetChanged();
//                        Toast.makeText(getContext(), "Refresh success!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getContext(), "Refresh fail!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "Refresh fail!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<GetVideosResponse> call, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
