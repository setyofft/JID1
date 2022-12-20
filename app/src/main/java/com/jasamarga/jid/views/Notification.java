package com.jasamarga.jid.views;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.jasamarga.jid.R;
import com.jasamarga.jid.adapter.NotifAdapater;
import com.jasamarga.jid.models.model_notif.ControllerNotif;
import com.jasamarga.jid.models.model_notif.ModelNotif;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.ServiceFunction;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notification extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView textView,kosong;
    private ImageView button_back;
    private ProgressBar progressBar;
    NotifAdapater notifAdapater;
    ArrayList<ModelNotif> modelEvents;
    int page = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif_layout);

        initVar();
        ServiceFunction.addLogActivity(this,"Notification","","Notification");

    }

    private void initVar(){
        kosong = findViewById(R.id.kosong);
        progressBar = findViewById(R.id.loading);
        button_back = findViewById(R.id.back);
        textView = findViewById(R.id.title);
        recyclerView = findViewById(R.id.list);
        action();
        getSegment();
    }

    @SuppressLint("SetTextI18n")
    private void action(){
        kosong.setVisibility(View.GONE);
        modelEvents = new ArrayList<>();
        textView.setText("Notification");
        buttonAction();
    }
    private void setAdapter(){
        notifAdapater = new NotifAdapater(modelEvents,this);
        recyclerView.setAdapter(notifAdapater);
    }
    private void buttonAction(){
        button_back.setOnClickListener(v -> {
            finish();
            ServiceFunction.getDataBadge(this);
        });
    }

    private void setRead(){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("limit", page);
        jsonObject.addProperty("name", ServiceFunction.getUserRole(getApplicationContext(),"name"));

        ReqInterface serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excuteRead(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, "onResponse: "+response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: Gagal" +t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences.Editor editor = getSharedPreferences("BADGE", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        ServiceFunction.getDataBadge(this);
    }

    private void getSegment() {
        kosong.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("limit", page);
        jsonObject.addProperty("platform", "jid_mobile");

        ReqInterface serviceAPI = ApiClient.getClient();
        Call<ControllerNotif> call = serviceAPI.excutenotif(jsonObject);
        call.enqueue(new Callback<ControllerNotif>() {
            @Override
            public void onResponse(Call<ControllerNotif> call, Response<ControllerNotif> response) {
                if(response.body() != null){
                    Log.d(TAG, "onResponse: " + response.body().getMsg());
                    modelEvents.addAll(response.body().getData());
                    setAdapter();
                    if (recyclerView.isAttachedToWindow()) {
                        setRead();
                    } else {
                        Toast.makeText(Notification.this, "TIDAK TERBACA", Toast.LENGTH_LONG).show();
                    }
                    kosong.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    SharedPreferences.Editor editor = getSharedPreferences("BADGE", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                }else {
                    kosong.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }

            }

            @Override
            public void onFailure(Call<ControllerNotif> call, Throwable t) {
                kosong.setVisibility(View.VISIBLE);
                Log.d("Error Data", t.getMessage());

            }
        });
    }

}
