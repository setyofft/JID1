package com.pim.jid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.pim.jid.adapter.UserSetting;
import com.pim.jid.router.ApiClient;
import com.pim.jid.router.ReqInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Sessionmanager sessionmanager;
    private ReqInterface serviceAPI;
    private LoadingDialog loadingDialog;
    TextView title;
    ProgressBar progresBar;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private UserSetting userSetting;
    private String version_app = "1";

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        userSetting = (UserSetting)getApplication();
        SharedPreferences sharedPreferences = getSharedPreferences(UserSetting.PREFERENCES, MODE_PRIVATE);
        String setJlnToll = sharedPreferences.getString(UserSetting.JALANTOLSET, UserSetting.onSet);
        String setKondisiTrafic = sharedPreferences.getString(UserSetting.KONDISITRAFFICSET, UserSetting.onSet);
        String setPemiliharaan = sharedPreferences.getString(UserSetting.PEMELIHARAANSET, UserSetting.onSet);
        String setRekayasa = sharedPreferences.getString(UserSetting.REKAYASALALINSET, UserSetting.onSet);
        String setGangguan = sharedPreferences.getString(UserSetting.GANGGUANLALINSET, UserSetting.onSet);
        String setCCTV = sharedPreferences.getString(UserSetting.CCTVSET, UserSetting.offSet);
        String setVMS = sharedPreferences.getString(UserSetting.VMSSET, UserSetting.offSet);

        userSetting.setJalanToll(setJlnToll);
        userSetting.setKondisiTraffic(setKondisiTrafic);
        userSetting.setRekayasaLalin(setRekayasa);
        userSetting.setPemeliharaan(setPemiliharaan);
        userSetting.setGangguanLalin(setGangguan);
        userSetting.setCctv(setCCTV);
        userSetting.setVms(setVMS);

        sessionmanager = new Sessionmanager(getApplicationContext());
        title = findViewById(R.id.title);
        progresBar = findViewById(R.id.progresBar);

        cekVersiApp();

        String path = getApplicationContext().getExternalFilesDir(null) + "/datajid/";
        File checkFile = new File(path);
        if (!checkFile.exists()) {
            checkFile.mkdir();
        }
    }

    private void cekVersiApp(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versi_app", version_app);

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutecekversi(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    Log.d("adefe", dataRes.getString("status"));
                    if (dataRes.getString("status").equals("1")){
                        verifyStoragePermissions(MainActivity.this);
                    }else if(dataRes.getString("status").equals("2")){
                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
                        alertDialogBuilder.setTitle("Update App");
                        alertDialogBuilder.setMessage("Versi terbaru tersedia, update sekarang ?");
                        alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("Ya", (dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.pim.jid"));
                            startActivity(intent);
                        });
                        alertDialogBuilder.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
            }
        });

    }

    private void cekKodisi(){
        Intent intent;
        if (ConnesctionManager.checkConnesction(getBaseContext())){
            updateFileLalin();
            updateScopeAndItem();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    sessionmanager.checkLogin();
                    finish();
                }
            },3000);
        }else{
            title.setVisibility(View.GONE);
            progresBar.setVisibility(View.GONE);
            showDialog();
        }
    }

    private void showDialog(){
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Koneksi Bermasalah");
        alertDialogBuilder.setMessage("Silahkan cek koneksi anda, kemudian coba lagi !");
        alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Coba Lagi", (dialog, which) -> {
            dialog.cancel();
            cekKodisi();
            title.setVisibility(View.VISIBLE);
            progresBar.setVisibility(View.VISIBLE);
        });
        alertDialogBuilder.show();
    }

    private void showpremision(){
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Perizinan Akses");
        alertDialogBuilder.setMessage("Silahkan memberikan izin akses pada aplikasi!");
        alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Sudah", (dialog, which) -> {
            dialog.cancel();
            verifyStoragePermissions(MainActivity.this);
            title.setVisibility(View.VISIBLE);
            progresBar.setVisibility(View.VISIBLE);
        });
        alertDialogBuilder.show();
    }

    private void updateFileLalin(){
        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutelalin();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if(response.body() != null) {
                        JSONObject dataRes = new JSONObject(response.body().toString());
                        String path = getApplicationContext().getExternalFilesDir(null) + "/datajid/";
                        File checkFile = new File(path);
                        if (!checkFile.exists()) {
                            checkFile.mkdir();
                        }
                        if (dataRes.getString("status").equals("1")){
                            FileWriter file = new FileWriter(checkFile.getAbsolutePath() + "/lalin.json");

                            file.write(dataRes.getString("data"));
                            file.flush();
                            file.close();
                        }else{
                            Log.d("Err DB", response.body().toString());
                        }
                    }else{
                        Log.d("sadde", "Kosong bro ah");
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
            }
        });

    }

    private void updateScopeAndItem(){
        HashMap<String, String> user = sessionmanager.getUserDetails();
        String username = user.get(Sessionmanager.kunci_id);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excuteupdatepravilage(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        JSONObject dataPravilage = new JSONObject(dataRes.getString("data"));
                        if (dataPravilage.getString("status").equals("0")){
                            Toast.makeText(getApplicationContext(),"Silahkan Login !", Toast.LENGTH_SHORT).show();
                        }else{
                            sessionmanager.createSession(dataPravilage.getString("name"),
                                    dataPravilage.getString("vip"), dataPravilage.getString("scope"),
                                    dataPravilage.getString("item"), dataPravilage.getString("info"),
                                    dataPravilage.getString("report"), dataPravilage.getString("dashboard"));
                        }
                    }else{
                        Log.d("Err DB", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
            }
        });

    }

    public void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            showpremision();
        }else {
            cekKodisi();
        }
    }


}