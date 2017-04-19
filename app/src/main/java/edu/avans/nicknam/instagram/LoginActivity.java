package edu.avans.nicknam.instagram;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginStatus.StatusChangedListener {
    private AnimationDrawable anim;
    private LoginStatus loginStatus;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        ConstraintLayout activityLogin = (ConstraintLayout) findViewById(R.id.activity_login);
        anim = (AnimationDrawable) activityLogin.getBackground();
        Spinner languageSpinner = (Spinner)findViewById(R.id.languageSpinner);
        final HashMap<String, Locale> localeHashMap = new HashMap<>();
        Locale[] locales = Locale.getAvailableLocales();
        for(Locale locale : locales) {
            if(locale.getDisplayLanguage().length() > 0 && !localeHashMap.containsKey(locale.getDisplayLanguage())){
                localeHashMap.put(locale.getDisplayLanguage(), locale);
            }
        }
        ArrayList<String> languages = new ArrayList<>(localeHashMap.keySet());
        Collections.sort(languages, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(R.layout.spinner_item_style);
        languageSpinner.setAdapter(adapter);
//        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Locale oldLocale = getResources().getConfiguration().locale;
//                Locale newLocale = localeHashMap.get(parent.getSelectedItem());
//                if (newLocale != oldLocale) {
//                    getResources().getConfiguration().setLocale(newLocale);
//                    finish();
//                    startActivity(getIntent());
//                    Log.i("Language", getResources().getConfiguration().locale.getDisplayLanguage());
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        //Broken line
        //Broken line
        Log.i("Spinner selection", String.valueOf((getResources().getConfiguration().locale.getDisplayCountry())));
        getResources().getConfiguration().setLocale(Locale.forLanguageTag("nl"));
        languageSpinner.setSelection(adapter.getPosition(getResources().getConfiguration().locale.getDisplayCountry()));
        if (savedInstanceState == null) {
            loginStatus = new LoginStatus(this);
        } else {
            loginStatus = (LoginStatus) savedInstanceState.getSerializable("loginStatus");
            loginStatus.refresh(this);
            anim.setState(savedInstanceState.getIntArray("animState"));
        }

        usernameEditText.getBackground().setAlpha(30);
        passwordEditText.getBackground().setAlpha(30);

        anim.setEnterFadeDuration(500);
        anim.setExitFadeDuration(10000);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0)
                    loginStatus.setUsernameReady(true);
                else
                    loginStatus.setUsernameReady(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0)
                    loginStatus.setPasswordReady(true);
                else
                    loginStatus.setPasswordReady(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loginButton.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("loginStatus", loginStatus);
        outState.putIntArray("animState", anim.getState());
        outState.putSerializable("locale", getResources().getConfiguration().locale);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    @Override
    public void statusChanged(boolean ready) {
        loginButton.setEnabled(ready);
        if (ready) {
            loginButton.setBackgroundColor(Color.WHITE);
            loginButton.setTextColor(Color.BLACK);
            loginButton.setAlpha(1f);
        } else {
            loginButton.setBackgroundColor(Color.TRANSPARENT);
            loginButton.setAlpha(.3f);
        }
        Log.i("Status", String.valueOf(ready));
    }

    @Override
    public void onClick(View v) {
        if (!loginStatus.attemptLogin()) {
            Snackbar.make(findViewById(R.id.activity_login), R.string.server_error, Snackbar.LENGTH_LONG).show();
            Log.i("Login", "failed");
        }
    }
}
