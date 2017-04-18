package edu.avans.nicknam.instagram;

import java.io.Serializable;

/**
 * Created by snick on 18-4-2017.
 */
public class LoginStatus implements Serializable {
    private boolean usernameReady;
    private boolean passwordReady;
    private transient StatusChangedListener scl;

    public LoginStatus(StatusChangedListener scl) {
        this.scl = scl;
        update();
    }

    public void setUsernameReady(boolean ready) {
        if (usernameReady != ready) {
            usernameReady = ready;
            update();
        }
    }

    public void setPasswordReady(boolean ready) {
        if (passwordReady != ready) {
            passwordReady = ready;
            update();
        }
    }

    public void refresh(StatusChangedListener scl) {
        this.scl = scl;
    }

    private void update() {
        if (usernameReady && passwordReady)
            scl.statusChanged(true);
        else
            scl.statusChanged(false);
    }

    public interface StatusChangedListener {
        void statusChanged(boolean ready);
    }
}
