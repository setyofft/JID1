package com.pim.jid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnesctionManager {
    public static boolean checkConnesction(Context context){
        ConnectivityManager connectiVityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectiVityManager != null){
            @SuppressLint("MissingPermission") NetworkInfo[] info = connectiVityManager.getAllNetworkInfo();
            if (info != null){
                for (int i = 0; i < info.length;i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
