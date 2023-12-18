package com.jasamarga.jid.views;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.jasamarga.jid.Dashboard;
import com.jasamarga.jid.models.ModelUsers;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.models.UserDevice;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.FirebaseService;
import com.jasamarga.jid.service.ServiceFunction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private EditText username, password;
    private Button btn_login;
    private LoadingDialog loadingDialog;
    private ImageView btn_eyes;

    Sessionmanager sessionManager;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        sessionManager = new Sessionmanager(getApplicationContext());

        initVariabel();
        initAction();
    }

    private void initVariabel() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        btn_eyes = findViewById(R.id.btn_eyes);
    }

    private void initAction() {
        btn_eyes.setOnClickListener(v -> {
            if (password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                btn_eyes.setImageResource(R.drawable.ic_hide);
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                btn_eyes.setImageResource(R.drawable.ic_show);
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            password.setSelection(password.getText().length());
        });

        btn_login.setOnClickListener(v -> {
            if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                message = "Username dan Password anda harus terisi !";
                showALert(message);
            }else{
                initLogin();
            }

        });
    }

    private void showALert(String message) {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        alertDialogBuilder.show();
    }

    private void initLogin() {
        loadingDialog = new LoadingDialog(Login.this);
        loadingDialog.showLoadingDialog("Loading...");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name",username.getText().toString());
        jsonObject.addProperty("pass",password.getText().toString());
        jsonObject.addProperty("device", "mobile");
        jsonObject.addProperty("device_name",ServiceFunction.getDeviceName());
        ReqInterface newServie = ApiClient.getServiceNew();

        Call<ModelUsers> calls =newServie.login(jsonObject);
        String fullUrl = calls.request().url().toString();
        Log.d(TAG, "initLogin: " + fullUrl);
        calls.enqueue(new Callback<ModelUsers>() {
            @Override
            public void onResponse(Call<ModelUsers> call, Response<ModelUsers> response) {
                if (response.body() != null){
                        ModelUsers it = response.body();
                        if (it.isStatus()){
                            sessionManager.createSession(it.getData().getName(),it.getToken(),String.valueOf(it.getData().getVip()),it.getData().getScope(),it.getData().getItem(),it.getData().getInfo(),it.getData().getReport(),it.getData().getDashboard());

                            Toast.makeText(getApplicationContext(), "Selamat datang "+it.getData().getName()+" !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, Dashboard.class));
                            finish();
                            loadingDialog.hideLoadingDialog();

                            Log.d(TAG, "onResponse: " + it.getToken());
//                            addSession(it.getData().getName());
                        }else {
                            showALert(it.getMessage());
                        }
                }else {
                    ResponseBody body = response.errorBody();
                    try {
                        if (body != null) {
                            String errorJson = body.string(); // Convert ResponseBody to a JSON string
                            JSONObject json = new JSONObject(errorJson);
                            String errorMessage = json.getString("message");
                            showALert(errorMessage);
                        } else {
                            showALert("Maaf Ada kesalahan dari server");
                        }
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d(TAG, "onResponse: " + body);
                    loadingDialog.hideLoadingDialog();
                }
            }
            @Override
            public void onFailure(Call<ModelUsers> call, Throwable t) {
                Log.d("Error Data LOGIN", Objects.requireNonNull(t.getMessage()));
                loadingDialog.hideLoadingDialog();
            }
        });
//        ReqInterface serviceAPI = ApiClient.getClient();
//        Call<JsonObject> call = serviceAPI.excutelogin(jsonObject);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                try {
//                    if(response.body() != null) {
//                        JSONObject dataRes = new JSONObject(response.body().toString());
//                        Log.d(TAG, "onResponse: " + dataRes);
//
//                        if (dataRes.getString("status").equals("1")){
//                            JSONObject dataAkun = new JSONObject(dataRes.getString("data"));
//                            sessionManager.createSession(dataAkun.getString("name"),
//                                    dataAkun.getString("vip"), dataAkun.getString("scope"),
//                                    dataAkun.getString("item"), dataAkun.getString("info"),
//                                    dataAkun.getString("report"), dataAkun.getString("dashboard"));
//
//                            addSession(dataAkun.getString("name"));
//                        }else if(dataRes.getString("status").equals("0")){
//                            message = dataRes.getString("msg");
//                            showALert(message);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                loadingDialog.hideLoadingDialog();
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.d("Error Data", call.toString());
//                loadingDialog.hideLoadingDialog();
//            }
//        });
    }

    private void delSession(){
        loadingDialog = new LoadingDialog(Login.this);
        loadingDialog.showLoadingDialog("Loading...");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", username.getText().toString());

        ReqInterface serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutedelsession(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        initLogin();
                    }else{
                        Log.d("STATUS", response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadingDialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
                loadingDialog.hideLoadingDialog();
            }
        });
    }

    private void addSession(String nameuser) {
        //get User Agent
        ArrayList<UserDevice> userDevices = new ArrayList<>();
        userDevices = FirebaseService.getTokenFCM(this);
        String ip = "",device = "",token = "";
        for(UserDevice item : userDevices){
            ip = item.getIp();
            device = item.getDevice();
            token = item.getToken();

        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", nameuser);
        jsonObject.addProperty("device_name",device);
        jsonObject.addProperty("addr",ip);
        jsonObject.addProperty("token",token);

        Log.d(TAG, "addSession: " + jsonObject);
        ReqInterface serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excuteaddsession(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        Toast.makeText(getApplicationContext(), "Selamat datang "+nameuser+" !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, Dashboard.class));
                        finish();
                    }else{
                        message = "Silahkan cek kembali username dan password anda !";
                        showALert(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadingDialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
                loadingDialog.hideLoadingDialog();
            }
        });
    }


}
