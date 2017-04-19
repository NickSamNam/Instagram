package edu.avans.nicknam.instagram;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;

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

        if (savedInstanceState == null)
            loginStatus = new LoginStatus(this);

        usernameEditText.getBackground().setAlpha(30);
        passwordEditText.getBackground().setAlpha(30);

        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.activity_login);
        anim = (AnimationDrawable) container.getBackground();
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
        ArrayList<String> languages = new ArrayList<>();
        languages.add(getResources().getConfiguration().locale.getDisplayLanguage());
        for(String locale : getResources().getAssets().getLocales()) {
            if(locale.length() > 0 && !languages.contains(locale)){
                languages.add(locale);
            }
        }
        Collections.sort(languages, String.CASE_INSENSITIVE_ORDER);
        Spinner languageSpinner = (Spinner)findViewById(R.id.languageSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(R.layout.spinner_item_style);
        languageSpinner.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("loginStatus", loginStatus);
        outState.putIntArray("animState", anim.getState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loginStatus = (LoginStatus) savedInstanceState.getSerializable("loginStatus");
        loginStatus.refresh(this);
        anim.setState(savedInstanceState.getIntArray("animState"));
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
