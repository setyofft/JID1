package com.jasamarga.jid;

import static android.content.ContentValues.TAG;

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
import com.jasamarga.jid.adapter.UserSetting;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Sessionmanager sessionmanager;
    private ReqInterface serviceAPI;
    private LoadingDialog loadingDialog;
    TextView title,versi;
    ProgressBar progresBar;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private UserSetting userSetting;
    private String version_app = BuildConfig.VERSION_NAME;
    private SharedPreferences sharedPref;

    //    private String version_app = "1.3";
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
        String setBatasKM = sharedPreferences.getString(UserSetting.BATASKMSET, UserSetting.offSet);
        String setJalanPenghubng = sharedPreferences.getString(UserSetting.JALANPENGHUBUNGSET, UserSetting.offSet);
        String setGerbangtol = sharedPreferences.getString(UserSetting.GERBANGTOLSET, UserSetting.offSet);
        String setRestArea = sharedPreferences.getString(UserSetting.RESTAREASET, UserSetting.offSet);
        String setRougnesindex = sharedPreferences.getString(UserSetting.ROUDNESINDEXSET, UserSetting.offSet);
        String setRTMS = sharedPreferences.getString(UserSetting.RTMSSET, UserSetting.offSet);
        String setRTMS2 = sharedPreferences.getString(UserSetting.RTMSSET2, UserSetting.offSet);
        String setSpedd = sharedPreferences.getString(UserSetting.SPEDDSET, UserSetting.offSet);
        String setLevel = sharedPreferences.getString(UserSetting.WATER, UserSetting.offSet);
        String setPompa = sharedPreferences.getString(UserSetting.POMPA, UserSetting.offSet);
        String setWIM = sharedPreferences.getString(UserSetting.WIM, UserSetting.offSet);
        String setBike = sharedPreferences.getString(UserSetting.BIKE, UserSetting.offSet);
        String setGpsKend = sharedPreferences.getString(UserSetting.GPSKEND, UserSetting.offSet);
        String setRadar = sharedPreferences.getString(UserSetting.RADAR, UserSetting.offSet);

        userSetting.setJalanToll(setJlnToll);
        userSetting.setKondisiTraffic(setKondisiTrafic);
        userSetting.setRekayasaLalin(setRekayasa);
        userSetting.setPemeliharaan(setPemiliharaan);
        userSetting.setGangguanLalin(setGangguan);
        userSetting.setCctv(setCCTV);
        userSetting.setVms(setVMS);
        userSetting.setBataskm(setBatasKM);
        userSetting.setJalanpenghubung(setJalanPenghubng);
        userSetting.setGerbangtol(setGerbangtol);
        userSetting.setRestarea(setRestArea);
        userSetting.setRougnesindex(setRougnesindex);
        userSetting.setRtms(setRTMS);
        userSetting.setRtms2(setRTMS2);
        userSetting.setSpeed(setSpedd);
        userSetting.setWaterlevel(setLevel);
        userSetting.setPompa(setPompa);
        userSetting.setWim(setWIM);
        userSetting.setBike(setBike);
        userSetting.setGpskend(setGpsKend);
        userSetting.setRadar(setRadar);

        versi = findViewById(R.id.versi);
        versi.setText("Version " + version_app);
        sessionmanager = new Sessionmanager(getApplicationContext());
        title = findViewById(R.id.title);
        progresBar = findViewById(R.id.progresBar);

//        cekVersiApp();
//        verifyStoragePermissions(MainActivity.this);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            Log.d(TAG, "onCreate: " + bundle.getString("ket_jenis"));
            int counter =0;
            counter++;
            SharedPreferences.Editor editor = getSharedPreferences("BADGE", MODE_PRIVATE).edit();
            editor.putInt("badge", counter);
            editor.apply();
        }

        String path = getApplicationContext().getExternalFilesDir(null) + "/datajid/";
        File checkFile = new File(path);
        if (!checkFile.exists()) {
            checkFile.mkdir();
        }else {
            Log.d(TAG, "onCreate:  Bikin folder Gagal"  );
        }
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        boolean firstStart = settings.getBoolean("firstStart", true);

        if(firstStart) {

            final String date = "2023-03-22";
            sharedPref.edit().putString("tgl_toll", date).apply();
            Log.d(TAG, "onCreate: TESTINGGG" );
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstStart", false);
            editor.commit();
        }
        refreshSession();

//          updateFileLalin();
    }
    private void cekTglUpdatejson(){
        String datenow =  sharedPref.getString("tgl_toll","empty");

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excuteupdatetol();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if(response.body() != null){
                        JSONObject dataRes = new JSONObject(response.body().toString());
                        String tgl = dataRes.getString("tglupdate");

                        if(datenow.equals(tgl)){
                            Log.d("Update Lalin", "Sudah terupdate");
                            verifyStoragePermissions(MainActivity.this);
                        }else{
                            sharedPref.edit().putString("tgl_toll", tgl).apply();
                            Log.d("Sedang Update Lalin", "onProses");
//                            updateFileLalin();
                            updateFileToll();
                        }
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

    private static long daysBetween(Calendar tanggalAwal, Calendar tanggalAkhir) {
        long lama = 0;
        Calendar tanggal = (Calendar) tanggalAwal.clone();
        while (tanggal.before(tanggalAkhir)) {
            tanggal.add(Calendar.DAY_OF_MONTH, 1);
            lama++;
        }
        return lama;
    }
    private void refreshSession(){
        ReqInterface apiClientNew = ApiClientNew.getServiceNew();
        HashMap<String, String> user = sessionmanager.getUserDetails();
        String token = user.get(Sessionmanager.nameToken);
        Log.d(TAG, "refreshSession: " + token);
        Call<JsonObject> call = apiClientNew.refreshSession(token);
        if(token != null){
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    if (response.isSuccessful()) {
                        // Handle successful response here
                        cekVersiApp();
                        try {
                            if (response.body() != null){
                                JSONObject dataRes = new JSONObject(response.body().toString());

                                Log.d(TAG, "onResponse: Refresh Session" + dataRes);
                            }else {
                                Log.d(TAG, "onNullBody: Refresh Session" + response.message() + response);

                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == 401) {
                        // Handle 401 unauthorized error
                        Log.d(TAG, "Unauthorized: " + response.message());
//                        if (response.message().contains("Unauthorized")){
                            sessionmanager.logout();
                            sessionmanager.checkLogin();
                            finish();
//                        }
                        // You can also show a Toast or perform other actions as needed
                    } else {
                        // Handle other error codes
                        Log.d(TAG, "Error: " + response.code() + " Oh yeah  " + response.message());
                        Toast.makeText(getApplicationContext(), "Error: " + response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("Error Data", t.getMessage());
                    Toast.makeText(getApplicationContext(),"Error API" + t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            cekVersiApp();
        }
    }
    private void cekVersiApp(){
        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("versi_app", version_app);

        serviceAPI = ApiClient.getServiceNew();
        Call<JsonObject> call = serviceAPI.cekVersion(version_app);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if(response.body() != null){
                        JSONObject dataRes = new JSONObject(response.body().toString());
                        Log.d("adefe", "Aplikasi sudah di versi : "+ dataRes.getBoolean("status"));
                        if (dataRes.getBoolean("status")){
//                            verifyStoragePermissions(MainActivity.this);
                            cekTglUpdatejson();
                        }else{
                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
                            alertDialogBuilder.setTitle("Update App");
                            alertDialogBuilder.setIcon(R.drawable.logojm);
                            alertDialogBuilder.setMessage("Versi terbaru telah tersedia");
                            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
                            alertDialogBuilder.setCancelable(false);
                            alertDialogBuilder.setPositiveButton("Perbarui Sekarang", (dialog, which) -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.jasamarga.jid"));
                                startActivity(intent);
                            });
                            alertDialogBuilder.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", t.getMessage());
            }
        });

    }

    private void cekKodisi(){
        Intent intent;
        if (ConnesctionManager.checkConnesction(getBaseContext())){
//            updateFileLalin();
//            updateScopeAndItem();
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
            Toast.makeText(getApplicationContext(),"Silahkan Login !",Toast.LENGTH_LONG).show();
            verifyStoragePermissions(MainActivity.this);
            title.setVisibility(View.VISIBLE);
            progresBar.setVisibility(View.VISIBLE);
        });
        alertDialogBuilder.show();
    }
    private void updateFileLalin(){
        serviceAPI = ApiClient.getNoClient();
        Call<JsonObject> call = serviceAPI.excutelinetoll();
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
                        FileWriter file = new FileWriter(checkFile.getAbsolutePath() + "/lalin.json");
                        file.write(dataRes.toString());
                        file.flush();
                        file.close();
                    }else{
                        Log.d("sadde", "Kosong bro ah" + response.toString());
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

    private void updateFileToll(){
        serviceAPI = ApiClient.getNoClient();
        Call<JsonObject> call = serviceAPI.excutelinetoll();
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
                        FileWriter file = new FileWriter(checkFile.getAbsolutePath() + "/toll.json");
                        file.write(dataRes.toString());
                        file.flush();
                        file.close();

                        verifyStoragePermissions(MainActivity.this);
                    }else{
                        Log.d("sadde", "Kosong bro ah" + response.toString());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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