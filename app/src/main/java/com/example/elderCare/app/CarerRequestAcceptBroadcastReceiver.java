package com.example.elderCare.app;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.elderCare.app.fcm.FirebaseData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CarerRequestAcceptBroadcastReceiver extends BroadcastReceiver {

    private FirebaseFunctions mFunctions;
    private LocalBroadcastManager broadcaster;

    @Override
    public void onReceive(Context context, Intent intent) {
        mFunctions = FirebaseFunctions.getInstance();
        String id = FirebaseData.RECEIVER_ID;
        if (!id.equalsIgnoreCase("")) {
            Log.d("Onyx", "accepting request");
            acceptCarerRequest(id).addOnSuccessListener(s -> Toast.makeText(context, s, Toast.LENGTH_SHORT).show());
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = FirebaseData.CARER_REQUEST_NOTIFICATION_ID;
        Log.d("Onyx4", Integer.toString(notificationId));
        Objects.requireNonNull(manager).cancel(notificationId);

        broadcaster = LocalBroadcastManager.getInstance(context);

        Intent disconnectButton = new Intent("connect");
        broadcaster.sendBroadcast(disconnectButton);
    }

    private Task<String> acceptCarerRequest(String id) {
        Map<String, Object> data = new HashMap<>();
        Log.d("Onyx", id);
        data.put("receiver", id);
        return mFunctions
                .getHttpsCallable("acceptCarerRequest")
                .call(data)
                .continueWith(task -> (String) Objects.requireNonNull(task.getResult()).getData());
    }

}
