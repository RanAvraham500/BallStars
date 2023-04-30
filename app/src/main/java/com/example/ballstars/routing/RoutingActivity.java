package com.example.ballstars.routing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.ballstars.firebase.CommunicateWithFirebase;
import com.example.ballstars.firebase.User;
import com.example.ballstars.home.HomeNavigationActivity;
import com.example.ballstars.registration.RegistrationActivity;
import com.example.ballstars.service.BallStarsService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class RoutingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        return false;
                    }
                });

        CommunicateWithFirebase firebase = new CommunicateWithFirebase(this, getSupportFragmentManager());
        if (!firebase.ifLoggedUser()) {
            startService(new Intent(this, BallStarsService.class));
            firebase.goToActivity(RegistrationActivity.class, new User());
            finish();
        }
        firebase.getUserDocumentFromDB().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                Intent intent = new Intent(RoutingActivity.this, BallStarsService.class);
                if (user.getUserSettings().isBackgroundMusicEnabled()) {
                    intent.addCategory("background_music");
                    intent.putExtra("volume", user.getUserSettings().getBackgroundMusicVolume());
                } else {
                    intent.addCategory("no_background_music");
                }
                RoutingActivity.this.startService(intent);

                firebase.goToActivity(HomeNavigationActivity.class, user);
                finish();
            }
        });

    }
}
