package com.example.ballstars.registration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ballstars.R;

public class SignUpFragment extends Fragment {
    private CustomEditText etEmail, etUsername, etPassword, etConfirmPassword;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        etEmail = new CustomEditText(view.findViewById(R.id.layoutEmail), (view.findViewById(R.id.etEmail)));
        etUsername = new CustomEditText(view.findViewById(R.id.layoutUsername), (view.findViewById(R.id.etUsername)));
        etPassword = new CustomEditText(view.findViewById(R.id.layoutPassword), (view.findViewById(R.id.etPassword)));
        etPassword.setMinimumCharacters(6);
        etConfirmPassword = new CustomEditText(view.findViewById(R.id.layoutConfirmPassword), (view.findViewById(R.id.etConfirmPassword)));
        etConfirmPassword.setMinimumCharacters(6);

        return view;
    }
    private boolean checkData() {
        boolean checkEmail = CheckString.checkEmail(etEmail);
        boolean checkUsername = CheckString.checkUserName(etUsername);
        boolean checkPassword = CheckString.checkPassword(etPassword);
        boolean checkConfirmPassword = CheckString.checkConfirmPassword(etConfirmPassword, etPassword.getText());
        return checkEmail && checkUsername && checkPassword && checkConfirmPassword;
    }
    public void sendData() {
        //send data to activity
        if (checkData()) {
            Bundle result = new Bundle();
            result.putString("email", etEmail.getText());
            result.putString("username", etUsername.getText());
            result.putString("password", etPassword.getText());
            getParentFragmentManager().setFragmentResult("signUpData", result);
        }
    }
}