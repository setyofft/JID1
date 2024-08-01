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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.NotifAdapater;
import com.jasamarga.jid.models.model_notif.ControllerNotif;
import com.jasamarga.jid.models.model_notif.ModelNotif;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.ServiceFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
    Sessionmanager sessionManager;
    HashMap<String,String> userDetails;
    int page = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif_layout);

        initVar();

    }

    private void initVar(){
        kosong = findViewById(R.id.kosong);
        progressBar = findViewById(R.id.loading);
        button_back = findViewById(R.id.back);
        textView = findViewById(R.id.title);
        recyclerView = findViewById(R.id.list);
        sessionManager = new Sessionmanager(this);
        userDetails = sessionManager.getUserDetails();
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
        jsonObject.addProperty("name", ServiceFunction.getUserRole(this,"name"));

        ReqInterface serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excuteRead(userDetails.get(Sessionmanager.nameToken),jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body()!=null){
                    Log.d(TAG, "onResponse: "+response.body().toString());
                }
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
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        ReqInterface serviceAPI = ApiClient.getClient(this);
        Call<ControllerNotif> call = serviceAPI.excutenotif(userDetails.get(Sessionmanager.nameToken),jsonObject);
        Gson gson = new Gson();
        call.enqueue(new Callback<ControllerNotif>() {
            @Override
            public void onResponse(Call<ControllerNotif> call, Response<ControllerNotif> response) {
                if(response.body() != null){
                    Log.d(TAG, "onResponse: " + gson.toJson(response.body()));
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
