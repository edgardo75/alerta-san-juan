package com.edadevsys.sanjuanalerta2.utils;

import android.widget.EditText;

import java.util.regex.Pattern;

public class Validation {

    //Regular Expression
    private static final String MESSAGE_ALERT_REGEX = "(^[\\w\\S-])+([\\w\\s-]){4,15}+$*";
    private static final String DATA_PERSONAL_REGEX = "(^[\\w\\S-])+([\\w\\s-])+$*";

    // Error Messages
    private static final String REQUIRED_MSG = "requerido";
    private static final String MESSAGE_ALERT_MSG = "Mensaje Alerta no permitido!!!";
    private static final String DATA_PERSONAL_MSG = "Dato opcional inválido!!!";
    // --Commented out by Inspection (13/07/2015 11:23):private static EditText editText;
    private static boolean required = false;


    // call this method when you need to check email validation
    public static boolean isMessageAlert(EditText editText) {
        return isValid(editText);
    }

    public static boolean isDataPersonal(EditText editText) {

        Validation.required = false;
        return isValidDataPersonal(editText, Validation.required);
    }

    // return true if the input field is valid, based on the parameter passed
    private static boolean isValid(EditText editText) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (hasText(editText)) return false;

        // pattern doesn't match so returning false
        if (!Pattern.matches(Validation.MESSAGE_ALERT_REGEX, text)) {
            editText.setError(Validation.MESSAGE_ALERT_MSG);
            return false;
        }

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {
        boolean retorno = true;
        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            retorno = false;
        }

        return !retorno;
    }

    private static boolean isValidDataPersonal(EditText editText, boolean required) {
        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // pattern doesn't match so returning false

        if (required ||!hasTextDataPersonal(editText)) {

            return false;

        }

        if (!Pattern.matches(Validation.DATA_PERSONAL_REGEX, text)) {

            editText.setError(Validation.DATA_PERSONAL_MSG);
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
