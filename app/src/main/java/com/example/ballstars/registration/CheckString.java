package com.example.ballstars.registration;

import java.util.regex.Pattern;

public class CheckString {
    public enum PasswordErrorCode {
        PASSWORD_TOO_SHORT,
        PASSWORD_NO_LOWER_CASE,
        PASSWORD_NO_UPPER_CASE,
        PASSWORD_NO_NUMBER,
        PASSWORD_NO_SYMBOL,
        PASSWORD_NO_ERROR
    }

    /**
     * Checking the format of the email
     * */
    public static boolean checkEmail(CustomEditText etEmail) {
        String email = etEmail.getText();
        Pattern emailPatten = Pattern.compile(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
        );
        if (!emailPatten.matcher(email).matches()) {
            etEmail.setError("Error with entered email");
            return false;
        }
        return true;
    }

    /**
     * Checking the format of the userName: if the username has any spaces
     * */
    public static boolean checkUserName(CustomEditText etUserName) {
        String username = etUserName.getText();
        if (username.contains(" ") || username.length() == 0) {
            etUserName.setError("Error with entered username");
            return false;
        }
        return true;
    }

    /**
     * Checking the format of the password:
     * if the password has at least one symbol,
     * at least one number
     * at least one lowercase character,
     * at least one uppercase character
     * and is at least 6 characters long
     * */
    public static boolean checkPassword(CustomEditText etPassword) {
        return etPassword.setError(getPasswordErrorCode(etPassword.getText()));
    }
    private static PasswordErrorCode getPasswordErrorCode(String password) {
        Pattern passwordPatten;
        /*passwordPatten = Pattern.compile(
                "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~$^+=<>]).{6,}$"
        );*/
        //check length
        if (password.length() < 6) return PasswordErrorCode.PASSWORD_TOO_SHORT;

        //check lower case exists
        passwordPatten = Pattern.compile(
            "(.*)([a-z]+)(.*)"
        );
        if (!passwordPatten.matcher(password).matches()) return PasswordErrorCode.PASSWORD_NO_LOWER_CASE;

        //check upper case exists
        passwordPatten = Pattern.compile(
            "(.*)([A-Z]+)(.*)"
        );
        if (!passwordPatten.matcher(password).matches()) return PasswordErrorCode.PASSWORD_NO_UPPER_CASE;

        //check number exists
        passwordPatten = Pattern.compile(
            "(.*)(\\d+)(.*)"
        );
        if (!passwordPatten.matcher(password).matches()) return PasswordErrorCode.PASSWORD_NO_NUMBER;

        //check symbol exists
        passwordPatten = Pattern.compile(
                "(.*)([!@#&()–{}:;',?/*~$^+=<>]+)(.*)"
        );
        if (!passwordPatten.matcher(password).matches()) return PasswordErrorCode.PASSWORD_NO_SYMBOL;

        return PasswordErrorCode.PASSWORD_NO_ERROR;
    }

    /**
     * Checking the format of the confirm password: if the confirm password is equal to the password
     * */
    public static boolean checkConfirmPassword(CustomEditText etConfirmPassword, String password) {
        if (!etConfirmPassword.getText().equals(password) || password.length() == 0) {
            etConfirmPassword.setError("The passwords aren't the same");
            return false;
        }
        return true;
    }
}
