package com.bytedance.androidcamp.minidouyin.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bytedance.androidcamp.minidouyin.MainActivity;
import com.bytedance.androidcamp.minidouyin.R;
import com.bytedance.androidcamp.minidouyin.activity.UserActivity;
import com.bytedance.androidcamp.minidouyin.model.GetVideosResponse;
import com.bytedance.androidcamp.minidouyin.model.IMiniDouyinService;
import com.bytedance.androidcamp.minidouyin.model.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DiscoverFragment extends Fragment {

    private List<Video> mVideos = new ArrayList<>();

    public final String BASE_URL = "http://test.androidcamp.bytedance.com/mini_douyin/invoke/";

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private IMiniDouyinService miniDouyinService =retrofit.create(IMiniDouyinService.class);

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String userName = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.fragment_discover, container, false);
        mRecyclerView = mView.findViewById(R.id.rv_find);
        /* 设定瀑布流layout */
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return CardViewHolder.create(getContext(), parent);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((CardViewHolder)holder).bind(mVideos.get(position));
            }

            @Override
            public int getItemCount() {
                return mVideos.size();
            }
        });
        mSwipeRefreshLayout = mView.findViewById(R.id.srl_discover);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (userName == null)
            fetchFeed();
        else
            fetchFeed(userName);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (userName == null)
                    fetchFeed();
                else
                    fetchFeed(userName);
            }
        });
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImage;
        public TextView mAuthorTextView;
        public TextView mTimeTextView;
        private Context mContext = null;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.iv_cover);
            mAuthorTextView = itemView.findViewById(R.id.tv_author);
            mTimeTextView = itemView.findViewById(R.id.tv_time);
        }

        static public CardViewHolder create(Context context, ViewGroup root) {
            View v = LayoutInflater.from(context).inflate(R.layout.layout_recycler_item, root, false);
            CardViewHolder viewHolder = new CardViewHolder(v);
            viewHolder.mContext = context;
            return viewHolder;
        }

        public void bind(@NonNull final Video video) {
            mTimeTextView.setText(video.getDate());
            mAuthorTextView.setText(video.getUserName());
            mAuthorTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, UserActivity.class);
                    intent.putExtra("username", video.getUserName());
                    intent.putExtra("id", video.getStudentId());
                    mContext.startActivity(intent);
                }
            });
            Glide.with(mImage.getContext()).load(video.getImageUrl()).into(mImage);
//            ConstraintLayout.LayoutParams layputParams =
//                    (ConstraintLayout.LayoutParams)this.mImage.getLayoutParams();
//            float imageW = video.getImageWidth();
//            float scale = (float) layputParams.width / imageW;
//            layputParams.height = (int)(scale * video.getImageHeight());
//            this.mImage.setLayoutParams(layputParams);
        }


    }

    public void fetchFeed() {
        miniDouyinService.getVideos().enqueue(new Callback<GetVideosResponse>() {
            @Override
            public void onResponse(Call<GetVideosResponse> call, Response<GetVideosResponse> response) {
                if (getActivity().findViewById(R.id.fab_ref).getAnimation() != null) {
                    getActivity().findViewById(R.id.fab_ref).getAnimation().setRepeatCount(2);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    GetVideosResponse getVideosResponse = response.body();
                    if (getVideosResponse.isSuccess()) {
                        mVideos = getVideosResponse.getVideos();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
//                        Toast.makeText(getContext(), "Refresh success!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getContext(), "Refresh fail!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "Refresh fail!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<GetVideosResponse> call, Throwable throwable) {
                if (getActivity().findViewById(R.id.fab_ref).getAnimation() != null) {
                    getActivity().findViewById(R.id.fab_ref).getAnimation().setRepeatCount(2);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                throwable.printStackTrace();
                Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void fetchFeed(final String userName) {
        miniDouyinService.getVideos().enqueue(new Callback<GetVideosResponse>() {
            @Override
            public void onResponse(Call<GetVideosResponse> call, Response<GetVideosResponse> response) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    GetVideosResponse getVideosResponse = response.body();
                    if (getVideosResponse.isSuccess()) {
                        mVideos = getVideosResponse.getUserVideos(userName);
                        mRecyclerView.getAdapter().notifyDataSetChanged();
//                        Toast.makeText(getContext(), "Refresh success!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getContext(), "Refresh fail!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "Refresh fail!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<GetVideosResponse> call, Throwable throwable) {
                mSwipeRefreshLayout.setRefreshing(false);
                throwable.printStackTrace();
                Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
