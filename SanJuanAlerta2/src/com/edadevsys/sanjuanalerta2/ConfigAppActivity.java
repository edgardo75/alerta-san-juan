package com.edadevsys.sanjuanalerta2;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.edadevsys.sanjuanalerta2.utils.Validation;

@SuppressLint({"ValidFragment", "NewApi"})
public class ConfigAppActivity extends Activity {

    public static final String configSmsLocalFile = "conf1037";
    private static final int TEXT_MCOUNTER = 15;
    private static final int TEXT_MCOUNTER1 = 30;
    boolean flagTheNumber;
    //global variables
    private EditText messageText = null;
    private EditText homeAndOrNameSurename = null;
    private Button saveButton = null;
    private SharedPreferences saveLocal = null;
    private TextView mCouter = null;
    private TextView mCounter1 = null;
    private RadioButton radioEmergencyButton = null;
    private RadioGroup radioEmergencyGroup = null;
    private RadioButton radioYes = null;
    private RadioButton radioNo = null;
    private String msg = null;
    private String homeText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // onCreate
        setContentView(R.layout.fragment_config_app);

        initUI();
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void initUI() {

        //get preference
        saveLocal = getSharedPreferences(configSmsLocalFile, ConfigAppActivity.MODE_PRIVATE);
        // a number phone

        // a message alert
        msg = saveLocal.getString(getString(R.string.configMessageString), "");

        // a home user
        homeText = saveLocal.getString(getString(R.string.configHomeUser), "");

        // a flag send police emergency phone
        flagTheNumber = saveLocal.getBoolean(getString(R.string.configFlagNumberString), false);

        // button send
        saveButton = (Button) findViewById(R.id.btnConfig);

        // editext
        messageText = (EditText) findViewById(R.id.messageConfigForPolice);

        homeAndOrNameSurename = (EditText) findViewById(R.id.editTextHomeAndOrNameSurename);


        // textview counter and counter1 caracter input (plus)
        mCouter = (TextView) findViewById(R.id.mCounter);

        mCounter1 = (TextView) findViewById(R.id.mCounter1);

        // radioGroup
        radioEmergencyGroup = (RadioGroup) findViewById(R.id.radioEmergencyNumberPhone);

        // home the user


        int seleted = radioEmergencyGroup.getCheckedRadioButtonId();

        //Get reference to the selected RadioButton
        radioEmergencyButton = (RadioButton) findViewById(seleted);


        radioYes = (RadioButton) findViewById(R.id.radio_optyes);

        radioNo = (RadioButton) findViewById(R.id.radio_optno);

        // flag getPreference
        if (flagTheNumber) {
            radioYes.setChecked(flagTheNumber);
            radioNo.setChecked(false);
        } else {
            radioYes.setChecked(false);
            radioNo.setChecked(true);
        }
        // listener button
        saveButton.setOnClickListener(new OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {


                if (checkValidation()) {
                    //Button Send Message ALert

                    String myMsg = messageText.getText().toString();

                    int selectedId = radioEmergencyGroup.getCheckedRadioButtonId();

                    homeText = homeAndOrNameSurename.getText().toString();

                    radioEmergencyButton = (RadioButton) findViewById(selectedId);


                    if ((myMsg.length() > 0 && radioEmergencyButton.getText() != null)) {

                        savePreferences(myMsg, radioEmergencyButton.getText().toString());

                        loadSavedPreferences();

                        Toast.makeText(ConfigAppActivity.this, getString(R.string.text_saved), Toast.LENGTH_SHORT).show();

                        finish();
                    } else {
                        Toast.makeText(getBaseContext(),
                                getString(R.string.text_input_number_phone),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(getBaseContext(),
                            getString(R.string.text_contain_error),
                            Toast.LENGTH_LONG).show();
                }
            }
        });//end setOnClickListener

        // listener message alert
        messageText.addTextChangedListener(new TextWatcher() {
            // using when character input user
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String smsNo;
                if (s.length() == 0)
                    smsNo = "0";
                else
                    smsNo = String.valueOf(s.length() / TEXT_MCOUNTER + 1);
                String smsLength = String.valueOf(TEXT_MCOUNTER - (s.length() % TEXT_MCOUNTER));
                mCouter.setText(smsLength + "/" + smsNo);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                Validation.isMessageAlert(messageText, true);

            }
        });
        homeAndOrNameSurename.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String homeText;
                if (s.length() == 0)
                    homeText = "0";
                else
                    homeText = String.valueOf(s.length() / TEXT_MCOUNTER1 + 1);
                String homeLength = String.valueOf(TEXT_MCOUNTER1 - (s.length() % TEXT_MCOUNTER1));
                mCounter1.setText(homeLength + "/" + homeText);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Validation.hasTextDataPersonal(homeAndOrNameSurename)) {
                    Validation.isDataPersonal(homeAndOrNameSurename, false);
                }

            }
        });


        messageText.setText(msg, TextView.BufferType.EDITABLE);

        homeAndOrNameSurename.setText(homeText, TextView.BufferType.EDITABLE);


    }


    //************************************************************
    // load preference method
    private void loadSavedPreferences() {


        msg = saveLocal.getString(getString(R.string.configMessageString), "");

        flagTheNumber = saveLocal.getBoolean(getString(R.string.configFlagNumberString), false);

        homeText = saveLocal.getString(getString(R.string.configHomeUser), "");


    }

    //************************************************************
    //save preference method
    private void savePreferences(String message, String radioSelected) {
        boolean seleted = false;

        if (radioSelected.equalsIgnoreCase("SI")) {
            seleted = true;
        }

        saveLocal = getSharedPreferences(configSmsLocalFile, ConfigAppActivity.MODE_PRIVATE);

        SharedPreferences.Editor editor = saveLocal.edit();

        editor.putString(getString(R.string.configMessageString), message);

        editor.putBoolean(getString(R.string.configFlagNumberString), seleted);

        editor.putString(getString(R.string.configHomeUser), homeText);

        editor.commit();

    }
    //************************************************************

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.hasText(messageText)) ret = false;

        if (!Validation.isMessageAlert(messageText, true)) ret = false;

        if (Validation.hasTextDataPersonal(homeAndOrNameSurename)) {

            if (!Validation.isDataPersonal(homeAndOrNameSurename, false)) ret = false;

        }


        return ret;
    }

}
