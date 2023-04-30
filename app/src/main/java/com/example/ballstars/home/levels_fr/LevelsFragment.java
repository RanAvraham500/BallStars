package com.example.ballstars.home.levels_fr;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.ballstars.CustomView;
import com.example.ballstars.R;
import com.example.ballstars.OnFragmentChangeListener;
import com.example.ballstars.ReplaceFragment;
import com.example.ballstars.firebase.CommunicateWithFirebase;
import com.example.ballstars.firebase.User;
import com.example.ballstars.home.home_fr.HomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class LevelsFragment extends Fragment implements View.OnClickListener {
    private CustomView<ImageButton> btnPrev, btnNext;
    private CustomView<AppCompatButton> btnSelectLevel;
    private CustomViewPager vpLevels;
    private OnFragmentChangeListener onFragmentChangeListener;
    private User user;

    public LevelsFragment() {
        // Required empty public constructor
    }
    public LevelsFragment(OnFragmentChangeListener onFragmentChangeListener, User user) {
        this.onFragmentChangeListener = onFragmentChangeListener;
        this.user = user;
    }

    public static LevelsFragment newInstance(OnFragmentChangeListener onFragmentChangeListener, User user) {
       return new LevelsFragment(onFragmentChangeListener, user);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        onFragmentChangeListener.onFragmentChange(this);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_levels, container, false);

        btnPrev = new CustomView<>(requireActivity(), view.findViewById(R.id.btnPrevLevel), this);
        btnPrev.getView().setVisibility(View.GONE);
        btnNext = new CustomView<>(requireActivity(), view.findViewById(R.id.btnNextLevel), this);

        ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateUI();
            }
        };

        vpLevels = new CustomViewPager(view.findViewById(R.id.vpLevels), requireActivity());
        vpLevels.setOnPageChangeCallback(onPageChangeCallback);

        //bringing the user nav button to front, that the viewpager2 won't block it
        requireActivity().findViewById(R.id.btnUserNav).bringToFront();

        btnSelectLevel = new CustomView<>(requireActivity(), view.findViewById(R.id.btnSelectLevel), this);

        return view;
    }

    private void updateUI() {
        switch (vpLevels.getCurrIndex()) {
            case 0 -> {
                btnPrev.disappear();
                btnNext.show();
            }
            case 1 -> {
                btnPrev.getView().setVisibility(View.VISIBLE);
                btnPrev.show();
                btnNext.disappear();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (btnPrev.isView(view)) {
            vpLevels.showPrev();
        } else if (btnNext.isView(view)){
            vpLevels.showNext();
        } else {
            user.setChosenLevel(vpLevels.getDisplayedLevel());
            CommunicateWithFirebase firebase = new CommunicateWithFirebase(requireActivity(), getParentFragmentManager());
            firebase.updateUser(user, () ->
                    ReplaceFragment.init(getParentFragmentManager(), HomeFragment.newInstance(onFragmentChangeListener, user))
            );
        }
    }
}