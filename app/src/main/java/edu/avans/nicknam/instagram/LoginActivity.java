package edu.avans.nicknam.instagram;

import android.content.res.Configuration;
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

import java.util.*;

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
        ArrayList<String> languages = new ArrayList<>();
        for(String ISOLanguage : Locale.getISOLanguages()) {
            Locale locale = new Locale(ISOLanguage);
            localeHashMap.put(locale.getDisplayLanguage(), new Locale(ISOLanguage));
            languages.add(locale.getDisplayLanguage());
        }
        Collections.sort(languages, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(R.layout.spinner_item_style);
        languageSpinner.setAdapter(adapter);
        languageSpinner.setSelection(adapter.getPosition(getResources().getConfiguration().locale.getDisplayLanguage()));
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Locale oldLocale = getResources().getConfiguration().locale;
                oldLocale = new Locale(oldLocale.getLanguage().substring(0, 2));
                Locale newLocale = localeHashMap.get(parent.getSelectedItem());
                newLocale = new Locale(newLocale.getLanguage().substring(0, 2));
                Log.i("Old Locale", oldLocale.toLanguageTag());
                Log.i("New Locale", newLocale.toLanguageTag());
                if (!newLocale.toLanguageTag().equals(oldLocale.toLanguageTag())) {
                    Configuration configuration = new Configuration(getResources().getConfiguration());
                    configuration.setLocale(newLocale);
                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                    // TODO: 19-4-2017 ask why not possible
//                    getResources().getConfiguration().setLocale(newLocale);
//                    getResources().getConfiguration().setTo(new Configuration(getResources().getConfiguration()));
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
