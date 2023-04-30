package com.example.ballstars.home.store_fr;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ballstars.CustomView;
import com.example.ballstars.R;
import com.example.ballstars.OnFragmentChangeListener;
import com.example.ballstars.ReplaceFragment;
import com.example.ballstars.firebase.CommunicateWithFirebase;
import com.example.ballstars.firebase.User;
import com.example.ballstars.home.home_fr.HomeFragment;

import java.util.ArrayList;

public class StoreFragment extends Fragment implements SkinsRecyclerView.OnSnapChangeListener, View.OnClickListener {
    private CustomView<TextView> tvSkinName, tvSkinPrice, tvMoney;
    private CustomView<AppCompatButton> btnSelectSkin;
    private SkinsRecyclerView recyclerview;
    private String[] indexToSkinName;
    private int[] indexToSkinPrice;
    private int i;
    private OnFragmentChangeListener onFragmentChangeListener;
    private User user;
    private ArrayList<Integer> availableSkins;
    private CommunicateWithFirebase firebase;
    public static final int[] indexToSkinId = {
            R.drawable.skin_bright_blue,
            R.drawable.skin_bright_red,
            R.drawable.skin_bright_green,
            R.drawable.skin_pink,
            R.drawable.skin_bright_orange,
            R.drawable.skin_yellow,
            R.drawable.skin_blue,
            R.drawable.skin_red,
            R.drawable.skin_green,
            R.drawable.skin_purple,
            R.drawable.skin_orange,
            R.drawable.skin_white,
            R.drawable.skin_grey,
            R.drawable.skin_black,
            R.drawable.skin_football,
            R.drawable.skin_basketball,
            R.drawable.skin_among_us,
            R.drawable.skin_seal,
            R.drawable.skin_sami_the_fireman,
            R.drawable.skin_israel,
            R.drawable.skin_troll,
            R.drawable.skin_andrew_tate,
            R.drawable.skin_yummi,
            R.drawable.skin_hasbulla,
            R.drawable.skin_ski_bi_dub,
    };

    public StoreFragment() {
        // Required empty public constructor
    }
    public StoreFragment(OnFragmentChangeListener onFragmentChangeListener, User user) {
        this.onFragmentChangeListener = onFragmentChangeListener;
        this.user = user;
        availableSkins = user.getAvailableSkins();
    }
    public static StoreFragment newInstance(OnFragmentChangeListener onFragmentChangeListener, User user) {
        return new StoreFragment(onFragmentChangeListener, user);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebase = new CommunicateWithFirebase(requireActivity(), getParentFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        onFragmentChangeListener.onFragmentChange(this);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        recyclerview = new SkinsRecyclerView(requireActivity(),
                view.findViewById(R.id.rvSkins),
                this,
                availableSkins);
        tvSkinName = new CustomView<>(view.findViewById(R.id.tvSkinName));
        tvSkinPrice = new CustomView<>(view.findViewById(R.id.tvSkinPrice));
        tvSkinPrice.getView().setVisibility(View.INVISIBLE);

        tvMoney = new CustomView<>(view.findViewById(R.id.tvMoney));
        tvMoney.getView().setText(user.getMoney() + "$");

        btnSelectSkin = new CustomView<>(getActivity(), view.findViewById(R.id.btnSelectSkin), this);

        indexToSkinName = getResources().getStringArray(R.array.index_to_skin_names);
        indexToSkinPrice = getResources().getIntArray(R.array.index_to_skin_price);

        return view;
    }

    @Override
    public void onSnapChange(int pos) {
        i = pos;
        if (availableSkins.get(pos) == 0) {
            btnSelectSkin.getView().setText("BUY");
            tvSkinPrice.getView().setVisibility(View.VISIBLE);
            tvSkinPrice.show();
        } else {
            btnSelectSkin.getView().setText("SELECT");
            tvSkinPrice.disappear();
        }

        tvSkinName.getView().setText(indexToSkinName[pos]);

        tvSkinPrice.getView().setText(indexToSkinPrice[pos] + "$");
    }

    @Override
    public void onClick(View view) {
        if (btnSelectSkin.getView().getText().equals("SELECT")) {
            user.setChosenSkin(indexToSkinId[i]);
            firebase.updateUser(user, () ->
                ReplaceFragment.init(getParentFragmentManager(), HomeFragment.newInstance(onFragmentChangeListener, user))
            );
        } else {
            if (user.getMoney() >= indexToSkinPrice[i]) {
                user.getAvailableSkins().set(i, 1);
                user.setMoney(user.getMoney() - indexToSkinPrice[i]);
                firebase.updateUser(user, () -> {
                        recyclerview.buyOnIndex(i);
                        tvSkinPrice.disappear();
                        tvMoney.getView().setText(user.getMoney() + "$");
                        btnSelectSkin.getView().setText("SELECT");
                    }
                );
            } else {
                Toast.makeText(requireContext(), "Can't purchase :(", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

