package com.pim.jid.views;

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
import com.pim.jid.Dashboard;
import com.pim.jid.LoadingDialog;
import com.pim.jid.R;
import com.pim.jid.Sessionmanager;
import com.pim.jid.router.ApiClient;
import com.pim.jid.router.ReqInterface;

import org.json.JSONException;
import org.json.JSONObject;

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
                message = "Maaf, username dan password anda harus terisi !";
                showALert(message);
            }else{
                initLogin();
            }

        });
    }

    private void showALert(String message) {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Peringatan Login");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Keluar", (dialog, which) -> dialog.cancel());
        alertDialogBuilder.show();
    }

    private void initLogin() {
        loadingDialog = new LoadingDialog(Login.this);
        loadingDialog.showLoadingDialog("Loading...");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username",username.getText().toString());
        jsonObject.addProperty("password",password.getText().toString());

        ReqInterface serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutelogin(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        JSONObject dataAkun = new JSONObject(dataRes.getString("data"));
                        sessionManager.createSession(dataAkun.getString("name"),
                                dataAkun.getString("vip"), dataAkun.getString("scope"),
                                dataAkun.getString("item"), dataAkun.getString("info"),
                                dataAkun.getString("report"), dataAkun.getString("dashboard"));

                        addSession(dataAkun.getString("name"));
                    }else if(dataRes.getString("status").equals("0")){
                        message = dataRes.getString("msg");
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
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", nameuser);

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
