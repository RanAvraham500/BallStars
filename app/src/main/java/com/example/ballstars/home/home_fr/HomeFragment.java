package com.example.ballstars.home.home_fr;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ballstars.CustomView;
import com.example.ballstars.R;
import com.example.ballstars.ReplaceFragment;
import com.example.ballstars.dialogs.InfoDialog;
import com.example.ballstars.firebase.CommunicateWithFirebase;
import com.example.ballstars.firebase.User;
import com.example.ballstars.game.GameActivity;
import com.example.ballstars.OnFragmentChangeListener;
import com.example.ballstars.home.levels_fr.LevelsFragment;
import com.example.ballstars.home.store_fr.StoreFragment;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private CustomView<AppCompatButton> btnPlay, btnGoLevels;
    private CustomView<ImageButton> btnGoStore, btnInfo;
    private CustomView<TextView> tvHeadline, tvStoreTag, tvLevelTag;
    private CustomView<ImageView> ivCircleBackground;
    private OnFragmentChangeListener onFragmentChangeListener;
    private User user;
    private CommunicateWithFirebase firebase;
    private int skinId = R.drawable.skin_bright_blue;
    private String levelName;

    public HomeFragment() {
        // Required empty public constructor
    }
    public HomeFragment(OnFragmentChangeListener onFragmentChangeListener, User user) {
        this.onFragmentChangeListener = onFragmentChangeListener;
        this.user = user;
        if (user.getChosenSkin() == 0) {
            user.setChosenSkin(skinId);
        }
        skinId = user.getChosenSkin();
        levelName = user.getChosenLevel();
    }
    public static HomeFragment newInstance(OnFragmentChangeListener onFragmentChangeListener, User user) {
        return new HomeFragment(onFragmentChangeListener, user);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        onFragmentChangeListener.onFragmentChange(this);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnPlay = new CustomView<>(getActivity(), view.findViewById(R.id.btnPlay), this);
        btnGoStore = new CustomView<>(getActivity(), view.findViewById(R.id.btnGoStore), this);
        setStoreButtonSkinBitmap();
        btnGoLevels = new CustomView<>(getActivity(), view.findViewById(R.id.btnGoLevels), this);
        setLevelButtonName();
        btnInfo = new CustomView<>(requireActivity(), view.findViewById(R.id.btnInfo), this);
        ivCircleBackground = new CustomView<>(view.findViewById(R.id.ivCircleBackgroundHome));

        tvHeadline = new CustomView<>(view.findViewById(R.id.tvHeadline));
        tvLevelTag = new CustomView<>(view.findViewById(R.id.tvLevelTag));
        tvStoreTag = new CustomView<>(view.findViewById(R.id.tvStoreTag));

        firebase = new CommunicateWithFirebase(requireActivity(), getParentFragmentManager());
        return view;
    }
    private void setStoreButtonSkinBitmap() {
        Bitmap storeSkinBitmap =  Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), skinId),
                130,
                130,
                false
        );
        btnGoStore.getView().setImageBitmap(storeSkinBitmap);
    }
    private void setLevelButtonName() {
        btnGoLevels.getView().setText(levelName);
    }

    private void disappearAllViewsButCircle() {
        new CustomView<ImageButton>(requireActivity(), requireActivity().findViewById(R.id.btnUserNav), null).disappear();
        btnInfo.disappear();
        btnPlay.disappear();
        btnGoLevels.disappear();
        btnGoStore.disappear();
        tvHeadline.disappear();
        tvLevelTag.disappear();
        tvStoreTag.disappear();
        ivCircleBackground.getView().bringToFront();
    }
    private void startGoToGameAnimation() {
        disappearAllViewsButCircle();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        ObjectAnimator animX = ObjectAnimator.ofFloat(ivCircleBackground.getView(), "scaleX",
                1f, (float) screenWidth / 725);
        ObjectAnimator animY = ObjectAnimator.ofFloat(ivCircleBackground.getView(), "scaleY",
                1f, (float) screenHeight / 725);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        animSetXY.setDuration(1500);

        animSetXY.start();
    }
    public void scheduledIntent() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra("user", user);
                requireActivity().startActivity(intent);
            }
        }, 1500);
    }

    @Override
    public void onClick(View view) {
        if (btnPlay.isView(view)) {
            firebase.updateUser(user, () -> {
                startGoToGameAnimation();
                scheduledIntent();
            });
        } else if (btnGoStore.isView(view)) {
            ReplaceFragment.init(getParentFragmentManager(), StoreFragment.newInstance(onFragmentChangeListener, user));
        } else if (btnGoLevels.isView(view)){
            ReplaceFragment.init(getParentFragmentManager(), LevelsFragment.newInstance(onFragmentChangeListener, user));
        } else {
            InfoDialog.display(getParentFragmentManager(), InfoDialog.InfoDialogType.HOME);
        }
    }
}