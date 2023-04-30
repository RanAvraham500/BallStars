package com.example.ballstars.home.levels_fr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ballstars.CustomView;
import com.example.ballstars.R;

public class CustomViewPager {
    private final ViewPager2 mViewPager2;
    private final String[] levelNames;
    public CustomViewPager(ViewPager2 viewPager2, Activity activity) {
        mViewPager2 = viewPager2;
        levelNames = activity.getResources().getStringArray(R.array.level_names);
        mViewPager2.setAdapter(new ViewPager2Adapter(activity, levelNames));
    }
    public void setOnPageChangeCallback(ViewPager2.OnPageChangeCallback onPageChangeCallback) {
        mViewPager2.registerOnPageChangeCallback(onPageChangeCallback);
    }

    public int getCurrIndex() {
        return mViewPager2.getCurrentItem();
    }

    public String getDisplayedLevel() {
        return levelNames[getCurrIndex()];
    }

    public void showNext() {
        mViewPager2.setCurrentItem(getCurrIndex()+1);
    }

    public void showPrev() {
        mViewPager2.setCurrentItem(getCurrIndex()-1);
    }

    private static class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewHolder> {
        private final Activity activity;
        private final String[] levelNames;
        public ViewPager2Adapter(Activity activity, String[] levelNames) {
            this.activity = activity;
            this.levelNames = levelNames;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.level_holder, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvLevel.getView().setText(levelNames[position]);
        }

        @Override
        public int getItemCount() {
            return levelNames.length;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            CustomView<TextView> tvLevel;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvLevel = new CustomView<>(itemView.findViewById(R.id.tvLevel)) ;
            }
        }
    }
}
