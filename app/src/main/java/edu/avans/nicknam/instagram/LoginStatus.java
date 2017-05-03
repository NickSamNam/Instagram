package edu.avans.nicknam.instagram;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by snick on 18-4-2017.
 */
public class LoginStatus implements Serializable {
    private boolean usernameReady;
    private boolean passwordReady;
    private String status;
    private transient StatusChangedListener scl;

    public LoginStatus(StatusChangedListener scl) {
        this.scl = scl;
        update();
    }

    public void setUsernameReady(boolean ready) {
        if (usernameReady != ready) {
            usernameReady = ready;
            Log.i("Username ready", String.valueOf(ready));
            update();
        }
    }

    public void setPasswordReady(boolean ready) {
        if (passwordReady != ready) {
            passwordReady = ready;
            Log.i("Password ready", String.valueOf(ready));
            update();
        }
    }

    public void refresh(StatusChangedListener scl) {
        this.scl = scl;
        update();
    }

    private void update() {
        if (usernameReady && passwordReady)
            scl.statusChanged(true);
        else
            scl.statusChanged(false);
    }

    public String getStatus() {
        return status;
    }

    public boolean attemptLogin() {
        status = "failed";
        return false;
    }

    public interface StatusChangedListener {
        void statusChanged(boolean ready);
    }
}
