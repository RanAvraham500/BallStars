package com.example.ballstars.dialogs.leaderboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ballstars.R;
import com.example.ballstars.home.store_fr.SkinsRecyclerView;

import java.util.ArrayList;

public class LeaderboardRecyclerView {
    private final LeaderboardRVAdapter adapter;
    public LeaderboardRecyclerView(Activity activity, RecyclerView recyclerView, ArrayList<LeaderboardPlace> leaderboard) {
        adapter = new LeaderboardRVAdapter(activity, leaderboard);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new SpacingItemDecoration());
    }

    /**פעולה המעדכנת את הRecyclerView*/
    public void updateLeaderboard(ArrayList<LeaderboardPlace> leaderboard) {
        adapter.setLeaderboard(leaderboard);
        adapter.notifyDataSetChanged();
    }

    private static class LeaderboardRVAdapter extends RecyclerView.Adapter<LeaderboardRecyclerView.LeaderboardRVAdapter.ViewHolder> {
        private final Activity activity;
        private ArrayList<LeaderboardPlace> leaderboard;

        public LeaderboardRVAdapter(Activity activity, ArrayList<LeaderboardPlace> leaderboard) {
            this.activity = activity;
            this.leaderboard = leaderboard;
        }

        public void setLeaderboard(ArrayList<LeaderboardPlace> leaderboard) {
            this.leaderboard = leaderboard;
        }

        @NonNull
        @Override
        public LeaderboardRecyclerView.LeaderboardRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LeaderboardRecyclerView.LeaderboardRVAdapter.ViewHolder(
                    LayoutInflater.from(activity).inflate(R.layout.leaderboard_place_holder, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull LeaderboardRecyclerView.LeaderboardRVAdapter.ViewHolder holder, int position) {
            holder.tvUserPlace.setText(position + 1 + "");
            if (position < leaderboard.size()) {
                if (leaderboard.get(position) != null) {
                    holder.tvUserEmail.setText(leaderboard.get(position).getEmail() + "");
                    holder.tvUserScore.setText(leaderboard.get(position).getScore() + "");
                } else {
                    holder.tvUserEmail.setText("-");
                    holder.tvUserScore.setText("-");
                }
            } else {
                holder.tvUserEmail.setText("-");
                holder.tvUserScore.setText("-");
            }
            //holder.itemView.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.recycler_enter_animation));
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvUserPlace, tvUserEmail, tvUserScore;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvUserPlace = itemView.findViewById(R.id.tvUserPlace);
                tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
                tvUserScore = itemView.findViewById(R.id.tvUserScore);
            }
        }
    }

    private static class SpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int ITEM_SPACING = 5;
        private final int FIRST_SPACING = 20;
        private final int LAST_SPACING = 200;

        public SpacingItemDecoration() {

        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.top = FIRST_SPACING;
                outRect.bottom = ITEM_SPACING;
            } else if (position == state.getItemCount() - 1) {
                outRect.top = ITEM_SPACING;
                outRect.bottom = LAST_SPACING;
            } else {
                outRect.top = ITEM_SPACING;
                outRect.bottom = ITEM_SPACING;
            }
        }
    }

}
