package com.example.ballstars.home.store_fr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.ballstars.R;

import java.util.ArrayList;

public class SkinsRecyclerView {
    private final Context context;
    private final RecyclerView mRecyclerView;
    private SkinsRVAdapter adapter;
    private Drawable[] skinDrawables;
    private SnapHelper snapHelper;
    private final OnSnapChangeListener mOnSnapChangeListener;
    private final ArrayList<Integer> availableSkins;
    public SkinsRecyclerView(Activity activity,
                             RecyclerView recyclerView,
                             OnSnapChangeListener onSnapChangeListener,
                             ArrayList<Integer> availableSkins) {
        this.context = activity;
        mRecyclerView = recyclerView;
        mOnSnapChangeListener = onSnapChangeListener;
        this.availableSkins = availableSkins;
        setUpDrawableArr();
        setUpRecyclerView();
    }
    private void setUpDrawableArr() {
        skinDrawables = new Drawable[StoreFragment.indexToSkinId.length];
        for (int i = 0; i < StoreFragment.indexToSkinId.length; i++) {
            skinDrawables[i] = ResourcesCompat.getDrawable(
                    context.getResources(),
                    StoreFragment.indexToSkinId[i],
                    context.getTheme()
            );
        }
    }

    private int getSnapPosition(SnapHelper snapHelper) {
        RecyclerView.LayoutManager manager = mRecyclerView.getLayoutManager();
        View view = snapHelper.findSnapView(manager);
        return manager.getPosition(view);
    }

    private void setUpRecyclerView() {
        adapter = new SkinsRVAdapter(context,
                skinDrawables,
                ResourcesCompat.getDrawable(context.getResources(), R.drawable.lock_icon, context.getTheme()),
                availableSkins);

        mRecyclerView.setAdapter(adapter);
        ScaleItemLayoutManager scaleItemLayoutManager =
                new ScaleItemLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(scaleItemLayoutManager);

        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(new HorizontalSpacingItemDecoration());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mOnSnapChangeListener.onSnapChange(getSnapPosition(snapHelper));
            }
        });

    }

    public void buyOnIndex(int i) {
        adapter.buyOnIndex(i);
        adapter.notifyItemChanged(i);
    }

    private static class SkinsRVAdapter extends RecyclerView.Adapter<SkinsRVAdapter.ViewHolder> {
        private final Context context;
        private final Drawable[] skinDrawables;
        private final Drawable[] updatedSkinDrawables;
        private final Drawable lockIcon;
        private final ArrayList<Integer> availableSkins;
        public SkinsRVAdapter(Context context, Drawable[] skinDrawables, Drawable lockIcon, ArrayList<Integer> availableSkins) {
            this.context = context;
            this.skinDrawables = skinDrawables;
            updatedSkinDrawables = new Drawable[skinDrawables.length];
            this.lockIcon = lockIcon;
            this.availableSkins = availableSkins;
            setUpSkinDrawablesLocked();

        }
        public void buyOnIndex(int i) {
            updatedSkinDrawables[i] = skinDrawables[i];
        }

        private void setUpSkinDrawablesLocked() {
            for (int i = 0; i < skinDrawables.length; i++) {
                if (availableSkins.get(i) == 0) {
                    updatedSkinDrawables[i] = lockIcon;
                } else {
                    updatedSkinDrawables[i] = skinDrawables[i];
                }
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.skin_holder, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SkinsRVAdapter.ViewHolder holder, int position) {
            holder.ivSkin.setImageDrawable(updatedSkinDrawables[position]);
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.recycler_enter_animation));
        }

        @Override
        public int getItemCount() {
            return skinDrawables.length;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            // creating variables for our text views.
            private final ImageView ivSkin;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivSkin = itemView.findViewById(R.id.ivSkin);
            }
        }
    }

    private static class HorizontalSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int ITEM_SPACING = 100;
        private final int FIRST_AND_LAST_SPACING = 1000;

        public HorizontalSpacingItemDecoration() {

        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.left = FIRST_AND_LAST_SPACING;
                outRect.right = ITEM_SPACING;
            } else if (position == state.getItemCount() - 1) {
                outRect.left = ITEM_SPACING;
                outRect.right = FIRST_AND_LAST_SPACING;
            } else {
                outRect.left = ITEM_SPACING;
                outRect.right = ITEM_SPACING;
            }
        }
    }

    private static class ScaleItemLayoutManager extends LinearLayoutManager {

        private final float SHRINK_AMOUNT = 0.25f;
        private final float SHRINK_DISTANCE = 0.6f;

        public ScaleItemLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        private void scaleMiddleItem() {
            float midpoint = getWidth() / 2f;
            float d0 = 0f;
            float d1 = SHRINK_DISTANCE * midpoint;
            float s0 = 1.2f;
            float s1 = 1f - SHRINK_AMOUNT;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                float childMidpoint = child.getX() + child.getWidth() / 2.0f;

                float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                child.setScaleX(scale);
                child.setScaleY(scale);
            }
        }

        @Override
        public void onLayoutCompleted(RecyclerView.State state) {
            super.onLayoutCompleted(state);
            scaleMiddleItem();
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);

            scaleMiddleItem();

            return scrolled;
        }

    }

    public interface OnSnapChangeListener {
        void onSnapChange(int pos);
    }
}
