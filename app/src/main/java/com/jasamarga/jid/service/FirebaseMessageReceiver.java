package com.jasamarga.jid.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jasamarga.jid.Dashboard;
import com.jasamarga.jid.MainActivity;
import com.jasamarga.jid.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    String channel_id = "notification_channel";
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    int counter = 0;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        SharedPreferences.Editor editor = getSharedPreferences("TOKEN_PREF", MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.apply();
        Log.e("NEW_TOKEN", token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String TAG = "FCM";
        Log.d(TAG, "onMessageReceived: " + Objects.requireNonNull(message.getNotification()).getTitle());

        //save badge
        counter++;
        SharedPreferences.Editor editor = getSharedPreferences("BADGE", MODE_PRIVATE).edit();
        editor.putInt("badge", counter);
        editor.apply();

        SharedPreferences prefs = getSharedPreferences("BADGE", MODE_PRIVATE);
        int token = prefs.getInt("badge", 0);
        Log.d(TAG, "onMessageReceivedTkoet: " + token);
        if(!message.getData().isEmpty()){
            Log.d(TAG, "onMessageReceived: " + Objects.requireNonNull(message.getData().get("ket_status")));
            Log.d(TAG, "onMessageReceivedTkoet: " + token);
            showBackNotif(message);
        }else {
            showBackNotif(message);
        }

    }

    private void showBackNotif(RemoteMessage message) {
        Intent notificationIntent = new Intent(getApplicationContext(), com.jasamarga.jid.views.Notification.class);
        PendingIntent resultIntent = PendingIntent. getActivity (getApplicationContext() , 0 , notificationIntent , PendingIntent.FLAG_IMMUTABLE ) ;
        Map<String, String> mapData = message.getData();
        JSONObject js = new JSONObject(mapData);
        String body = null;
        try {
            body = js.getString("ket");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(),
                channel_id);
        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        notification.setSmallIcon(R.drawable.logonew);
        notification.setContentTitle(message.getNotification().getTitle());
        notification.setPriority(NotificationCompat.PRIORITY_HIGH);
        notification.setContentIntent(resultIntent);
        notification.setAutoCancel(true);
        notification.build();
        NotificationManager manager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE );

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new
                    NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            notification.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert manager != null;
            manager.createNotificationChannel(notificationChannel) ;
        }

        assert manager != null;
        manager.notify(( int ) System.currentTimeMillis(), notification.build());
    }

}
