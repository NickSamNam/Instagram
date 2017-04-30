package edu.avans.nicknam.instagram;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import java.util.*;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginStatus.StatusChangedListener {
    private AnimationDrawable anim;
    private LoginStatus loginStatus;
    private Button loginButton;
    private LoginButton fbLoginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("onCreate", "started");
        Log.i("Current locale", getResources().getConfiguration().locale.getDisplayLanguage());
        long startTime = System.nanoTime();

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        usernameEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);

        anim = (AnimationDrawable) findViewById(R.id.activity_login).getBackground();
        Spinner languageSpinner = (Spinner)findViewById(R.id.languageSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                AvailableLocales.getAvailableLocalesAsDisplayLanguage());

        adapter.setDropDownViewResource(R.layout.spinner_item_style);
        languageSpinner.setAdapter(adapter);
        languageSpinner.setSelection(adapter.getPosition(getResources().getConfiguration().locale
                .getDisplayLanguage(getResources().getConfiguration().locale)));
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Locale oldLocale = getResources().getConfiguration().locale;
                Locale newLocale = AvailableLocales.getAvailableLocalesHashMap().get(parent.getSelectedItem());
                if (!newLocale.getLanguage().equals(oldLocale.getLanguage())) {
                    Log.i("Old Locale", oldLocale.getLanguage());
                    Log.i("New Locale", newLocale.getLanguage());

                    Configuration configuration = new Configuration(getResources().getConfiguration());
                    configuration.setLocale(newLocale);
                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
//                     TODO: 19-4-2017 ask why not possible
//                    getResources().getConfiguration().setLocale(newLocale);
//                    getResources().getConfiguration().setLayoutDirection(newLocale);
//                    getResources().getConfiguration().setTo(new Configuration(getResources().getConfiguration()));
//                    getResources().getConfiguration().locale = newLocale;
//                    onConfigurationChanged(getResources().getConfiguration());
//                    getResources().getConfiguration().updateFrom(configuration);

                    recreate();
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

        usernameEditText.getBackground().setAlpha(20);
        passwordEditText.getBackground().setAlpha(20);
        findViewById(R.id.signUpTextView).getBackground().setAlpha(20);

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

        fbLoginButton = (LoginButton) findViewById(R.id.fbLoginButton);
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                serverError();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
        long endTime = System.nanoTime();
        Log.i("onCreate", "completed in " + (endTime-startTime));
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
            loginButton.setBackground(getDrawable(R.drawable.button_border));
            loginButton.setAlpha(.3f);
        }
        Log.i("Status", String.valueOf(ready));
    }

    @Override
    public void onClick(View v) {
        if (!loginStatus.attemptLogin()) {
            serverError();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void serverError() {
        Snackbar.make(findViewById(R.id.activity_login), R.string.server_error, Snackbar.LENGTH_LONG).show();
        Log.i("Login", "failed");
    }
}
