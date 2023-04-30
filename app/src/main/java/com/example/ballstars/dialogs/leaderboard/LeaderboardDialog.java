package com.example.ballstars.dialogs.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.ballstars.R;
import com.example.ballstars.dialogs.LoadingDialog;
import com.example.ballstars.firebase.CommunicateWithFirebase;
import com.example.ballstars.firebase.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class LeaderboardDialog extends DialogFragment implements MaterialButtonToggleGroup.OnButtonCheckedListener {
    private static final String TAG = "leaderboard_dialog";
    private User user;
    private View view;
    private CommunicateWithFirebase firebase;
    private ViewStub viewStub;
    private TextView tvPersonalPlace, tvPersonalEmail, tvPersonalScore;
    private MaterialButtonToggleGroup btgLeaderboard;
    private LeaderboardRecyclerView recyclerView;
    private ArrayList<LeaderboardPlace> leaderboardCoins, leaderboardMaze;
    private LoadingDialog loadingDialog;
    private boolean oneQueryFinished;
    private int userCoinsPlace, userMazePlace;

    public LeaderboardDialog() {}
    public LeaderboardDialog(User user) {
        this.user = user;
    }

    /**פעולה המציגה את הדיאלוג על המסך ומחזירה אותו*/
    public static LeaderboardDialog display(FragmentManager fragmentManager, User user) {
        LeaderboardDialog dialog = new LeaderboardDialog(user);
        dialog.show(fragmentManager, TAG);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.ThemeOverlay_Material);

        firebase = new CommunicateWithFirebase(requireActivity(), getParentFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dialog_leaderboard, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Leaderboard");
        toolbar.inflateMenu(R.menu.leaderboard_refresh);
        toolbar.setOnMenuItemClickListener(item -> {
            refreshLeaderboard();
            return true;
        });
        toolbar.setNavigationOnClickListener(v -> dismiss());

        btgLeaderboard = view.findViewById(R.id.btgLeaderboard);
        btgLeaderboard.addOnButtonCheckedListener(this);

        firstLoadUp();

        return view;
    }

    /**מציגה את הנתונים על הפודיום בטעינה הראשונה*/
    private void firstLoadUp() {
        loadingDialog = LoadingDialog.display(getParentFragmentManager());
        oneQueryFinished = false;
        LoadingDialog.OnLoadingFinishedListener listener = new LoadingDialog.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                userCoinsPlace = getUserCoinsPlace();
                userMazePlace = getUserMazePlace();
                setUpViewStub();
                recyclerView = new LeaderboardRecyclerView(requireActivity(), view.findViewById(R.id.rvLeaderboard), leaderboardCoins);
            }
        };
        setUpCoinsLeaderboard(listener);
        setUpMazeLeaderboard(listener);
    }

    /**מרעננת את הפודיום*/
    private void refreshLeaderboard() {
        loadingDialog = LoadingDialog.display(getParentFragmentManager());
        oneQueryFinished = false;
        LoadingDialog.OnLoadingFinishedListener listener = new LoadingDialog.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                userCoinsPlace = getUserCoinsPlace();
                userMazePlace = getUserMazePlace();
                if (btgLeaderboard.getCheckedButtonId() == R.id.btnLeaderboardCoins) {
                    recyclerView.updateLeaderboard(leaderboardCoins);
                } else {
                    recyclerView.updateLeaderboard(leaderboardMaze);
                }
            }
        };
        setUpCoinsLeaderboard(listener);
        setUpMazeLeaderboard(listener);
    }

    /**שתי הפעולות הבאות מחזירות את המקום את המשתמש הנוכחי יחסית לשאר הפודיום*/
    private int getUserCoinsPlace() {
        int place = 1;
        for (LeaderboardPlace leaderboardPlace : leaderboardCoins) {
            if (leaderboardPlace.getEmail().equals(user.getEmail())) {
                return place;
            }
            place++;
        }

        return -1;
    }

    private int getUserMazePlace() {
        int place = 1;
        for (LeaderboardPlace leaderboardPlace : leaderboardMaze) {
            if (leaderboardPlace.getEmail().equals(user.getEmail())) {
                return place;
            }
            place++;
        }

        return -1;
    }

    /**שתי הפעולות הבאות לוקחות את שני הפודיומים ממסד הנתונים ושמות אותו בתוך המערך הרצוי*/
    private void setUpCoinsLeaderboard(LoadingDialog.OnLoadingFinishedListener listener) {
        firebase.getLeaderboardDocumentFromDB(CommunicateWithFirebase.COINS_PATH).get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                LinkedHashMap<String, Integer> leaderboardMap =
                        new LinkedHashMap<>(firebase.castMap(documentSnapshot.getData()));
                leaderboardMap = firebase.sortMap(leaderboardMap);

                leaderboardCoins = firebase.castToLeaderboardArray(leaderboardMap);

                if (oneQueryFinished) {
                    loadingDialog.success(listener);
                } else {
                    oneQueryFinished = true;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebase.DBFailed(loadingDialog, "Failed to access database");
            }
        });
    }

    private void setUpMazeLeaderboard(LoadingDialog.OnLoadingFinishedListener listener) {
        firebase.getLeaderboardDocumentFromDB(CommunicateWithFirebase.MAZE_PATH).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        LinkedHashMap<String, Integer> leaderboardMap =
                                new LinkedHashMap<>(firebase.castMap(documentSnapshot.getData()));
                        leaderboardMap = firebase.sortMap(leaderboardMap);

                        leaderboardMaze = firebase.castToLeaderboardArray(leaderboardMap);

                        if (oneQueryFinished) {
                            loadingDialog.success(listener);
                        } else {
                            oneQueryFinished = true;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        firebase.DBFailed(loadingDialog, "Failed to access database");
                    }
                });
    }

    /**לסדר את התצוגה של המשתמש הנוכחי בתוך ViewStub*/
    private void setUpViewStub() {
        viewStub = view.findViewById(R.id.viewStub);
        viewStub.setLayoutResource(R.layout.leaderboard_place_holder);
        View inflatedStub = viewStub.inflate();

        tvPersonalPlace = inflatedStub.findViewById(R.id.tvUserPlace);

        tvPersonalPlace.setTextColor(ResourcesCompat.getColor(getResources(), R.color.main_color, null));
        tvPersonalPlace.setBackground(ResourcesCompat.getDrawable(getResources(),
                R.drawable.personal_leaderboard_place_bg2, null));

        tvPersonalEmail = inflatedStub.findViewById(R.id.tvUserEmail);

        tvPersonalEmail.setTextColor(ResourcesCompat.getColor(getResources(), R.color.main_color, null));

        tvPersonalScore = inflatedStub.findViewById(R.id.tvUserScore);

        tvPersonalScore.setTextColor(ResourcesCompat.getColor(getResources(), R.color.main_color, null));

        LinearLayout llHolder = inflatedStub.findViewById(R.id.llHolder);
        llHolder.setBackground(ResourcesCompat.getDrawable(getResources(),
                R.drawable.personal_leaderboard_place_bg1, null));

        setUpViewStubText();
    }

    private void setUpViewStubText() {
        tvPersonalEmail.setText(user.getEmail());

        if (btgLeaderboard.getCheckedButtonId() == R.id.btnLeaderboardCoins) {
            if (userCoinsPlace != -1) {
                tvPersonalPlace.setText(userCoinsPlace + "");
            } else {
                tvPersonalPlace.setText("-");
            }

            tvPersonalScore.setText(user.getHighScore().getCoins() + "");
        } else {
            if (userMazePlace != -1) {
                tvPersonalPlace.setText(userMazePlace + "");
            } else {
                tvPersonalPlace.setText("-");
            }

            tvPersonalScore.setText(user.getHighScore().getMaze() + "");
        }
    }

    /**לבצע את הנדרש לפי לחיצת הכפתורים (בחירה של MAZE או COINS)*/
    @Override
    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        if (isChecked) {
            setUpViewStubText();
            if (checkedId == R.id.btnLeaderboardCoins) {
                recyclerView.updateLeaderboard(leaderboardCoins);
            } else {
                recyclerView.updateLeaderboard(leaderboardMaze);
            }
        }
    }
}
