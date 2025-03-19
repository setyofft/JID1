package com.jasamarga.jid;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.gson.JsonObject;
import com.jasamarga.jid.adapter.UserSetting;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_CODE = 1;
    Sessionmanager sessionmanager;
    private ReqInterface serviceAPI;
    private LoadingDialog loadingDialog;
    TextView title,versi;
    ProgressBar progresBar;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private UserSetting userSetting;
    private String version_app = BuildConfig.VERSION_NAME;
    private SharedPreferences sharedPref;
    private AppUpdateManager appUpdateManager;
    private Task<AppUpdateInfo> appUpdateInfoTask;
    private int REQUEST_CODE_UPDATE;

    //    private String version_app = "1.3";
    @SuppressLint("SetTextI18n")
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

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        versi = findViewById(R.id.versi);
        versi.setText("Version " + version_app);
        sessionmanager = new Sessionmanager(this);
        title = findViewById(R.id.title);
        progresBar = findViewById(R.id.progresBar);
        Log.d("FirebaseInit", "Firebase initialized successfully");
//        cekVersiApp();
        cekUpdate();
//        activityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartIntentSenderForResult        (),
//                result -> {
//                    if (result.getResultCode() == RESULT_OK) {
//                        // Update berhasil atau dalam proses
//                        Snackbar.make(findViewById(android.R.id.content), "Update sedang berlangsung...", Snackbar.LENGTH_LONG).show();
//                    } else {
//                        // Tangani situasi di mana pengguna menolak update atau update gagal
//                        Snackbar.make(findViewById(android.R.id.content), "Update gagal atau ditolak!", Snackbar.LENGTH_LONG).show();
//                    }
//                }
//        );
    }
    private void cekUpdate(){
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE);
                Log.e(TAG, "cekUpdate: ");
                showAlert(appUpdateInfo);
            }else {
                Log.e(TAG, "cekUpdate: Sudah ter update");
                refreshSession();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error saat memeriksa pembaruan aplikasi: " + e.getMessage());
            refreshSession();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                // If an update is already in progress, resume it.
                mulaiPembaruan(appUpdateInfo);
            }
        });

        appUpdateManager.registerListener(installStateUpdatedListener);

    }

    private final InstallStateUpdatedListener installStateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification and request user confirmation to restart the app.
            popupSnackbarForCompleteUpdate();
        } else if (installState.installStatus() == InstallStatus.CANCELED) {
            Log.e(TAG, "Update was canceled by the user.");
            refreshSession();
        }
    };

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "Aplikasi sudah terupdate, aplikasi akan restart.",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            appUpdateManager.completeUpdate();

            // Tutup aplikasi
            finishAffinity();

            // Buka kembali aplikasi
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }, 3000); // 3 detik
    }

    private void cekTglUpdatejson(){
        String datenow =  sharedPref.getString("tgl_toll","empty");

        serviceAPI = ApiClient.getClient(this);
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
//                            updateFileToll();
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
        assert token != null;
        if(!token.equals("Bearer null")){
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    if (response.isSuccessful()) {
                        // Handle successful response here
//                        cekVersiApp();
                        verifyStoragePermissions(MainActivity.this);
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
//                        }
                        // You can also show a Toast or perform other actions as needed
                    } else {
                        // Handle other error codes
                        Log.d(TAG, "Error: " + response.code() + " Oh yeah  " + response.toString());
                        Toast.makeText(getApplicationContext(), "Error: " + response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("Error Data refresh", t.getMessage());
                    Toast.makeText(getApplicationContext(),"Error API" + t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            verifyStoragePermissions(MainActivity.this);
        }
    }
    private void cekVersiApp(){
        serviceAPI = ApiClient.getServiceNew(this);
        Call<JsonObject> call = serviceAPI.cekVersion(version_app);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if(response.body() != null){
                        JSONObject dataRes = new JSONObject(response.body().toString());
                        Log.d("adefe", "Aplikasi sudah di versi : "+ dataRes.getBoolean("status"));
                        if (dataRes.getBoolean("status")){
                            refreshSession();
//                            cekTglUpdatejson();
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
                    }else {
                        Log.d(TAG, "onResponse: " + response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data Versi", Objects.requireNonNull(Objects.requireNonNull(t.getMessage())));
            }
        });

    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void showAlert(AppUpdateInfo appUpdateInfo){
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
        alertDialogBuilder.setTitle("Update App");
        alertDialogBuilder.setIcon(R.drawable.logojm);
        alertDialogBuilder.setMessage("Versi terbaru telah tersedia");
        alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Perbarui Sekarang", (dialog, which) -> {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.jasamarga.jid"));
//            startActivity(intent);
            mulaiPembaruan(appUpdateInfo);

        });
        alertDialogBuilder.show();
    }

    private void mulaiPembaruan(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this, // Aktivitas saat ini
                    REQUEST_CODE_UPDATE); // Kode permintaan pembaruan, dapatkan dari konstanta atau definisikan sendiri
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
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
//            Toast.makeText(getApplicationContext(),"Silahkan Login !",Toast.LENGTH_LONG).show();
            verifyStoragePermissions(MainActivity.this);
            title.setVisibility(View.VISIBLE);
            progresBar.setVisibility(View.VISIBLE);
        });
        alertDialogBuilder.show();
    }
    private void updateFileLalin(){
        serviceAPI = ApiClient.getNoClient(this);
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
        serviceAPI = ApiClient.getNoClient(this);
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
        int locationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        // Izin notifikasi
        int notificationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Izin penyimpanan pada Android 13 atau lebih baru
            if (locationPermission != PackageManager.PERMISSION_GRANTED ||
                    notificationPermission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS
                }, REQUEST_PERMISSIONS_CODE);
            } else {
                cekKodisi();
            }
        } else {
            // Izin pada Android 12 atau lebih lama
            if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_PERMISSIONS_CODE);
            } else {
                cekKodisi();
            }
        }


//        showpremision();
    }
    // Di sini Anda perlu menambahkan metode onRequestPermissionsResult() untuk menangani hasil permintaan izin.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                cekKodisi();
            } else {
                Toast.makeText(this, "Silahkan izinkan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}