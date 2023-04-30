package com.example.ballstars.firebase;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;

import com.example.ballstars.R;
import com.example.ballstars.dialogs.LoadingDialog;
import com.example.ballstars.dialogs.leaderboard.LeaderboardPlace;
import com.example.ballstars.home.HomeNavigationActivity;
import com.example.ballstars.registration.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CommunicateWithFirebase {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore mDB = FirebaseFirestore.getInstance();
    public static final String COINS_PATH = "coins", MAZE_PATH = "maze";
    private final Activity activity;
    private final FragmentManager fragmentManager;

    public CommunicateWithFirebase(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    public void signIn(String email, String password) {
        LoadingDialog loadingDialog =  LoadingDialog.display(fragmentManager);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getUserDocumentFromDB().get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        loadingDialog.success(() -> {
                                            User user = documentSnapshot.toObject(User.class);
                                            goToActivity(HomeNavigationActivity.class, user);
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        DBFailed(loadingDialog, "Failed to access database");
                                    }
                                });
                        } else {
                            DBFailed(loadingDialog, "Wrong email or password ");
                        }
                    }
                });
    }

    public void signUp(String email, String username, String password) {
        LoadingDialog loadingDialog =  LoadingDialog.display(fragmentManager);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(email, username);
                            getUserDocumentFromDB().set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        loadingDialog.success(() -> goToActivity(HomeNavigationActivity.class, user));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        DBFailed(loadingDialog, "Failed to access database");
                                    }
                                });
                        } else {
                            DBFailed(loadingDialog, "Error while creating an account");
                        }
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
        goToActivity(RegistrationActivity.class, new User());
    }

    public boolean ifLoggedUser() {
        return mAuth.getCurrentUser() != null;
    }

    public void goToActivity(Class<?> activityClass, User user) {
        Intent intent = new Intent(activity, activityClass);
        intent.putExtra("user", user);
        activity.startActivity(intent);
    }

    public void updateUser(User user, LoadingDialog.OnLoadingFinishedListener listener) {
        LoadingDialog loadingDialog =  LoadingDialog.display(fragmentManager);
        getUserDocumentFromDB().set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loadingDialog.success(listener);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        DBFailed(loadingDialog, "Failed to access database");
                    }
                });
    }

    public void updateCoinsHighScore(int score, LoadingDialog.OnLoadingFinishedListener listener, User user) {
        user.setMoney(user.getMoney() + score);
        int userCoinsHighScore = user.getHighScore().getCoins();
        if (userCoinsHighScore < score) {
            LoadingDialog loadingDialog =  LoadingDialog.display(fragmentManager);

            user.getHighScore().setCoins(score);
            getLeaderboardDocumentFromDB(COINS_PATH).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    LinkedHashMap<String, Integer> leaderboard =
                            new LinkedHashMap<>(castMap(documentSnapshot.getData()));

                    if (leaderboard.containsKey(user.getEmail())) {
                        leaderboard.replace(user.getEmail(), score);
                        loadingDialog.success(listener);
                    }
                    else {
                        leaderboard.put(user.getEmail(), score);
                        if (leaderboard.size() > 10) {
                            leaderboard = sortMap(leaderboard);

                            leaderboard.remove(
                                    String.valueOf(leaderboard.keySet().toArray()[leaderboard.size() - 1])
                            );
                        }
                    }
                    getLeaderboardDocumentFromDB(COINS_PATH).set(leaderboard)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loadingDialog.success(listener);
                                    } else {
                                        DBFailed(loadingDialog, "Failed to access database");
                                    }
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    DBFailed(loadingDialog, "Failed to access database");
                }
            });
        } else {
            listener.onLoadingFinished();
        }
        getUserDocumentFromDB().set(user);
    }
    public void updateMazeHighScore(int score, LoadingDialog.OnLoadingFinishedListener listener, User user) {
        user.setMoney(user.getMoney() + score);
        int userMazeHighScore = user.getHighScore().getMaze();
        if (userMazeHighScore < score) {
            LoadingDialog loadingDialog =  LoadingDialog.display(fragmentManager);

            user.getHighScore().setMaze(score);
            getLeaderboardDocumentFromDB(MAZE_PATH).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    LinkedHashMap<String, Integer> leaderboard =
                            new LinkedHashMap<>(castMap(documentSnapshot.getData()));

                    if (leaderboard.containsKey(user.getEmail())) {
                        leaderboard.replace(user.getEmail(), score);
                        loadingDialog.success(listener);
                    }
                    else {
                        leaderboard.put(user.getEmail(), score);
                        if (leaderboard.size() > 10) {
                            leaderboard = sortMap(leaderboard);

                            leaderboard.remove(
                                    String.valueOf(leaderboard.keySet().toArray()[leaderboard.size() - 1])
                            );
                        }
                    }
                    getLeaderboardDocumentFromDB(MAZE_PATH).set(leaderboard)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loadingDialog.success(listener);
                                    } else {
                                        DBFailed(loadingDialog, "Failed to access database");
                                    }
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    DBFailed(loadingDialog, "Failed to access database");
                }
            });
        } else {
            listener.onLoadingFinished();
        }
        getUserDocumentFromDB().set(user);
    }

    public Map<String, Integer> castMap(Map<?, ?> map) {
        return map
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> Integer.parseInt(e.getValue().toString())
                ));
    }

    public LinkedHashMap<String, Integer> sortMap(LinkedHashMap<String, Integer> map) {
        return map
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new)
                );
    }

    public ArrayList<LeaderboardPlace> castToLeaderboardArray(LinkedHashMap<String, Integer> map) {
        ArrayList<LeaderboardPlace> leaderboard = new ArrayList<>();

        for (Map.Entry<String, Integer> mapEntry: map.entrySet()) {
            leaderboard.add(new LeaderboardPlace(mapEntry.getKey(), mapEntry.getValue()));
        }

        return leaderboard;
    }

    public DocumentReference getUserDocumentFromDB() {
        return mDB.collection("users").document(mAuth.getCurrentUser().getEmail());
    }

    public DocumentReference getLeaderboardDocumentFromDB(String path) {
        return mDB.collection("leaderboard").document(path);
    }

    public void DBFailed(LoadingDialog loadingDialog, String message) {
        //finish loading with error
        loadingDialog.failure();
        createSnackBar(message);
    }

    private void createSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(activity.findViewById(R.id.llContent), message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ResourcesCompat.getColor(activity.getResources(), R.color.secondary_color, activity.getTheme()));
        snackbar.setTextColor(ResourcesCompat.getColor(activity.getResources(), R.color.main_color, activity.getTheme()));
        View view = snackbar.getView();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.CENTER | Gravity.BOTTOM;
        lp.width = 600;
        view.setLayoutParams(lp);
        snackbar.show();
    }
}