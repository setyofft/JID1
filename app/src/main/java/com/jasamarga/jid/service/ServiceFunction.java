package com.jasamarga.jid.service;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.mapbox.mapboxsdk.style.expressions.Expression.e;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.jasamarga.jid.models.model_notif.ControllerNotif;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ReqInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceFunction {
    public static String loadJSONFromAsset(Activity activity,String search) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open(search);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public static void addLogActivity(Activity activity,String klas,String error,String desc){
        SharedPreferences prefs = activity.getSharedPreferences("TOKEN_PREF", MODE_PRIVATE);
        String token = prefs.getString("token", "");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token",token);
        jsonObject.addProperty("device",FirebaseService.getDeviceName());
        jsonObject.addProperty("classname",klas);
        jsonObject.addProperty("operation","show");
        jsonObject.addProperty("description","Mengakses "+desc);
        jsonObject.addProperty("status",1);
        jsonObject.addProperty("exception",error);


        ReqInterface serviceAPI = ApiClient.getClient(activity);
        Call<JsonObject> call = serviceAPI.excutelogactivity(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                assert response.body() != null;
                Log.d(TAG, "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });



    }
    public static int getBadge(Activity activity){
        SharedPreferences prefs = activity.getSharedPreferences("BADGE", MODE_PRIVATE);
        int token = prefs.getInt("badge", 0);
        Log.d(TAG, "getBadge: " + token);
        return token;

    }

    public static void getDataBadge(Activity activity) {
        Sessionmanager sessionmanager =new Sessionmanager(activity);
        HashMap<String,String> user = sessionmanager.getUserDetails();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("limit", 100);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        ReqInterface serviceAPI = ApiClient.getClient(activity);
        Call<ControllerNotif> call = serviceAPI.excutenotif(user.get(Sessionmanager.nameToken),jsonObject);
        call.enqueue(new Callback<ControllerNotif>() {
            @Override
            public void onResponse(Call<ControllerNotif> call, Response<ControllerNotif> response) {
                SharedPreferences.Editor editor = activity.getSharedPreferences("BADGE", MODE_PRIVATE).edit();
                if (response.body() != null){
                    if (response.body().getCount() !=null){
                        editor.putString("token", String.valueOf(response.body().getCount()));
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<ControllerNotif> call, Throwable t) {
                Log.d("Error Data", t.getMessage());

            }
        });
    }

    public static String getMathRandomWebview(){
        double math = Math.random();
        String path = "?t=" + math;
        return path;
    }

    public static boolean initCheckItem(Activity context,String search){
        Sessionmanager sessionmanager = new Sessionmanager(context);
        HashMap<String, String> user = sessionmanager.getUserDetails();
        String item = user.get(Sessionmanager.set_dashboard);
        String item2 = user.get(Sessionmanager.set_dashboard);
        String name = user.get(Sessionmanager.pref_name);

        String[] arrItems = null;

        arrItems = item.split(",");

        for (int i = 0; i < arrItems.length; i++) {
            if (arrItems[i].equals(search)) return true;
        }
        return false;

    }
    public static String getUserRole(Activity context,String search){
        Sessionmanager sessionmanager = new Sessionmanager(context);
        HashMap<String, String> user = sessionmanager.getUserDetails();
        String name = user.get(Sessionmanager.pref_name);
        String item = user.get(Sessionmanager.set_dashboard);
        String item2 = user.get(Sessionmanager.set_item);


        if (search.equals("item")) {
            return (item2 != null) ? item2 : "0";
        } else if (search.equals("dash")) {
            return (item != null) ? item : "0";
        } else if (search.equals("name")) {
            return (name != null) ? name : "0";
        }
        return "0";


    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        Log.d(TAG, "getDeviceName: " + capitalize(model));

        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char firstChar = s.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return Character.toUpperCase(firstChar) + s.substring(1);
        } else {
            return s;
        }
    }
    public static void delSession(Activity context, LoadingDialog loadingDialog, String username, Sessionmanager sessionmanager) {
        loadingDialog = new LoadingDialog(((Activity) context));
        loadingDialog.showLoadingDialog("Loading...");
        HashMap<String,String> hashMap = sessionmanager.getUserDetails();
        String token = hashMap.get(Sessionmanager.nameToken);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", username);

        ReqInterface serviceAPI = ApiClient.getClient(context);
        ReqInterface serviceAPI2 = ApiClient.getServiceNew(context);

        LoadingDialog finalLoadingDialog = loadingDialog;

        Call<JsonObject> call2 = serviceAPI2.logout(token);

        String fullUrl = call2.request().url().toString();
        Log.d(TAG, "delSession: " + fullUrl);

        call2.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() !=null){
                    try {
                        JSONObject dataRes = new JSONObject(response.body().toString());
                        Log.d(TAG, "onResponse: " + dataRes);
                        if (dataRes.getBoolean("status")){
                                sessionmanager.logout();
                                ((Activity) context).finish();
                        }else {
                            Toast.makeText(context,dataRes.getString("message"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }else if (response.code() == 500) {
                    // Handle 401 unauthorized error
                    Log.d(ContentValues.TAG, "Unauthorized: " + response.message());
//                        if (response.message().contains("Unauthorized")){

//                        }
                    // You can also show a Toast or perform other actions as needed
                }else {
                    Toast.makeText(context,"Logout gagal" , Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse ERROR LOGOUT: "+ response.message()+ response.errorBody());
                    finalLoadingDialog.hideLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Log.d("Error Data", call.toString());
                finalLoadingDialog.hideLoadingDialog();
            }
        });

    }

    public static void initStreamImg(Context context,String img_url,String key, ImageView img, ProgressBar loadingIMG) {
        String url = "https://jid.jasamarga.com/cctv2/"+key+"?tx="+Math.random();
        Glide.with(context)
                .asBitmap()
                .load(img_url)
                .override(350, 250)
                .centerInside()
                .error(R.drawable.blank_photo)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        loadingIMG.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        loadingIMG.setVisibility(View.GONE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource,
                                                @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        img.setImageBitmap(resource);
                    }
                });
    }
    private static Bitmap getResizedBitmap(Context context, int drawableId, int width, int height) {
        Drawable drawable = context.getDrawable(drawableId);
        Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(drawable);
        assert bitmap != null;
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void iconImage(Style style,Activity context) {

        style.addImageAsync("main_rood_off", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_main_off, 25, 25)));
        style.addImageAsync("main_rood_on", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_main, 25, 25)));
        style.addImageAsync("arteri_off", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_arteri_off, 25, 25)));
        style.addImageAsync("arteri_on", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_arteri, 25, 25)));
        style.addImageAsync("genangan_off", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_pemantauan_off, 25, 25)));
        style.addImageAsync("genangan_on", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_pemantauan, 25, 25)));
        style.addImageAsync("gerbang_off", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_gerbang_off, 25, 25)));
        style.addImageAsync("gerbang_on", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_gerbang, 25, 25)));
        style.addImageAsync("ss_off", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_ss_off, 25, 25)));
        style.addImageAsync("ss_on", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_ss, 25, 25)));
        style.addImageAsync("ramp_off", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_ramp_off, 25, 25)));
        style.addImageAsync("ramp_on", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_ramp, 25, 25)));
        style.addImageAsync("elevated_on", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_fly_over, 25, 25)));
        style.addImageAsync("elevated_off", Objects.requireNonNull(getResizedBitmap(
                context, R.drawable.cctv_fly_over_off, 25, 25)));
        style.addImageAsync("vms_off", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.vms_off))));
        style.addImageAsync("vms_on", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.vms_on))));
        style.addImageAsync("gangguan_lalin", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.crash_32))));
        style.addImageAsync("pemeliharaanimg", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.repair_32))));
        style.addImageAsync("rekayasalalinimg", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.control_32))));
        style.addImageAsync("arrow", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.arrow_line_biru))));
        style.addImageAsync("rekayasapengalihan", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.noway_24))));
        style.addImageAsync("bataskmimg", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.km_20))));
        style.addImageAsync("rampimgon", Objects.requireNonNull(getResizedBitmap(context,
                R.drawable.gerbang,35,35)));
        style.addImageAsync("rampimgoff", Objects.requireNonNull(getResizedBitmap(context,
                R.drawable.gerbang,35,35)));

        style.addImageAsync("RAMAI", Objects.requireNonNull(getResizedBitmap(context,
                R.drawable.gerbang_ramai,35,35)));
        style.addImageAsync("NORMAL", Objects.requireNonNull(getResizedBitmap(context,
                R.drawable.gerbang,35,35)));
        style.addImageAsync("PADAT", Objects.requireNonNull(getResizedBitmap(context,
                R.drawable.gerbang_padat,35,35)));

        style.addImageAsync("restareanormal", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.park_32))));
        style.addImageAsync("restareapenuh", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.busy_park_32))));
        style.addImageAsync("rtmson", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.rtms_b_24))));
        style.addImageAsync("rtmsoff", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.rtms_r_24))));
        style.addImageAsync("rtms2on", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.rtms2_g_24))));
        style.addImageAsync("rtms2off", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.rtms2_p_24))));
        style.addImageAsync("speedimg", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.speed_b_24))));
        style.addImageAsync("levelimg", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.level_g_32))));
        style.addImageAsync("pompaimg", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.pump_24))));
        style.addImageAsync("wimimg", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.wim_32))));
        style.addImageAsync("bikeimg", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.bike_g_30))));
        style.addImageAsync("ambulance_30", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.ambulance_30))));
        style.addImageAsync("derek_30", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.derek_30))));
        style.addImageAsync("kamtib_30", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.kamtib_30))));
        style.addImageAsync("patroli_30", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.patroli_30))));
        style.addImageAsync("support_30", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.support_30))));
        style.addImageAsync("radaron", Objects.requireNonNull(getResizedBitmap(context,
                R.drawable.radar_new,200,200)));
        style.addImageAsync("radaroff", Objects.requireNonNull(getResizedBitmap(context,
                R.drawable.radar_new,200,200)));
        style.addImageAsync("midasimg", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.circle_radar))));
    }
    public static void pesanNosignal(WebView content_antrian_gerbang,Activity activity){
        content_antrian_gerbang.setVisibility(WebView.GONE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setTitle("Silahkan Cek Internet Anda !");
        alertDialogBuilder
                .setMessage("Klik 'YA' Untuk Coba Lagi !")
                .setIcon(R.drawable.logojm)
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        activity.finish();
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void pesanNosignalDefault(Activity activity){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setTitle("Silahakan Cek Internet Anda !");
        alertDialogBuilder
                .setMessage("Klik 'YA' Untuk Coba Lagi !")
                .setIcon(R.drawable.logojm)
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        activity.finish();
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public static boolean Terkoneksi(Activity activity){
        boolean connectStatus = true;
        ConnectivityManager ConnectionManager=(ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true ) {
            connectStatus = true;
        }
        else {
            connectStatus = false;
        }
        return connectStatus;
    }

    public static void showLogout(Activity activity,LoadingDialog loadingDialog, String username, Sessionmanager sessionmanager){
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity);
        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
        alertDialogBuilder.setBackground(activity.getResources().getDrawable(R.drawable.modal_alert));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> delSession(activity,loadingDialog,username,sessionmanager));
        alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
        alertDialogBuilder.show();
    }

    public static String getFile(String nmFile,Activity activity){
        String retDatajson = null;
        try {
            String path = activity.getApplicationContext().getExternalFilesDir(null) + "/datajid/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String directory_path = path + nmFile;
            InputStream inputStream = new FileInputStream(directory_path);
            if(inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                retDatajson = stringBuilder.toString();
            }else{

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retDatajson;
    }
    public static String getFileAssets(String nmFile, Context context) {
        String retDatajson = null;
        AssetManager assetManager = context.getAssets();
        try {
            // Ubah path untuk mengambil dari folder 'assets'
            InputStream inputStream = assetManager.open( nmFile);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String receiveString;
            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }
            inputStream.close();
            retDatajson = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retDatajson;
    }
    public static String getJsonFileContent(String fileName,Context context) {
        String jsonContent = null;
        String path = context.getExternalFilesDir(null) + "/datajid/" + fileName;
        File file = new File(path);

        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                jsonContent = convertStreamToString(fis);
            } else {
                InputStream is = context.getAssets().open(fileName);
                jsonContent = convertStreamToString(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonContent;
    }

    public static  String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

//    public static String convertUrlToBase64(String url) {
//        URL newurl;
//        Bitmap bitmap;
//        String base64 = "";
//        try {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//            newurl = new URL(url);
//            bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//            base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return base64;
//    }

    public static void initLineToll(Style style,String datalain) {
        try {
            style.addSource(new GeoJsonSource("toll", new URI("https://jid.jasamarga.com/map/tol.json")));
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
        }
        style.addLayer(new LineLayer("finaltoll", "toll").withProperties(
                PropertyFactory.lineColor(Color.GRAY),
                PropertyFactory.lineWidth(3f)
        ));
        FeatureCollection featureCollectionvms = FeatureCollection.fromJson(datalain);
        style.addSource(new GeoJsonSource("lalin", featureCollectionvms.toJson()));
        style.addLayer(new LineLayer("finallalin", "lalin").withProperties(
                PropertyFactory.lineColor(
                        match(get("color"), rgb(0, 0, 0),
                                stop("#ffcc00", "#ffcc00"),
                                stop("#ff0000", "#ff0000"),
                                stop("#bb0000", "#bb0000"),
                                stop("#440000", "#440000"),
                                stop("#00ff00", "#00ff00")
                        )),
                PropertyFactory.lineWidth(2f)
        ));
    }

}
