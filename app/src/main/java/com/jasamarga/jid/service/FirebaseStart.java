package com.jasamarga.jid.service;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class FirebaseStart extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        Log.d("FirebaseInit", "Firebase initialized successfully");
    }

}
