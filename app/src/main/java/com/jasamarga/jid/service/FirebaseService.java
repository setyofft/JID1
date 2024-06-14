package com.jasamarga.jid.service;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jasamarga.jid.MainActivity;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.models.UserDevice;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseService {
    public static void showNotif(Activity activity) {
        String id = "channel_id", name = "channel name", des = "channel desh";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, id)
                .setSmallIcon(R.drawable.logojm)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(activity);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(001, builder.build());
    }
    public static ArrayList<UserDevice> getTokenFCM(Activity activity){
        ArrayList<UserDevice> arrayList = new ArrayList<>();
        UserDevice userDevice = new UserDevice();

        SharedPreferences prefs = activity.getSharedPreferences("TOKEN_PREF", MODE_PRIVATE);
        String token = prefs.getString("token", "");

        Context context = activity.getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(v->{
            SharedPreferences.Editor editor = activity.getSharedPreferences("TOKEN_PREF", MODE_PRIVATE).edit();
            if (token!=null){
                editor.putString("token", v);
                editor.apply();
            }
            Log.d("ON TOKEN",v);
        });

        userDevice.setDevice(getDeviceName());
        userDevice.setIp(getLocalIpAddress());
        userDevice.setName(ServiceFunction.getUserRole(activity.getApplicationContext(),"name"));
        userDevice.setToken(token);
        Log.d(TAG, "getTokenFCM: " +        token);
        arrayList.add(userDevice);

        return arrayList;
    }


    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        }
        return manufacturer + " " + model;
    }
//    @Override
//    public void onTokenRefresh() {
//        // Get updated InstanceID token.
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//
//        // TODO: Implement this method to send any registration to your app's servers.
//        sendRegistrationToServer(refreshedToken);
//    }
}
