package com.example.ballstars.registration;

import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.core.content.res.ResourcesCompat;

import com.example.ballstars.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CustomEditText implements TextWatcher {
    private TextInputLayout mTextInputLayout;
    private TextInputEditText mTextInputEditText;
    private int minimumCharacters = 0;

    public CustomEditText(TextInputLayout textInputLayout, TextInputEditText textInputEditText) {
        mTextInputLayout = textInputLayout;
        mTextInputEditText = textInputEditText;
        mTextInputEditText.addTextChangedListener(this);

    }

    public String getText() {
        return mTextInputEditText.getText().toString();
    }

    public void setMinimumCharacters(int minimumCharacters) {
        this.minimumCharacters = minimumCharacters;
    }

    public void setError(String error) {
        mTextInputLayout.setError(error);
    }
    public boolean setError(CheckString.PasswordErrorCode errorCode) {
        switch (errorCode) {
            case PASSWORD_TOO_SHORT -> {
                setError("Password must be at least 6 characters long");
                return false;
            }
            case PASSWORD_NO_LOWER_CASE -> {
                setError("Password must contain a lowercase character");
                return false;
            }
            case PASSWORD_NO_UPPER_CASE -> {
                setError("Password must contain an uppercase character");
                return false;
            }
            case PASSWORD_NO_NUMBER -> {
                setError("Password must contain a number");
                return false;
            }
            case PASSWORD_NO_SYMBOL -> {
                setError("Password must contain a symbol");
                return false;
            }
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    private void setCounterTextColor(int colorId) {
        mTextInputLayout.setCounterTextColor(
                ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                                mTextInputLayout.getContext().getResources(),
                                colorId,
                                mTextInputLayout.getContext().getTheme()
                        )
                )
        );
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (minimumCharacters != 0) {
            if (charSequence.length() < minimumCharacters) {
                setCounterTextColor(R.color.red);
            } else {
                setCounterTextColor(R.color.main_color);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        mTextInputLayout.setErrorEnabled(false);
    }
}
