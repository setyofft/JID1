package com.jasamarga.jid.components;

import static android.content.Context.MODE_PRIVATE;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jasamarga.jid.Dashboard;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.views.Notification;

import java.util.Objects;

public class Appbar {
    @SuppressLint("CutPasteId")
    public static void appBar(Activity activity, View view){
        CardView button_exit,button_notif;
        TextView nameInitial,nameuser,badge;
        Sessionmanager sessionmanager = new Sessionmanager(activity.getApplicationContext());
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        button_exit = view.findViewById(R.id.button_exit);
        button_notif = view.findViewById(R.id.button_bell);
        nameInitial = view.findViewById(R.id.nameInitial);
        nameuser = view.findViewById(R.id.nameuser);
        badge = view.findViewById(R.id.cart_badge);
        SharedPreferences prefs = activity.getSharedPreferences("BADGE", MODE_PRIVATE);
        int token = prefs.getInt("badge", 0);
        Log.d(TAG, "appBar: " + token);
        if(token != 0){
            badge.setVisibility(View.VISIBLE);
        }else {
            badge.setVisibility(View.GONE);

        }

        nameuser.setText(ServiceFunction.getUserRole(activity,"name"));
        nameInitial.setText(ServiceFunction.getUserRole(activity,"name").substring(0,1).toUpperCase());

        button_notif.setOnClickListener(v -> {
            Intent intent = new Intent(activity, Notification.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(0,0);
        });

        button_exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity);
            alertDialogBuilder.setTitle("Peringatan Akun");
            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
            alertDialogBuilder.setBackground(activity.getResources().getDrawable(R.drawable.modal_alert));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> ServiceFunction.delSession(activity,loadingDialog,ServiceFunction.getUserRole(activity,"name"),sessionmanager));
            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();
        });

    }
    public static void appBarNoName(Activity activity, View view){
        CardView button_exit,button_notif;
        ImageView button_back;
        TextView nameInitial,nameuser,badge;
        Sessionmanager sessionmanager = new Sessionmanager(activity.getApplicationContext());
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        button_exit = view.findViewById(R.id.button_exit);
        button_notif = view.findViewById(R.id.button_bell);
        button_back = view.findViewById(R.id.back);
        badge = view.findViewById(R.id.cart_badge);
        SharedPreferences prefs = activity.getSharedPreferences("BADGE", MODE_PRIVATE);
        int token = prefs.getInt("badge", 0);
        Log.d(TAG, "appBar: " + token);
        if(token != 0){
            badge.setVisibility(View.VISIBLE);
        }else {
            badge.setVisibility(View.GONE);

        }
        button_back.setOnClickListener(v->{
            activity.finish();
        });
        button_notif.setOnClickListener(v -> {
            Intent intent = new Intent(activity, Notification.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(0,0);
        });

        button_exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity);
            alertDialogBuilder.setTitle("Peringatan Akun");
            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
            alertDialogBuilder.setBackground(activity.getResources().getDrawable(R.drawable.modal_alert));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> ServiceFunction.delSession(activity,loadingDialog,ServiceFunction.getUserRole(activity,"name"),sessionmanager));
            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();
        });

    }
}
