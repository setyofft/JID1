package com.pim.jid;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog(Activity myactivity){
        activity = myactivity;
    }

    public void showLoadingDialog(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.custom_loading, null);
        builder.setView(dialoglayout);
        builder.setCancelable(false);

        TextView title_loading = dialoglayout.findViewById(R.id.text_progress);
        title_loading.setText(title);

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    public void hideLoadingDialog(){
        alertDialog.dismiss();
    }

}
