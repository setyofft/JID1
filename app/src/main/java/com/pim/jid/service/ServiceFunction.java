package com.pim.jid.service;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifBitmapProvider;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableResource;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.pim.jid.LoadingDialog;
import com.pim.jid.R;
import com.pim.jid.Sessionmanager;
import com.pim.jid.router.ApiClient;
import com.pim.jid.router.ReqInterface;

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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceFunction {

    public static void delSession(Context context, LoadingDialog loadingDialog, String username, Sessionmanager sessionmanager) {
        loadingDialog = new LoadingDialog(((Activity) context));
        loadingDialog.showLoadingDialog("Loading...");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", username);

        ReqInterface serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutedelsession(jsonObject);
        LoadingDialog finalLoadingDialog = loadingDialog;
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        sessionmanager.logout();
                        ((Activity) context).finish();
                    }else{
                        Log.d("STATUS", response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finalLoadingDialog.hideLoadingDialog();
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
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(url)
                .override(350, 200)
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void iconImage(Style style,Activity context) {
        style.addImageAsync("main_rood_off", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.cctv_r_24))));
        style.addImageAsync("main_rood_on", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.cctv_b_24))));
        style.addImageAsync("arteri_off", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.arteri_off))));
        style.addImageAsync("arteri_on", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.arteri_on))));
        style.addImageAsync("genangan_off", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.genangan_off))));
        style.addImageAsync("genangan_on", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.genangan_on))));
        style.addImageAsync("gerbang_off", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.gerbang_off))));
        style.addImageAsync("gerbang_on", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.gerbang_on))));
        style.addImageAsync("ss_off", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.ss_off))));
        style.addImageAsync("ss_on", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.ss_on))));
        style.addImageAsync("ramp_off", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.rampp_off))));
        style.addImageAsync("ramp_on", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.ramp_on))));
        style.addImageAsync("elevated_on", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.elevated_on))));
        style.addImageAsync("elevated_off", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.elevated_off))));
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
        style.addImageAsync("rampimgon", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.gate_b_32))));
        style.addImageAsync("rampimgoff", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.gate_r_32))));
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
        style.addImageAsync("radarimg", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.radar_g_24))));

        style.addImageAsync("midasimg", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                context.getDrawable(R.drawable.circle_radar))));
    }

    public static void pesanNosignal(WebView content_antrian_gerbang,Activity activity){
        content_antrian_gerbang.setVisibility(WebView.GONE);
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
        alertDialogBuilder.setTitle("Peringatan Akun");
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
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retDatajson;
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
