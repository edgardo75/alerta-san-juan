package com.edadevsys.sanjuanalerta2.utils;

import android.widget.EditText;

import java.util.regex.Pattern;

public class Validation {

    //Regular Expression
    private static final String MESSAGE_ALERT_REGEX = "(^[\\w\\S-])+([\\w\\s-.]){4,15}+$*";
    private static final String DATA_PERSONAL_REGEX = "(^[\\w\\S-])+([\\w\\s-.])+$*";

    // Error Messages
    private static final String REQUIRED_MSG = "requerido";
    private static final String MESSAGE_ALERT_MSG = "Mensaje Alerta no permitido!!!";
    private static final String DATA_PERSONAL_MSG = "Dato opcional inválido!!!";
    private static EditText editText;
    private static boolean required;

    // call this method when you need to check email validation
    public static boolean isMessageAlert(EditText editText, boolean required) {
        return isValid(editText, MESSAGE_ALERT_REGEX, MESSAGE_ALERT_MSG, required);
    }

    public static boolean isDataPersonal(EditText editText, boolean required) {
        Validation.editText = editText;
        Validation.required = required;
        return isValidDataPersonal(editText, DATA_PERSONAL_REGEX, DATA_PERSONAL_MSG, required);
    }

    // return true if the input field is valid, based on the parameter passed
    private static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }

    public static boolean isValidDataPersonal(EditText editText, String regex, String errMsg, boolean required) {
        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // pattern doesn't match so returning false

        if (required || !hasTextDataPersonal(editText)) {

            return false;

        }

        if (required || !Pattern.matches(regex, text)) {

            editText.setError(errMsg);
            return false;
        }

        return true;

    }

    public static boolean hasTextDataPersonal(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        return text.length() != 0;


    }


}
