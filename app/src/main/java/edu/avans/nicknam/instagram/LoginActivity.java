package edu.avans.nicknam.instagram;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        final LocaleAdapter localeAdapter = new LocaleAdapter(this, getLayoutInflater(), AvailableLocales.getAvailableLocales());

        languageSpinner.setAdapter(localeAdapter);
        languageSpinner.setSelection(localeAdapter.getPosition(getResources().getConfiguration().locale));
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Locale oldLocale = getResources().getConfiguration().locale;
                Locale newLocale = (Locale) parent.getSelectedItem();
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

        usernameEditText.getBackground().mutate().setAlpha(38);
        passwordEditText.getBackground().mutate().setAlpha(38);
        findViewById(R.id.signUpTextView).getBackground().mutate().setAlpha(26);

        anim.setEnterFadeDuration(500);
        anim.setExitFadeDuration(10000);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
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
                if (s.length() > 0)
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
        fbLoginButton.setBackgroundColor(Color.TRANSPARENT);
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
            loginButton.setBackground(getResources().getDrawable(R.drawable.button_border));
            loginButton.setTextColor(Color.WHITE);
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

    public class LocaleAdapter extends BaseAdapter implements SpinnerAdapter {
        Context context;
        LayoutInflater inflator;
        List<Locale> locales;
        Locale activeLocale;

        public LocaleAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Locale> locales)
        {
            this.context = context;
            this.inflator = layoutInflater;
            this.locales = locales;
            activeLocale = getResources().getConfiguration().locale;
        }

        public int getPosition(Locale locale) {
            return locales.indexOf(locale);
        }

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return locales.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return locales.get(position);
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderClosed viewHolderClosed;
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.spinner_closed, null);

                viewHolderClosed = new ViewHolderClosed();
                viewHolderClosed.tvLanguageSpinner_closed = (TextView) convertView.findViewById(R.id.tvLanguageSpinner_closed);

                convertView.setTag(viewHolderClosed);
            } else {
                viewHolderClosed = (ViewHolderClosed) convertView.getTag();
            }
            viewHolderClosed.tvLanguageSpinner_closed.setText(activeLocale.getDisplayLanguage(activeLocale).substring(0,1).toUpperCase() + activeLocale.getDisplayLanguage(activeLocale).substring(1));
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            ViewHolderItem viewHolderItem;
            if(convertView == null) {
                convertView = inflator.inflate(R.layout.spinner_item, null);

                viewHolderItem = new ViewHolderItem();
                viewHolderItem.ivSelectedCheckmark = (ImageView) convertView.findViewById(R.id.ivSelectedCheckmark);
                viewHolderItem.tvLanguageCurrent = (TextView) convertView.findViewById(R.id.tvLanguageCurrent);
                viewHolderItem.tvLanguageLanguage = (TextView) convertView.findViewById(R.id.tvLanguageLanguage);
                viewHolderItem.lineBottom = convertView.findViewById(R.id.lineBottom);

                convertView.setTag(viewHolderItem);
            } else {
                viewHolderItem = (ViewHolderItem) convertView.getTag();
            }

            Locale locale = locales.get(position);

            if (locale.getLanguage().equals(activeLocale.getLanguage())) {
                viewHolderItem.ivSelectedCheckmark.setVisibility(View.VISIBLE);
            }
            String displayLanguageLanguage = locale.getDisplayLanguage(locale);
            String displayLanguageCurrent = locale.getDisplayLanguage(activeLocale);
            viewHolderItem.tvLanguageLanguage.setText(displayLanguageLanguage.substring(0,1).toUpperCase() + displayLanguageLanguage.substring(1));
            viewHolderItem.tvLanguageCurrent.setText(displayLanguageCurrent.substring(0,1).toUpperCase() + displayLanguageCurrent.substring(1));

            return convertView;
        }
    }

    private static class ViewHolderItem {
        public ImageView ivSelectedCheckmark;
        public TextView tvLanguageCurrent;
        public TextView tvLanguageLanguage;
        public View lineBottom;
    }

    private static class ViewHolderClosed {
        public TextView tvLanguageSpinner_closed;
    }
}
