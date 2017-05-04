package edu.avans.nicknam.instagram;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by snick on 4-5-2017.
 */

public class firebaseMessagingService extends FirebaseMessagingService {
    private final static String TAG = "MessagingService";
}
