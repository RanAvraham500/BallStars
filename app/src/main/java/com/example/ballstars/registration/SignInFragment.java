package com.example.ballstars.registration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ballstars.R;


public class SignInFragment extends Fragment {
    private CustomEditText etEmail, etPassword;
    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        etEmail = new CustomEditText(view.findViewById(R.id.layoutEmail), view.findViewById(R.id.etEmail));
        etPassword = new CustomEditText(view.findViewById(R.id.layoutPassword), view.findViewById(R.id.etPassword));
        etPassword.setMinimumCharacters(6);
        return view;
    }

    //return true if data is ok
    private boolean checkData() {
        boolean checkEmail = CheckString.checkEmail(etEmail);
        boolean checkPassword = CheckString.checkPassword(etPassword);
        return  checkEmail && checkPassword;
    }
    public void sendData() {
        //send data to activity
        if (checkData()) {
            Bundle result = new Bundle();
            result.putString("email", etEmail.getText());
            result.putString("password", etPassword.getText());
            getParentFragmentManager().setFragmentResult("signInData", result);
        }
    }
}