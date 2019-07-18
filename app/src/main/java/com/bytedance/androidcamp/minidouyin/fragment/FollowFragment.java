package com.bytedance.androidcamp.minidouyin.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bytedance.androidcamp.minidouyin.MainActivity;
import com.bytedance.androidcamp.minidouyin.R;
import com.bytedance.androidcamp.minidouyin.activity.UserActivity;
import com.bytedance.androidcamp.minidouyin.db.Follow;

import java.util.ArrayList;
import java.util.List;

public class FollowFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Follow> followList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.fragment_follow, container, false);
        mRecyclerView = mView.findViewById(R.id.rv_follow);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(new RecyclerView.Adapter<FollowViewHolder>() {
            @NonNull
            @Override
            public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return FollowViewHolder.create(getActivity(), parent);
            }

            @Override
            public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
                holder.bind(followList.get(position));
            }

            @Override
            public int getItemCount() {
                return followList.size();
            }
        });
        mSwipeRefreshLayout = mView.findViewById(R.id.srl_follow);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        followList = ((MainActivity)getActivity()).getFollowList();
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                followList = ((MainActivity)getActivity()).getFollowList();
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void updateFollowList(List<Follow> follows) {
        followList = follows;
        if (mRecyclerView != null && mRecyclerView.getAdapter() != null)
            mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private static class FollowViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;

        private TextView mFollowNameTextView;
        private TextView mFollowIDTextView;
        private AppCompatButton mFollowButton;

        private boolean followState = true;

        private static final int FOLLOW_BACKGROUND = 0xffdddddd;
        private static final int UNFOLLOW_BACKGROUND = 0xFFD81B60;
        private static final int FOLLOW_TEXTCOLOR = 0xffaaaaaa;
        private static final int UNFOLLOW_TEXTCOLOR = Color.WHITE;

        static public FollowViewHolder create(Context context, ViewGroup root) {
            View v = LayoutInflater.from(context).inflate(R.layout.layout_follow_item, root, false);
            FollowViewHolder viewHolder = new FollowViewHolder(v);
            viewHolder.mContext = context;
            return viewHolder;
        }

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            mFollowNameTextView = itemView.findViewById(R.id.tv_followname);
            mFollowIDTextView = itemView.findViewById(R.id.tv_followid);
            mFollowButton = itemView.findViewById(R.id.b_follow);
        }

        public void bind(@NonNull final Follow follow) {
            mFollowNameTextView.setText(follow.followName);
            mFollowIDTextView.setText(follow.followID);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, UserActivity.class);
                    i.putExtra("username", follow.followName);
                    i.putExtra("id", follow.followID);
                    i.putExtra("has_login", ((MainActivity)mContext).isHasLogin());
                    i.putExtra("follow_state", true);
                    ((Activity)mContext).startActivityForResult(i, MainActivity.USER_REQUEST_CODE);
                }
            });
            mFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followState = !followState;
                    mFollowButton.setText(followState ? R.string.follow_text : R.string.unfollow_text);
                    mFollowButton.setBackgroundColor(followState ? FOLLOW_BACKGROUND : UNFOLLOW_BACKGROUND);
                    mFollowButton.setTextColor(followState ? FOLLOW_TEXTCOLOR : UNFOLLOW_TEXTCOLOR);
                    ((MainActivity)mContext).updateFollow(followState, follow);
                }
            });
            followState = true;
            mFollowButton.setText(R.string.follow_text);
            mFollowButton.setBackgroundColor(FOLLOW_BACKGROUND);
            mFollowButton.setTextColor(FOLLOW_TEXTCOLOR);

        }
    }

}
