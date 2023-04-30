package com.example.ballstars.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ballstars.dialogs.settings.SettingsDialog;
import com.example.ballstars.dialogs.leaderboard.LeaderboardDialog;
import com.example.ballstars.firebase.CommunicateWithFirebase;
import com.example.ballstars.CustomView;
import com.example.ballstars.OnFragmentChangeListener;
import com.example.ballstars.R;
import com.example.ballstars.ReplaceFragment;
import com.example.ballstars.firebase.User;
import com.example.ballstars.home.home_fr.HomeFragment;
import com.example.ballstars.home.levels_fr.LevelsFragment;
import com.example.ballstars.home.store_fr.StoreFragment;
import com.example.ballstars.service.BallStarsService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

public class HomeNavigationActivity extends AppCompatActivity implements View.OnClickListener, OnFragmentChangeListener {
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private CustomView<ImageButton> btnUserNav, btnLogOut, btnSettings, btnSocial;
    private CommunicateWithFirebase firebase;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_navigation);
        overridePendingTransition(0, 0); //no enter animation

        user = (User) getIntent().getSerializableExtra("user");

        firebase = new CommunicateWithFirebase(this, getSupportFragmentManager());

        mDrawer = findViewById(R.id.drawer);

        nvDrawer = findViewById(R.id.nvView);
        setupDrawerContent();

        ReplaceFragment.init(getSupportFragmentManager(), HomeFragment.newInstance(this, user));

        btnUserNav = new CustomView<>(this, findViewById(R.id.btnUserNav), this);
    }
    private ImageButton setUpDrawerButton(int srcId) {
        LinearLayout.LayoutParams lp = new
                LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (srcId != R.drawable.chart_icon) {
            lp.setMarginEnd(40);
        }
        lp.topMargin = 10;
        lp.bottomMargin = 30;
        ImageButton button = new ImageButton(this);
        button.setLayoutParams(lp);
        button.setImageResource(srcId);
        button.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_bg, getTheme()));
        return button;
    }
    private void setupDrawerContent() {
        View view  = nvDrawer.getHeaderView(0);
        TextView tvHeaderUsername = view.findViewById(R.id.tvHeaderUsername);
        TextView tvHeaderEmail = view.findViewById(R.id.tvHeaderEmail);
        tvHeaderUsername.setText(user.getUsername());
        tvHeaderEmail.setText(user.getEmail());

        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        //setting up the custom buttons in the navigation drawer
        btnLogOut = new CustomView<>(this, setUpDrawerButton(R.drawable.logout_icon), this);
        btnSettings = new CustomView<>(this, setUpDrawerButton(R.drawable.setting_icon), this);
        btnSocial = new CustomView<>(this, setUpDrawerButton(R.drawable.chart_icon), this);

        //setting up the linearLayout
        LinearLayout buttonsContainer = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonsContainer.setLayoutParams(lp);
        buttonsContainer.setGravity(Gravity.CENTER);
        buttonsContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonsContainer.addView(btnLogOut.getView());
        buttonsContainer.addView(btnSettings.getView());
        buttonsContainer.addView(btnSocial.getView());

        nvDrawer.addHeaderView(buttonsContainer);
    }

    private void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        @SuppressLint("NonConstantResourceId")
        Fragment fragment = switch (menuItem.getItemId()) {
            case R.id.nav_home_fragment -> HomeFragment.newInstance(this, user);
            case R.id.nav_level_fragment -> LevelsFragment.newInstance(this, user);
            case R.id.nav_store_fragment -> StoreFragment.newInstance(this, user);
            default -> null;
        };

        // Insert the fragment by replacing any existing fragment
        ReplaceFragment.init(getSupportFragmentManager(), fragment);

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public void onClick(View v) {
        if (btnUserNav.isView(v)) {
            mDrawer.open();
        } else if (btnSettings.isView(v)) {
            SettingsDialog.display(getSupportFragmentManager(), user);

        } else if (btnLogOut.isView(v)){
            //Handle log out
            firebase.signOut();
        } else {
            LeaderboardDialog.display(getSupportFragmentManager(), user);
        }
    }

    @Override
    public void onFragmentChange(Fragment fragment) {
        //access menu items and highlight them
        Menu menu = nvDrawer.getMenu();
        if (fragment instanceof HomeFragment) {
            menu.getItem(0).setChecked(true);
        } else if (fragment instanceof StoreFragment) {
            menu.getItem(1).setChecked(true);
        } else {
            menu.getItem(2).setChecked(true);
        }
    }
}