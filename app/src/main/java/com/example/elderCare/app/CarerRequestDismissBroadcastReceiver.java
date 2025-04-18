package com.example.elderCare.app;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.elderCare.app.fcm.FirebaseData;

import java.util.Objects;

public class CarerRequestDismissBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = FirebaseData.CARER_REQUEST_NOTIFICATION_ID;
        Log.d("Onyx4", Integer.toString(notificationId));
        Objects.requireNonNull(manager).cancel(notificationId);
    }
}
