package com.jasamarga.jid.views;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.jasamarga.jid.components.PopupDetailLalin.TAG;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.jasamarga.jid.BuildConfig;
import com.jasamarga.jid.Dashboard;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.R;
import com.jasamarga.jid.ServiceRealtime;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.MenuAdapter;
import com.jasamarga.jid.adapter.UserSetting;
import com.jasamarga.jid.components.ShowAlert;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.service.ServiceKondisiLalin;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.CannotAddSourceException;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Maps extends AppCompatActivity {

    Sessionmanager sessionmanager;
    ServiceRealtime serviceRealtime;
    ServiceKondisiLalin serviceKondisiLalin;
    private MapView mapView;
    private LoadingDialog loadingDialog;
    private ReqInterface serviceAPI;
    private Handler handler_vms, handler_cctv;
    private Style styleSet;
    private MapboxMap mapboxMapSet;

    private String img_url, scope, username,
            img_vms, justify, ngisor, overlap_status = "aktif", item,
            aktif_popup_lalin = "false";
    private Button logout;
    private Integer count;
    private FloatingActionButton setting_layer, center_fit, overlap_symbol, refresh_data, list_menu;
    BottomSheetBehavior sheetBehavior;
    private BottomSheetDialog sheetDialog;
    View bottom_sheet;
    private UserSetting userSetting;
    AlertDialog alertDialogLineToll;
    private ValueAnimator markerAnimator;
    private String token;
    private ExoPlayer player;
    private String uri;
    private ImageView img;
    private ProgressBar loadingIMG;
    private PlayerView playerView;
    private TextView set_cctv_off;
    private String key_id,id_ruas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, BuildConfig.MAPBOX_ACCESS_TOKEN);
        setContentView(R.layout.activity_main);
        menuBottomnavbar();

        sessionmanager = new Sessionmanager(this);
        serviceRealtime     = new ServiceRealtime(this);
        serviceKondisiLalin = new ServiceKondisiLalin(this);

        userSetting = (UserSetting)getApplication();

        HashMap<String, String> user = sessionmanager.getUserDetails();
        scope = user.get(Sessionmanager.set_scope);
        username = user.get(Sessionmanager.kunci_id);
        item = user.get(Sessionmanager.set_item);
        token = user.get(Sessionmanager.nameToken);

        ngisor = "top";
        justify = "center";

        mapView = findViewById(R.id.mapView);

        initMaps();
        initAction();
    }

    private void initAction() {
        setting_layer = findViewById(R.id.setting_layer);
        bottom_sheet = findViewById(R.id.bottom_sheet_dialog);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        setting_layer.setOnClickListener(v -> {
            View view = getLayoutInflater().inflate(R.layout.layer_dialog, null);

            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            sheetDialog = new BottomSheetDialog(this, R.style.BottomSheetTheme);
            Chip switch_jalan_toll = view.findViewById(R.id.switch_jalan_toll);
            Chip switch_kondisi_traffic = view.findViewById(R.id.switch_kondisi_traffic);
            Chip switch_cctv = view.findViewById(R.id.switch_cctv);
            Chip switch_vms = view.findViewById(R.id.switch_vms);
            TextView txtPemeliharaan = view.findViewById(R.id.txt_pemeliharaan);
            ImageView img_pemeliharaan = view.findViewById(R.id.img_pemeliharaan);
            TextView txtGangguan = view.findViewById(R.id.txt_gangguan);
            ImageView img_Gangguan = view.findViewById(R.id.img_gangguan);
            TextView txt_rekayasa = view.findViewById(R.id.txt_rekayasa);
            ImageView img_rekayasa = view.findViewById(R.id.img_rekayasa);
            AtomicBoolean gangguan = new AtomicBoolean(true);
            AtomicBoolean rekayasa = new AtomicBoolean(true);
            AtomicBoolean pemeliharaan = new AtomicBoolean(true);

            int colorGangguan = img_Gangguan.isSelected() ? getResources().getColor(R.color.birujid) : getResources().getColor(R.color.gray3);
            int colorPemeliharaan = img_pemeliharaan.isSelected() ? getResources().getColor(R.color.birujid) : getResources().getColor(R.color.gray3);
            int colorRekayasa = img_rekayasa.isSelected() ? getResources().getColor(R.color.birujid) : getResources().getColor(R.color.gray3);

            txtGangguan.setTextColor(colorGangguan);
            img_Gangguan.setColorFilter(colorGangguan);

            img_pemeliharaan.setColorFilter(colorPemeliharaan);
            txtPemeliharaan.setTextColor(colorPemeliharaan);

            img_rekayasa.setColorFilter(colorRekayasa);
            txt_rekayasa.setTextColor(colorRekayasa);

            img_Gangguan.setOnClickListener(view1 -> {
                boolean newValue = !gangguan.get();
                gangguan.set(newValue);
                img_Gangguan.setSelected(gangguan.get());
                txtGangguan.setSelected(gangguan.get());

                int colorGangguans = img_Gangguan.isSelected() ? getResources().getColor(R.color.birujid) : getResources().getColor(R.color.gray3);
                txtGangguan.setTextColor(colorGangguans);
                img_Gangguan.setColorFilter(colorGangguans);
                Log.d(TAG, "initAction: TEXTCLICK" + gangguan.get());

            });

            img_pemeliharaan.setOnClickListener(view1 -> {
                boolean newValue = !pemeliharaan.get(); // Ambil nilai sebaliknya
                pemeliharaan.set(newValue);
                img_pemeliharaan.setSelected(pemeliharaan.get());
                txtPemeliharaan.setSelected(pemeliharaan.get());

                int colorPemeliharaans = img_pemeliharaan.isSelected() ? getResources().getColor(R.color.birujid) : getResources().getColor(R.color.gray3);
                img_pemeliharaan.setColorFilter(colorPemeliharaans);
                txtPemeliharaan.setTextColor(colorPemeliharaans);
                Log.d(TAG, "initAction: TEXTCLICK" + pemeliharaan.get());
            });

            img_rekayasa.setOnClickListener(view1 -> {
                boolean newValue = !rekayasa.get(); // Ambil nilai sebaliknya
                rekayasa.set(newValue);
                img_rekayasa.setSelected(rekayasa.get());
                txt_rekayasa.setSelected(rekayasa.get());
                int colorRekayasas = img_rekayasa.isSelected() ? getResources().getColor(R.color.birujid) : getResources().getColor(R.color.gray3);
                img_rekayasa.setColorFilter(colorRekayasas);
                txt_rekayasa.setTextColor(colorRekayasas);
                Log.d(TAG, "initAction: TEXTCLICK" + rekayasa.get());
            });
//            Chip switch_pemeliharaan = view.findViewById(R.id.switch_pemeliharaan);
//            Chip switch_gangguan_lalin = view.findViewById(R.id.switch_gangguan_lalin);
//            Chip switch_rekayasalalin = view.findViewById(R.id.switch_rekayasalalin);
            Chip switch_batas_km = view.findViewById(R.id.switch_batas_km);
            Chip switch_jalan_penghubung = view.findViewById(R.id.switch_jalan_penghubung);
            Chip switch_gerbang_tol = view.findViewById(R.id.switch_gerbang_tol);
            Chip switch_rest_Area = view.findViewById(R.id.switch_rest_Area);
            Chip switch_roughnes_index = view.findViewById(R.id.switch_roughnes_index);
            Chip switch_rtms = view.findViewById(R.id.switch_rtms);
            Chip switch_rtms2 = view.findViewById(R.id.switch_rtms2);
            Chip switch_radar = view.findViewById(R.id.switch_radar);
            Chip switch_speed = view.findViewById(R.id.switch_speed);
            Chip switch_water_level = view.findViewById(R.id.switch_water_level);
            Chip switch_pompa_banjir = view.findViewById(R.id.switch_pompa_banjir);
            Chip switch_wim_bridge = view.findViewById(R.id.switch_wim_bridge);
            Chip switch_gps_kend_opra = view.findViewById(R.id.switch_gps_kend_opra);
            Chip switch_sepeda_montor = view.findViewById(R.id.switch_sepeda_montor);
            switch_sepeda_montor.setVisibility(View.GONE);
            GridLayout chipGroup = view.findViewById(R.id.grid_layer);
            GridLayout chipGroup2 = view.findViewById(R.id.grid_jalan_tol);

            Button set_layer = view.findViewById(R.id.set_layer);

            initSetChecked(switch_jalan_toll, switch_kondisi_traffic, switch_cctv, switch_vms,img_pemeliharaan,img_Gangguan,img_rekayasa,txtPemeliharaan,txtGangguan,txt_rekayasa,
                    switch_batas_km, switch_jalan_penghubung, switch_gerbang_tol,
                    switch_rest_Area, switch_roughnes_index, switch_rtms, switch_rtms2, switch_speed, switch_water_level, switch_pompa_banjir,
                    switch_wim_bridge, switch_gps_kend_opra, switch_sepeda_montor, switch_radar);

            initNotScope(view,switch_jalan_toll, switch_kondisi_traffic, switch_cctv, switch_vms,img_pemeliharaan,img_Gangguan,img_rekayasa,txtPemeliharaan,txtGangguan,txt_rekayasa, switch_batas_km, switch_jalan_penghubung, switch_gerbang_tol,
                    switch_rest_Area, switch_roughnes_index, switch_rtms, switch_rtms2, switch_speed, switch_water_level, switch_pompa_banjir,
                    switch_wim_bridge, switch_gps_kend_opra, switch_sepeda_montor, switch_radar,chipGroup,chipGroup2);

            set_layer.setOnClickListener(v1 -> initKondisiAktif(sheetDialog, switch_jalan_toll, switch_kondisi_traffic, switch_cctv, switch_vms, img_pemeliharaan,img_Gangguan,img_rekayasa,txtPemeliharaan,txtGangguan,txt_rekayasa, switch_batas_km, switch_jalan_penghubung, switch_gerbang_tol,
                    switch_rest_Area, switch_roughnes_index, switch_rtms, switch_rtms2, switch_speed, switch_water_level, switch_pompa_banjir,
                    switch_wim_bridge, switch_gps_kend_opra, switch_sepeda_montor, switch_radar));

            onCLickCekChip(switch_jalan_toll, switch_kondisi_traffic, switch_cctv, switch_vms, img_pemeliharaan,img_Gangguan,img_rekayasa,txtPemeliharaan,txtGangguan,txt_rekayasa, switch_batas_km, switch_jalan_penghubung, switch_gerbang_tol,
                    switch_rest_Area, switch_roughnes_index, switch_rtms, switch_rtms2, switch_speed, switch_water_level, switch_pompa_banjir,
                    switch_wim_bridge, switch_gps_kend_opra, switch_sepeda_montor, switch_radar);
            sheetDialog.setContentView(view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                sheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            sheetDialog.show();

            sheetDialog.setOnDismissListener(dialog -> sheetDialog.dismiss());

        });

    }

    boolean initCheckItemsLayars(String search)
    {
        String[] arrItems = null;
        arrItems = item.split(",");
        Log.d(TAG, "initCheckItemsLayars: "+item);
        for (int i = 0; i < arrItems.length; i++) {
            if (arrItems[i].equals(search)) return true;
        }
        return false;
    }

    private void onCLickCekChip(Chip switch_jalan_toll, Chip switch_kondisi_traffic, Chip switch_cctv, Chip switch_vms,
                                ImageView img_pemeliharaan, ImageView img_gangguan, ImageView img_rekayasa, TextView txt_pemeliharaan, TextView txt_gangguan,TextView txt_rekayasa, Chip switch_batas_km, Chip switch_jalan_penghubung, Chip switch_gerbang_tol,
                                Chip switch_rest_Area, Chip switch_roughnes_index, Chip switch_rtms, Chip switch_rtms2,
                                Chip switch_speed, Chip switch_water_level, Chip switch_pompa_banjir,
                                Chip switch_wim_bridge, Chip switch_gps_kend_opra, Chip switch_sepeda_montor, Chip switch_radar)
    {
        SharedPreferences.Editor editor = getSharedPreferences(UserSetting.PREFERENCES, MODE_PRIVATE).edit();

        switch_jalan_toll.setOnClickListener(v12 -> {
            if (switch_jalan_toll.isChecked()){
                switch_jalan_toll.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                switch_jalan_toll.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                switch_jalan_toll.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
            }else{
                switch_jalan_toll.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                switch_jalan_toll.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                switch_jalan_toll.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            }
        });

        switch_kondisi_traffic.setOnClickListener(v12 -> {
            if (switch_kondisi_traffic.isChecked()){
                switch_kondisi_traffic.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                switch_kondisi_traffic.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                switch_kondisi_traffic.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
            }else{
                switch_kondisi_traffic.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                switch_kondisi_traffic.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                switch_kondisi_traffic.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            }
        });

        switch_cctv.setOnClickListener(v12 -> {
            if (initCheckItemsLayars("cctv")){
                if (switch_cctv.isChecked()){
                    switch_cctv.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                    switch_cctv.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                    switch_cctv.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                }else{
                    switch_cctv.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    switch_cctv.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                    switch_cctv.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                }
            }else{
                Toast.makeText(getApplicationContext(), "Anda belum ada akses ke layar ini ! ", Toast.LENGTH_SHORT).show();
            }

        });

        switch_vms.setOnClickListener(v12 -> {
            if (initCheckItemsLayars("vms")){
                if (switch_vms.isChecked()){
                    switch_vms.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                    switch_vms.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                    switch_vms.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                }else{
                    switch_vms.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    switch_vms.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                    switch_vms.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                }
            }else{
                Toast.makeText(getApplicationContext(), "Anda belum ada akses ke layar ini ! ", Toast.LENGTH_SHORT).show();
            }
        });

//        img_pemeliharaan.setOnClickListener(v12 -> {
//            if (img_pemeliharaan.isSelected()){
//                switch_pemeliharaan.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
//                switch_pemeliharaan.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
//                switch_pemeliharaan.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
//            }else{
//                switch_pemeliharaan.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
//                switch_pemeliharaan.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//                switch_pemeliharaan.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//            }
//        });
//
//        switch_gangguan_lalin.setOnClickListener(v12 -> {
//            if (switch_gangguan_lalin.isChecked()){
//                switch_gangguan_lalin.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
//                switch_gangguan_lalin.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
//                switch_gangguan_lalin.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
//            }else{
//                switch_gangguan_lalin.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
//                switch_gangguan_lalin.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//                switch_gangguan_lalin.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//            }
//        });
//
//        switch_rekayasalalin.setOnClickListener(v12 -> {
//            if (switch_rekayasalalin.isChecked()){
//                switch_rekayasalalin.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
//                switch_rekayasalalin.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
//                switch_rekayasalalin.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
//            }else{
//                switch_rekayasalalin.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
//                switch_rekayasalalin.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//                switch_rekayasalalin.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//            }
//        });

        switch_batas_km.setOnClickListener(v12 -> {
            if (switch_batas_km.isChecked()){
                switch_batas_km.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                switch_batas_km.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                switch_batas_km.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
            }else{
                switch_batas_km.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                switch_batas_km.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                switch_batas_km.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            }
        });

        switch_jalan_penghubung.setOnClickListener(v -> {
            if (switch_jalan_penghubung.isChecked()){
                switch_jalan_penghubung.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                switch_jalan_penghubung.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                switch_jalan_penghubung.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
            }else{
                switch_jalan_penghubung.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                switch_jalan_penghubung.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                switch_jalan_penghubung.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            }
        });

        switch_gerbang_tol.setOnClickListener(v -> {
            if (switch_gerbang_tol.isChecked()){
                switch_gerbang_tol.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                switch_gerbang_tol.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                switch_gerbang_tol.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
            }else{
                switch_gerbang_tol.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                switch_gerbang_tol.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                switch_gerbang_tol.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            }
        });

        switch_rest_Area.setOnClickListener(v -> {
            if (switch_rest_Area.isChecked()){
                switch_rest_Area.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                switch_rest_Area.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                switch_rest_Area.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
            }else{
                switch_rest_Area.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                switch_rest_Area.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                switch_rest_Area.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            }
        });

        switch_roughnes_index.setOnClickListener(v -> {
            if (switch_roughnes_index.isChecked()){
                switch_roughnes_index.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                switch_roughnes_index.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                switch_roughnes_index.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
            }else{
                switch_roughnes_index.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                switch_roughnes_index.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                switch_roughnes_index.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            }
        });

        switch_rtms.setOnClickListener(v -> {
            if (initCheckItemsLayars("rtms")){
                if (switch_rtms.isChecked()){
                    switch_rtms.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                    switch_rtms.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                    switch_rtms.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                }else{
                    switch_rtms.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    switch_rtms.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                    switch_rtms.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                }
            }else{
                Toast.makeText(getApplicationContext(), "Anda belum ada akses ke layar ini ! ", Toast.LENGTH_SHORT).show();
            }
        });
        switch_rtms2.setOnClickListener(v -> {
            if (initCheckItemsLayars("rtms2")){
                if (switch_rtms2.isChecked()){
                    switch_rtms2.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                    switch_rtms2.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                    switch_rtms2.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                }else{
                    switch_rtms2.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    switch_rtms2.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                    switch_rtms2.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                }
            }else{
                Toast.makeText(getApplicationContext(), "Anda belum ada akses ke layar ini ! ", Toast.LENGTH_SHORT).show();
            }
        });

        switch_speed.setOnClickListener(v -> {
            if (initCheckItemsLayars("speed")){
                if (switch_speed.isChecked()){
                    switch_speed.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                    switch_speed.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                    switch_speed.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                }else{
                    switch_speed.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    switch_speed.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                    switch_speed.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                }
            }else{
                Toast.makeText(getApplicationContext(), "Anda belum ada akses ke layar ini ! ", Toast.LENGTH_SHORT).show();
            }
        });
        switch_water_level.setOnClickListener(v -> {
            if (initCheckItemsLayars("level")){
                if (switch_water_level.isChecked()){
                    switch_water_level.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                    switch_water_level.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                    switch_water_level.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                }else{
                    switch_water_level.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    switch_water_level.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                    switch_water_level.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                }
            }else{
                Toast.makeText(getApplicationContext(), "Anda belum ada akses ke layar ini ! ", Toast.LENGTH_SHORT).show();
            }
        });
        switch_pompa_banjir.setOnClickListener(v -> {
            if (initCheckItemsLayars("pump")){
                if (switch_pompa_banjir.isChecked()){
                    switch_pompa_banjir.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                    switch_pompa_banjir.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                    switch_pompa_banjir.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                }else{
                    switch_pompa_banjir.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    switch_pompa_banjir.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                    switch_pompa_banjir.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                }
            }else{
                Toast.makeText(getApplicationContext(), "Anda belum ada akses ke layar ini ! ", Toast.LENGTH_SHORT).show();
            }
        });
        switch_wim_bridge.setOnClickListener(v -> {
            if (initCheckItemsLayars("wim")){
                if (switch_wim_bridge.isChecked()){
                    switch_wim_bridge.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                    switch_wim_bridge.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                    switch_wim_bridge.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                }else{
                    switch_wim_bridge.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    switch_wim_bridge.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                    switch_wim_bridge.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                }
            }else{
                Toast.makeText(getApplicationContext(), "Anda belum ada akses ke layar ini ! ", Toast.LENGTH_SHORT).show();
            }
        });
        switch_sepeda_montor.setOnClickListener(v -> {
            if (initCheckItemsLayars("bike")){
                if (switch_sepeda_montor.isChecked()){
                    switch_sepeda_montor.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                    switch_sepeda_montor.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                    switch_sepeda_montor.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                }else{
                    switch_sepeda_montor.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    switch_sepeda_montor.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                    switch_sepeda_montor.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                }
            }else{
                Toast.makeText(getApplicationContext(), "Anda belum ada akses ke layar ini ! ", Toast.LENGTH_SHORT).show();
            }
        });
        switch_gps_kend_opra.setOnClickListener(v -> {
            if (initCheckItemsLayars("cars")){
                if (switch_gps_kend_opra.isChecked()){
                    switch_gps_kend_opra.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                    switch_gps_kend_opra.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                    switch_gps_kend_opra.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                }else{
                    switch_gps_kend_opra.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    switch_gps_kend_opra.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                    switch_gps_kend_opra.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                }
            }else{
                Toast.makeText(getApplicationContext(), "Anda belum ada akses ke layar ini ! ", Toast.LENGTH_SHORT).show();
            }
        });
        switch_radar.setOnClickListener(v -> {
            if (initCheckItemsLayars("radar")){
                if (switch_radar.isChecked()){
                    switch_radar.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
                    switch_radar.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                    switch_radar.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                }else{
                    switch_radar.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    switch_radar.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                    switch_radar.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
                }
            }else{
                Toast.makeText(getApplicationContext(), "Anda belum ada akses ke layar ini ! ", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void initNotScope(View view,Chip switch_jalan_toll, Chip switch_kondisi_traffic, Chip switch_cctv, Chip switch_vms,
                              ImageView img_pemeliharaan, ImageView img_gangguan, ImageView img_rekayasa, TextView txt_pemeliharaan, TextView txt_gangguan,TextView txt_rekayasa,
                              Chip switch_batas_km, Chip switch_jalan_penghubung, Chip switch_gerbang_tol,
                              Chip switch_rest_Area, Chip switch_roughnes_index, Chip switch_rtms, Chip switch_rtms2,
                              Chip switch_speed, Chip switch_water_level, Chip switch_pompa_banjir,
                              Chip switch_wim_bridge, Chip switch_gps_kend_opra, Chip switch_sepeda_montor, Chip switch_radar,GridLayout chipGroup,GridLayout chip_jalan){
        if (!initCheckItemsLayars("cctv")){
            switch_cctv.setVisibility(View.GONE);
            chipGroup.removeView(switch_cctv);
        }else {
            switch_cctv.setVisibility(View.VISIBLE);
        }
        if (!initCheckItemsLayars("vms")){
            switch_vms.setVisibility(View.GONE);
            chipGroup.removeView(switch_vms);
        }else {
            switch_vms.setVisibility(View.VISIBLE);

        }
        if (!initCheckItemsLayars("rtms")){
            switch_rtms.setVisibility(View.GONE);
            chipGroup.removeView(switch_rtms);

        }else {
            switch_rtms.setVisibility(View.VISIBLE);

        }
        if (!initCheckItemsLayars("rtms2")){
            switch_rtms2.setVisibility(View.GONE);
            chipGroup.removeView(switch_rtms2);

        }else {
            switch_rtms2.setVisibility(View.VISIBLE);

        }
        if (!initCheckItemsLayars("speed")){
            switch_speed.setVisibility(View.GONE);
            chipGroup.removeView(switch_speed);

        }else {
            switch_speed.setVisibility(View.VISIBLE);

        }
        if (!initCheckItemsLayars("level")){
            switch_water_level.setVisibility(View.GONE);
            chipGroup.removeView(switch_water_level);

        }else {
            switch_water_level.setVisibility(View.VISIBLE);
        }
        if (!initCheckItemsLayars("pump")){
            switch_pompa_banjir.setVisibility(View.GONE);
            chipGroup.removeView(switch_pompa_banjir);
        }else {
            switch_pompa_banjir.setVisibility(View.VISIBLE);

        }
        if (!initCheckItemsLayars("wim")){
            switch_wim_bridge.setVisibility(View.GONE);
            chipGroup.removeView(switch_wim_bridge);

        }else {
            switch_wim_bridge.setVisibility(View.VISIBLE);

        }
        if (!initCheckItemsLayars("bike")){
            switch_sepeda_montor.setVisibility(View.GONE);
            chipGroup.removeView(switch_sepeda_montor);

        }else {
            switch_sepeda_montor.setVisibility(View.GONE);
        }
        if (!initCheckItemsLayars("cars")){
            switch_gps_kend_opra.setVisibility(View.GONE);
            chipGroup.removeView(switch_gps_kend_opra);

        }else {
            switch_gps_kend_opra.setVisibility(View.VISIBLE);
        }
        if (!initCheckItemsLayars("radar")){
            switch_radar.setVisibility(View.GONE);
            chipGroup.removeView(switch_radar);

        }else {
            switch_radar.setVisibility(View.VISIBLE);
        }
        if (!initCheckItemsLayars("rough")){
            switch_roughnes_index.setVisibility(View.GONE);
            chip_jalan.removeView(switch_roughnes_index);

        }else {
            switch_roughnes_index.setVisibility(View.VISIBLE);
        }
        if (!initCheckItemsLayars("gate")){
            switch_gerbang_tol.setVisibility(View.GONE);
            chip_jalan.removeView(switch_gerbang_tol);

        }else {
            switch_gerbang_tol.setVisibility(View.VISIBLE);
        }

    }
    private void initSetChecked(Chip switch_jalan_toll, Chip switch_kondisi_traffic, Chip switch_cctv, Chip switch_vms,
                                ImageView img_pemeliharaan, ImageView img_gangguan, ImageView img_rekayasa, TextView txt_pemeliharaan, TextView txt_gangguan,TextView txt_rekayasa,
                                Chip switch_batas_km, Chip switch_jalan_penghubung, Chip switch_gerbang_tol,
                                Chip switch_rest_Area, Chip switch_roughnes_index, Chip switch_rtms, Chip switch_rtms2,
                                Chip switch_speed, Chip switch_water_level, Chip switch_pompa_banjir,
                                Chip switch_wim_bridge, Chip switch_gps_kend_opra, Chip switch_sepeda_montor, Chip switch_radar)
    {
        switch_jalan_toll.setChecked(userSetting.getJalanToll().equals(UserSetting.onSet));
        switch_kondisi_traffic.setChecked(userSetting.getKondisiTraffic().equals(UserSetting.onSet));
        if (initCheckItemsLayars("cctv")){
            switch_cctv.setVisibility(View.GONE);
            switch_cctv.setChecked(userSetting.getCctv().equals(UserSetting.onSet));
        }
        if (initCheckItemsLayars("vms")){
            switch_vms.setVisibility(View.GONE);

            switch_vms.setChecked(userSetting.getVms().equals(UserSetting.onSet));
        }
        img_pemeliharaan.setSelected(userSetting.getPemeliharaan().equals(UserSetting.onSet));
        txt_pemeliharaan.setSelected(userSetting.getPemeliharaan().equals(UserSetting.onSet));
        img_gangguan.setSelected(userSetting.getGangguanLalin().equals(UserSetting.onSet));
        txt_gangguan.setSelected(userSetting.getGangguanLalin().equals(UserSetting.onSet));
        img_rekayasa.setSelected(userSetting.getRekayasaLalin().equals(UserSetting.onSet));
        txt_rekayasa.setSelected(userSetting.getRekayasaLalin().equals(UserSetting.onSet));

        int colorGangguan = img_gangguan.isSelected() ? getResources().getColor(R.color.birujid) : getResources().getColor(R.color.gray3);
        int colorPemeliharaan = img_pemeliharaan.isSelected() ? getResources().getColor(R.color.birujid) : getResources().getColor(R.color.gray3);
        int colorRekayasa = img_rekayasa.isSelected() ? getResources().getColor(R.color.birujid) : getResources().getColor(R.color.gray3);
        txt_gangguan.setTextColor(colorGangguan);
        img_gangguan.setColorFilter(colorGangguan);

        img_pemeliharaan.setColorFilter(colorPemeliharaan);
        txt_pemeliharaan.setTextColor(colorPemeliharaan);

        img_rekayasa.setColorFilter(colorRekayasa);
        txt_rekayasa.setTextColor(colorRekayasa);

        switch_batas_km.setChecked(userSetting.getBataskm().equals(UserSetting.onSet));
        switch_jalan_penghubung.setChecked(userSetting.getJalanpenghubung().equals(UserSetting.onSet));
        switch_gerbang_tol.setChecked(userSetting.getGerbangtol().equals(UserSetting.onSet));
        switch_rest_Area.setChecked(userSetting.getRestarea().equals(UserSetting.onSet));
        switch_roughnes_index.setChecked(userSetting.getRougnesindex().equals(UserSetting.onSet));

        if (initCheckItemsLayars("rtms")){
            switch_rtms.setChecked(userSetting.getRtms().equals(UserSetting.onSet));
        }
        if (initCheckItemsLayars("rtms2")){
            switch_rtms2.setChecked(userSetting.getRtms2().equals(UserSetting.onSet));
        }
        if (initCheckItemsLayars("speed")){
            switch_speed.setChecked(userSetting.getSpeed().equals(UserSetting.onSet));
        }
        if (initCheckItemsLayars("level")){
            switch_water_level.setChecked(userSetting.getWaterlevel().equals(UserSetting.onSet));
        }
        if (initCheckItemsLayars("pump")){
            switch_pompa_banjir.setChecked(userSetting.getPompa().equals(UserSetting.onSet));
        }
        if (initCheckItemsLayars("wim")){
            switch_wim_bridge.setChecked(userSetting.getWim().equals(UserSetting.onSet));
        }
        if (initCheckItemsLayars("bike")){
            switch_sepeda_montor.setChecked(userSetting.getBike().equals(UserSetting.onSet));
        }
        if (initCheckItemsLayars("cars")){
            switch_gps_kend_opra.setChecked(userSetting.getGpskend().equals(UserSetting.onSet));
        }
        if (initCheckItemsLayars("radar")){
            switch_radar.setChecked(userSetting.getRadar().equals(UserSetting.onSet));
        }

        if (switch_jalan_toll.isChecked()){
            switch_jalan_toll.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_jalan_toll.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_jalan_toll.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_jalan_toll.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_jalan_toll.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_jalan_toll.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_kondisi_traffic.isChecked()){
            switch_kondisi_traffic.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_kondisi_traffic.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_kondisi_traffic.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_kondisi_traffic.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_kondisi_traffic.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_kondisi_traffic.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_cctv.isChecked()){
            switch_cctv.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_cctv.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_cctv.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_cctv.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_cctv.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_cctv.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_vms.isChecked()){
            switch_vms.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_vms.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_vms.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_vms.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_vms.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_vms.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
//        if (switch_pemeliharaan.isChecked()){
//            switch_pemeliharaan.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
//            switch_pemeliharaan.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
//            switch_pemeliharaan.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
//        }else{
//            switch_pemeliharaan.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
//            switch_pemeliharaan.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//            switch_pemeliharaan.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//        }
//        if (switch_gangguan_lalin.isChecked()){
//            switch_gangguan_lalin.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
//            switch_gangguan_lalin.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
//            switch_gangguan_lalin.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
//        }else{
//            switch_gangguan_lalin.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
//            switch_gangguan_lalin.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//            switch_gangguan_lalin.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//        }
//        if (switch_rekayasalalin.isChecked()){
//            switch_rekayasalalin.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
//            switch_rekayasalalin.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
//            switch_rekayasalalin.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
//        }else{
//            switch_rekayasalalin.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
//            switch_rekayasalalin.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//            switch_rekayasalalin.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
//        }
        if (switch_batas_km.isChecked()){
            switch_batas_km.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_batas_km.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_batas_km.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_batas_km.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_batas_km.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_batas_km.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_jalan_penghubung.isChecked()){
            switch_jalan_penghubung.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_jalan_penghubung.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_jalan_penghubung.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_jalan_penghubung.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_jalan_penghubung.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_jalan_penghubung.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_gerbang_tol.isChecked()){
            switch_gerbang_tol.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_gerbang_tol.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_gerbang_tol.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_gerbang_tol.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_gerbang_tol.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_gerbang_tol.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_rest_Area.isChecked()){
            switch_rest_Area.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_rest_Area.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_rest_Area.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_rest_Area.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_rest_Area.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_rest_Area.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_roughnes_index.isChecked()){
            switch_roughnes_index.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_roughnes_index.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_roughnes_index.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_roughnes_index.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_roughnes_index.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_roughnes_index.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_rtms.isChecked()){
            switch_rtms.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_rtms.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_rtms.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_rtms.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_rtms.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_rtms.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_rtms2.isChecked()){
            switch_rtms2.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_rtms2.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_rtms2.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_rtms2.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_rtms2.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_rtms2.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_speed.isChecked()){
            switch_speed.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_speed.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_speed.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_speed.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_speed.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_speed.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_water_level.isChecked()){
            switch_water_level.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_water_level.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_water_level.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_water_level.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_water_level.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_water_level.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_pompa_banjir.isChecked()){
            switch_pompa_banjir.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_pompa_banjir.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_pompa_banjir.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_pompa_banjir.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_pompa_banjir.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_pompa_banjir.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_wim_bridge.isChecked()){
            switch_wim_bridge.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_wim_bridge.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_wim_bridge.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_wim_bridge.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_wim_bridge.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_wim_bridge.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_sepeda_montor.isChecked()){
            switch_sepeda_montor.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_sepeda_montor.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_sepeda_montor.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_sepeda_montor.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_sepeda_montor.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_sepeda_montor.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_gps_kend_opra.isChecked()){
            switch_gps_kend_opra.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_gps_kend_opra.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_gps_kend_opra.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_gps_kend_opra.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_gps_kend_opra.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_gps_kend_opra.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
        if (switch_radar.isChecked()){
            switch_radar.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.biruRa)));
            switch_radar.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            switch_radar.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
        }else{
            switch_radar.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            switch_radar.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
            switch_radar.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.chip_selected)));
        }
    }

    private void initKondisiAktif(BottomSheetDialog sheetDialog, Chip switch_jalan_toll, Chip switch_kondisi_traffic, Chip switch_cctv,
                                  Chip switch_vms,ImageView img_pemeliharaan, ImageView img_gangguan, ImageView img_rekayasa, TextView txt_pemeliharaan, TextView txt_gangguan,TextView txt_rekayasa, Chip switch_batas_km, Chip switch_jalan_penghubung, Chip switch_gerbang_tol,
                                  Chip switch_rest_Area, Chip switch_roughnes_index, Chip switch_rtms, Chip switch_rtms2,
                                  Chip switch_speed, Chip switch_water_level, Chip switch_pompa_banjir,
                                  Chip switch_wim_bridge, Chip switch_gps_kend_opra, Chip switch_sepeda_montor, Chip switch_radar)
    {
        SharedPreferences.Editor editor = getSharedPreferences(UserSetting.PREFERENCES, MODE_PRIVATE).edit();

        LineLayer linetol = styleSet.getLayerAs("finaltoll");
        LineLayer linelalin = styleSet.getLayerAs("finallalin");
        SymbolLayer symbolcctv = styleSet.getLayerAs("finalcctv");
        SymbolLayer symbolvms = styleSet.getLayerAs("finalvms");
        SymbolLayer symbolpemeliharaan = styleSet.getLayerAs("finalpemeliharaan");
        SymbolLayer symbolgangguan = styleSet.getLayerAs("finalgangguan");
        SymbolLayer symbolrekayasalain = styleSet.getLayerAs("finalrekayasalalin");
        LineLayer linerekayasalalin = styleSet.getLayerAs("finalrekayasalalinline");
        SymbolLayer symbollinerekayasalain = styleSet.getLayerAs("finalarrowline");
        SymbolLayer symbolbataskm = styleSet.getLayerAs("finalbataskm");
        LineLayer lineramp = styleSet.getLayerAs("finalramp");
        SymbolLayer symbolgerbangtolm = styleSet.getLayerAs("finalgerbangtol");
        SymbolLayer symbolgerbangsistolm = styleSet.getLayerAs("finalgerbangsisputol");
        SymbolLayer symbolrestarea = styleSet.getLayerAs("finalrestarea");
        LineLayer linerougnesindex = styleSet.getLayerAs("finalrougnesindex");
        SymbolLayer symbolrtms = styleSet.getLayerAs("finalrtms");
        SymbolLayer symbolrtms2 = styleSet.getLayerAs("finalrtms2");
        SymbolLayer symbolspeed = styleSet.getLayerAs("finalspeed");
        SymbolLayer symbolwater = styleSet.getLayerAs("finallevel");
        SymbolLayer symbolpompa = styleSet.getLayerAs("finalpompa");
        SymbolLayer symbolwim = styleSet.getLayerAs("finalwim");
        SymbolLayer symbolbike = styleSet.getLayerAs("finalbike");
        SymbolLayer symbolgps = styleSet.getLayerAs("finalgpskendaraan");
        SymbolLayer symbolradar = styleSet.getLayerAs("finalradar");

        if (switch_jalan_toll.isChecked()){
            if (linetol != null) {
                linetol.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }
            userSetting.setJalanToll(UserSetting.onSet);
            editor.putString(UserSetting.JALANTOLSET, userSetting.getJalanToll());
            editor.apply();
        }else{
            if (linetol != null) {
                linetol.setProperties(PropertyFactory.visibility(Property.NONE));
            }
            userSetting.setJalanToll(UserSetting.offSet);
            editor.putString(UserSetting.JALANTOLSET, userSetting.getJalanToll());
            editor.apply();
        }
        //LALIN
        if (switch_kondisi_traffic.isChecked()){
            if (linelalin != null) {
                linelalin.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }
            userSetting.setKondisiTraffic(UserSetting.onSet);
            editor.putString(UserSetting.KONDISITRAFFICSET, userSetting.getKondisiTraffic());
            editor.apply();
        }else{
            if (linelalin != null) {
                linelalin.setProperties(PropertyFactory.visibility(Property.NONE));
            }
            userSetting.setKondisiTraffic(UserSetting.offSet);
            editor.putString(UserSetting.KONDISITRAFFICSET, userSetting.getKondisiTraffic());
            editor.apply();
        }
        //CCTV
        if (initCheckItemsLayars("cctv")){
            if (switch_cctv.isChecked()){
                if (symbolcctv != null) {
                    symbolcctv.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }else{
                    initLoadCCTV(styleSet, mapboxMapSet);
                }
                userSetting.setCctv(UserSetting.onSet);
                editor.putString(UserSetting.CCTVSET, userSetting.getCctv());
                editor.apply();
            }else{
                if (symbolcctv != null) {
                    symbolcctv.setProperties(PropertyFactory.visibility(Property.NONE));
                }
                userSetting.setCctv(UserSetting.offSet);
                editor.putString(UserSetting.CCTVSET, userSetting.getCctv());
                editor.apply();
            }
        }

        //VMS
        if (initCheckItemsLayars("vms")) {
            if (switch_vms.isChecked()) {
                if (symbolvms != null) {
                    symbolvms.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                } else {
                    initLoadVMS(styleSet, mapboxMapSet);
                }
                userSetting.setVms(UserSetting.onSet);
                editor.putString(UserSetting.VMSSET, userSetting.getVms());
                editor.apply();
            } else {
                if (symbolvms != null) {
                    symbolvms.setProperties(PropertyFactory.visibility(Property.NONE));
                }
                userSetting.setVms(UserSetting.offSet);
                editor.putString(UserSetting.VMSSET, userSetting.getVms());
                editor.apply();
            }
        }
        //oemeliharaan
        if (img_pemeliharaan.isSelected()){
            if (symbolpemeliharaan != null) {
                symbolpemeliharaan.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }else{
                Pemeliharaan(styleSet, mapboxMapSet);
            }
            userSetting.setPemeliharaan(UserSetting.onSet);
            editor.putString(UserSetting.PEMELIHARAANSET, userSetting.getPemeliharaan());
            editor.apply();
        }else{
            if (symbolpemeliharaan != null) {
                symbolpemeliharaan.setProperties(PropertyFactory.visibility(Property.NONE));
            }
            userSetting.setPemeliharaan(UserSetting.offSet);
            editor.putString(UserSetting.PEMELIHARAANSET, userSetting.getPemeliharaan());
            editor.apply();
        }
        //Gangguan
        if (img_gangguan.isSelected()){
            if (symbolgangguan != null) {
                symbolgangguan.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }else{
                GangguanLalin(styleSet, mapboxMapSet);
            }
            userSetting.setGangguanLalin(UserSetting.onSet);
            editor.putString(UserSetting.GANGGUANLALINSET, userSetting.getGangguanLalin());
            editor.apply();
        }else{
            if (symbolgangguan != null) {
                symbolgangguan.setProperties(PropertyFactory.visibility(Property.NONE));
            }
            userSetting.setGangguanLalin(UserSetting.offSet);
            editor.putString(UserSetting.GANGGUANLALINSET, userSetting.getGangguanLalin());
            editor.apply();
        }
        if (img_rekayasa.isSelected()){
            if (symbolrekayasalain != null){
                symbolrekayasalain.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                linerekayasalalin.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                symbollinerekayasalain.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }else{
                RekaysaLalin(styleSet, mapboxMapSet);
            }
            userSetting.setRekayasaLalin(UserSetting.onSet);
            editor.putString(UserSetting.REKAYASALALINSET, userSetting.getRekayasaLalin());
            editor.apply();
        }else{
            if (symbolrekayasalain != null) {
                symbolrekayasalain.setProperties(PropertyFactory.visibility(Property.NONE));
                linerekayasalalin.setProperties(PropertyFactory.visibility(Property.NONE));
                symbollinerekayasalain.setProperties(PropertyFactory.visibility(Property.NONE));
            }
            userSetting.setRekayasaLalin(UserSetting.offSet);
            editor.putString(UserSetting.REKAYASALALINSET, userSetting.getRekayasaLalin());
            editor.apply();
        }

        if (switch_batas_km.isChecked()){
            if (symbolbataskm != null){
                symbolbataskm.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }else {
                BatasKM(styleSet, mapboxMapSet);
            }
            userSetting.setBataskm(UserSetting.onSet);
            editor.putString(UserSetting.BATASKMSET, userSetting.getBataskm());
            editor.apply();
        }else{
            if (symbolbataskm != null) {
                symbolbataskm.setProperties(PropertyFactory.visibility(Property.NONE));
            }
            userSetting.setBataskm(UserSetting.offSet);
            editor.putString(UserSetting.BATASKMSET, userSetting.getBataskm());
            editor.apply();
        }

        if (switch_jalan_penghubung.isChecked()){
            if (lineramp != null){
                lineramp.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }else{
                JalanPenghubung(styleSet, mapboxMapSet);
            }
            userSetting.setJalanpenghubung(UserSetting.onSet);
            editor.putString(UserSetting.JALANPENGHUBUNGSET, userSetting.getJalanpenghubung());
            editor.apply();
        }else{
            if (lineramp != null) {
                lineramp.setProperties(PropertyFactory.visibility(Property.NONE));
            }
            userSetting.setJalanpenghubung(UserSetting.offSet);
            editor.putString(UserSetting.JALANPENGHUBUNGSET, userSetting.getJalanpenghubung());
            editor.apply();
        }

        if (switch_gerbang_tol.isChecked()){
            if (symbolgerbangtolm != null || symbolgerbangsistolm != null){
                symbolgerbangsistolm.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                symbolgerbangtolm.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }else{
                GerbangTol(styleSet, mapboxMapSet);
            }
            userSetting.setGerbangtol(UserSetting.onSet);
            userSetting.setGerbangsistol(UserSetting.onSet);
            editor.putString(UserSetting.GERBANGSISTOLSET,userSetting.getGerbangsistol());
            editor.putString(UserSetting.GERBANGTOLSET, userSetting.getGerbangtol());
            editor.apply();
        }else{

            if (symbolgerbangtolm != null) {
                symbolgerbangtolm.setProperties(PropertyFactory.visibility(Property.NONE));
            } else {
                Log.d("LayerCheck", "symbolgerbangtolm is null");
            }

            if (symbolgerbangsistolm != null) {
                symbolgerbangsistolm.setProperties(PropertyFactory.visibility(Property.NONE));
            } else {
                Log.d("LayerCheck", "symbolgerbangsistolm is null");
            }
            userSetting.setGerbangtol(UserSetting.offSet);
            userSetting.setGerbangsistol(UserSetting.offSet);
            editor.putString(UserSetting.GERBANGSISTOLSET, userSetting.getGerbangsistol());
            editor.putString(UserSetting.GERBANGTOLSET, userSetting.getGerbangtol());
            editor.apply();
        }

        if (switch_rest_Area.isChecked()){
            if (symbolrestarea != null){
                symbolrestarea.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }else{
                RestArea(styleSet, mapboxMapSet);
            }
            userSetting.setRestarea(UserSetting.onSet);
            editor.putString(UserSetting.RESTAREASET, userSetting.getRestarea());
            editor.apply();
        }else{
            if (symbolrestarea != null) {
                symbolrestarea.setProperties(PropertyFactory.visibility(Property.NONE));
            }
            userSetting.setRestarea(UserSetting.offSet);
            editor.putString(UserSetting.RESTAREASET, userSetting.getRestarea());
            editor.apply();
        }

        if (switch_roughnes_index.isChecked()){
            if (linerougnesindex != null){
                linerougnesindex.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }else{
                RoughnesIndex(styleSet, mapboxMapSet);
            }
            userSetting.setRougnesindex(UserSetting.onSet);
            editor.putString(UserSetting.ROUDNESINDEXSET, userSetting.getRougnesindex());
            editor.apply();
        }else{
            if (linerougnesindex != null) {
                linerougnesindex.setProperties(PropertyFactory.visibility(Property.NONE));
            }
            userSetting.setRougnesindex(UserSetting.offSet);
            editor.putString(UserSetting.ROUDNESINDEXSET, userSetting.getRougnesindex());
            editor.apply();
        }

        //rtms
        if (initCheckItemsLayars("rtms")) {
            if (switch_rtms.isChecked()){
                if (symbolrtms != null){
                    symbolrtms.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }else{
                    RTMS(styleSet, mapboxMapSet);
                }
                userSetting.setRtms(UserSetting.onSet);
                editor.putString(UserSetting.RTMSSET, userSetting.getRtms());
                editor.apply();
            }else{
                if (symbolrtms != null) {
                    symbolrtms.setProperties(PropertyFactory.visibility(Property.NONE));
                }
                userSetting.setRtms(UserSetting.offSet);
                editor.putString(UserSetting.RTMSSET, userSetting.getRtms());
                editor.apply();
            }
        }
        if (initCheckItemsLayars("rtms2")){
            if (switch_rtms2.isChecked()){
                if (symbolrtms2 != null){
                    symbolrtms2.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }else{
                    RTMS2(styleSet, mapboxMapSet);
                }
                userSetting.setRtms2(UserSetting.onSet);
                editor.putString(UserSetting.RTMSSET2, userSetting.getRtms2());
                editor.apply();
            }else{
                if (symbolrtms2 != null) {
                    symbolrtms2.setProperties(PropertyFactory.visibility(Property.NONE));
                }
                userSetting.setRtms2(UserSetting.offSet);
                editor.putString(UserSetting.RTMSSET2, userSetting.getRtms2());
                editor.apply();
            }
        }
        if (initCheckItemsLayars("speed")){
            if (switch_speed.isChecked()){
                if (symbolspeed != null){
                    symbolspeed.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }else{
                    SpeedCounting(styleSet, mapboxMapSet);
                }
                userSetting.setSpeed(UserSetting.onSet);
                editor.putString(UserSetting.SPEDDSET, userSetting.getSpeed());
                editor.apply();
            }else{
                if (symbolspeed != null) {
                    symbolspeed.setProperties(PropertyFactory.visibility(Property.NONE));
                }
                userSetting.setSpeed(UserSetting.offSet);
                editor.putString(UserSetting.SPEDDSET, userSetting.getSpeed());
                editor.apply();
            }
        }
        if (initCheckItemsLayars("level")){
            if (switch_water_level.isChecked()){
                if (symbolwater != null){
                    symbolwater.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }else{
                    WaterLevelSensor(styleSet, mapboxMapSet);
                }
                userSetting.setWaterlevel(UserSetting.onSet);
                editor.putString(UserSetting.WATER, userSetting.getWaterlevel());
                editor.apply();
            }else{
                if (symbolwater != null) {
                    symbolwater.setProperties(PropertyFactory.visibility(Property.NONE));
                }
                userSetting.setWaterlevel(UserSetting.offSet);
                editor.putString(UserSetting.WATER, userSetting.getWaterlevel());
                editor.apply();
            }
        }
        if (initCheckItemsLayars("pump")){
            if (switch_pompa_banjir.isChecked()){
                if (symbolpompa != null){
                    symbolpompa.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }else{
                    PompaBajir(styleSet, mapboxMapSet);
                }
                userSetting.setPompa(UserSetting.onSet);
                editor.putString(UserSetting.POMPA, userSetting.getPompa());
                editor.apply();
            }else{
                if (symbolpompa != null) {
                    symbolpompa.setProperties(PropertyFactory.visibility(Property.NONE));
                }
                userSetting.setPompa(UserSetting.offSet);
                editor.putString(UserSetting.POMPA, userSetting.getPompa());
                editor.apply();
            }
        }
        if (initCheckItemsLayars("wim")){
            if (switch_wim_bridge.isChecked()){
                if (symbolwim != null){
                    symbolwim.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }else{
                    WIMData(styleSet, mapboxMapSet);
                }
                userSetting.setWim(UserSetting.onSet);
                editor.putString(UserSetting.WIM, userSetting.getWim());
                editor.apply();
            }else{
                if (symbolwim != null) {
                    symbolwim.setProperties(PropertyFactory.visibility(Property.NONE));
                }
                userSetting.setWim(UserSetting.offSet);
                editor.putString(UserSetting.WIM, userSetting.getWim());
                editor.apply();
            }
        }
        if (initCheckItemsLayars("bike")){
            if (switch_sepeda_montor.isChecked()){
                if (symbolbike != null){
                    symbolbike.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }else{
                    Bike(styleSet, mapboxMapSet);
                }
                userSetting.setBike(UserSetting.onSet);
                editor.putString(UserSetting.BIKE, userSetting.getBike());
                editor.apply();
            }else{
                if (symbolbike != null) {
                    symbolbike.setProperties(PropertyFactory.visibility(Property.NONE));
                }
                userSetting.setBike(UserSetting.offSet);
                editor.putString(UserSetting.BIKE, userSetting.getBike());
                editor.apply();
            }
        }
        if (initCheckItemsLayars("cars")){
            if (switch_gps_kend_opra.isChecked()){
                if (symbolgps != null){
                    symbolgps.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }else{
                    GpsKendaraanOprasinal(styleSet, mapboxMapSet);
                }
                userSetting.setGpskend(UserSetting.onSet);
                editor.putString(UserSetting.GPSKEND, userSetting.getGpskend());
                editor.apply();
            }else{
                if (symbolgps != null) {
                    symbolgps.setProperties(PropertyFactory.visibility(Property.NONE));
                }
                userSetting.setGpskend(UserSetting.offSet);
                editor.putString(UserSetting.GPSKEND, userSetting.getGpskend());
                editor.apply();
            }
        }
        if (initCheckItemsLayars("radar")){
            if (switch_radar.isChecked()){
                if (symbolradar != null){
                    symbolradar.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }else{
                    Radar(styleSet, mapboxMapSet);
                }
                userSetting.setRadar(UserSetting.onSet);
                editor.putString(UserSetting.RADAR, userSetting.getRadar());
                editor.apply();
            }else{
                if (symbolradar != null) {
                    symbolradar.setProperties(PropertyFactory.visibility(Property.NONE));
                }
                userSetting.setRadar(UserSetting.offSet);
                editor.putString(UserSetting.RADAR, userSetting.getRadar());
                editor.apply();
            }
        }

        sheetDialog.dismiss();
    }

    private void initMaps() {
        loadingDialog = new LoadingDialog(Maps.this);
        loadingDialog.showLoadingDialog("Memuat Data...");
        mapView.onStop();
        mapView.getMapAsync(mapboxMap -> {
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(-2.9638907, 109.24058))
                    .zoom(3.5)
                    .tilt(1.0)
                    .bearing(0)
                    .build()),
            500);
            mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    UiSettings uiSettings = mapboxMap.getUiSettings();
                    uiSettings.setCompassEnabled(false);
                    uiSettings.setRotateGesturesEnabled(false);
                    uiSettings.setLogoEnabled(false);
                    uiSettings.setAttributionEnabled(false);

                    styleSet = style;
                    mapboxMapSet = mapboxMap;
                    ServiceFunction.iconImage(style, Maps.this);

                    try {
                        initLineToll(style);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                    RekaysaLalin(style, mapboxMap);

                    loadingDialog.hideLoadingDialog();
                }
            });
        });
    }

    private void initLalinLocal(Style style, MapboxMap mapboxMap)   {
            if (ServiceFunction.getFileAssets("lalin.json", this) != null) {
                FeatureCollection featureCollectiontoll = FeatureCollection.fromJson(ServiceFunction.getFileAssets("lalin.json", this));
                style.addSource(new GeoJsonSource("lalin", featureCollectiontoll.toJson()));

                LineLayer linelalins = new LineLayer("finallalin", "lalin").withProperties(
                        PropertyFactory.lineColor(
                                match(get("color"), rgb(0, 0, 0),
                                        stop("#ffcc00", "#ffcc00"),
                                        stop("#ff0000", "#ff0000"),
                                        stop("#bb0000", "#bb0000"),
                                        stop("#440000", "#440000"),
                                        stop("#00ff00", "#00ff00")
                                )),
                        PropertyFactory.lineWidth(2f)
                );
                style.addLayerAbove(linelalins,"finaltoll");
            }

            serviceRealtime.handleRunServiceLalin(style, mapboxMap, "finallalin", "lalin");

            mapboxMap.addOnMapClickListener(pointlalin -> {
                aktif_popup_lalin = "true";
//                ShowAlert.showDialogRealtime(this,mapboxMap,pointlalin,alertDialogLineToll);
                return false;
            });

            MidasData(style, mapboxMap);

            LineLayer linelalin = style.getLayerAs("finallalin");
            if(userSetting.getKondisiTraffic().equals(UserSetting.onSet)){
                if (linelalin != null) {
                    linelalin.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }
            }else{
                if (linelalin != null) {
                    linelalin.setProperties(PropertyFactory.visibility(Property.NONE));
                }
            }

            if (userSetting.getBataskm().equals(UserSetting.onSet)){
                BatasKM(style, mapboxMap);
            }

            if (userSetting.getRtms().equals(UserSetting.onSet)){
                RTMS(style, mapboxMap);
            }
            if (userSetting.getRtms2().equals(UserSetting.onSet)){
                RTMS2(style, mapboxMap);
            }
            if (userSetting.getCctv().equals(UserSetting.onSet)){
                initLoadCCTV(style, mapboxMap);
            }
            if (userSetting.getVms().equals(UserSetting.onSet)){
                initLoadVMS(style, mapboxMap);
            }

            if (userSetting.getJalanpenghubung().equals(UserSetting.onSet)){
                JalanPenghubung(style,mapboxMap);
            }
            if (userSetting.getGerbangtol().equals(UserSetting.onSet)){
                GerbangTol(style, mapboxMap);
            }
            if (userSetting.getRestarea().equals(UserSetting.onSet)){
                RestArea(style, mapboxMap);
            }
            if (userSetting.getRougnesindex().equals(UserSetting.onSet)){
                RoughnesIndex(style, mapboxMap);
            }

            if (userSetting.getSpeed().equals(UserSetting.onSet)){
                SpeedCounting(style, mapboxMap);
            }

            if (userSetting.getWaterlevel().equals(UserSetting.onSet)){
                WaterLevelSensor(style, mapboxMap);
            }
            if (userSetting.getPompa().equals(UserSetting.onSet)){
                PompaBajir(style, mapboxMap);
            }
            if (userSetting.getWim().equals(UserSetting.onSet)){
                WIMData(style, mapboxMap);
            }
            if (userSetting.getBike().equals(UserSetting.onSet)){
                Bike(style, mapboxMap);
            }
            if (userSetting.getGpskend().equals(UserSetting.onSet)){
                GpsKendaraanOprasinal(style, mapboxMap);
            }
            if (userSetting.getRadar().equals(UserSetting.onSet)){
                Radar(style, mapboxMap);
            }

    }

    private void initLineToll(Style style) throws URISyntaxException {
        if (ServiceFunction.getFileAssets("toll.json", this) != null){
            FeatureCollection featureCollectiontoll = FeatureCollection.fromJson(ServiceFunction.getFileAssets("toll.json", this));
            style.addSource(new GeoJsonSource("toll", featureCollectiontoll.toJson()));
            style.addLayer(new LineLayer("finaltoll", "toll").withProperties(
                    PropertyFactory.lineColor(Color.GRAY),
                    PropertyFactory.lineWidth(3f)
            ));

            LineLayer linetol = style.getLayerAs("finaltoll");
            if (userSetting.getJalanToll() != null && userSetting.getJalanToll().equals(UserSetting.onSet)) {
                if (linetol != null) {
                    linetol.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                }
            }else{
                if (linetol != null) {
                    linetol.setProperties(PropertyFactory.visibility(Property.NONE));
                }
            }
        }
    }

    private void initLoadCCTV(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);
        paramsIdruas.addProperty("limit", 9999);
        Log.d("CCTV", "initLoadCCTV: " + scope);
        serviceAPI = ApiClient.getClient(this);
        loadingDialog.showLoadingDialog("Memuat cctv.....");
        loadingDialog.hideLoadingDialog();
        Call<JsonObject> call = serviceAPI.excutegetcctv(paramsIdruas,token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    Log.d(TAG, "onResponse: DATA CCTV" + dataRes.toString());
                    if (dataRes.getString("status").equals("1")){
                        JSONObject dataObj = new JSONObject(dataRes.toString());
                        try {
                            FeatureCollection featureCollectioncctv = FeatureCollection.fromJson(dataRes.getString("data"));
                            style.addSource(new GeoJsonSource("cctv", featureCollectioncctv.toJson()));
                            style.addLayer(new SymbolLayer("finalcctv", "cctv").withProperties(
                                    PropertyFactory.iconImage(get("poi")),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("nama")),
                                    PropertyFactory.textRadialOffset(1.8f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f)
                            ));
                            loadingDialog.hideLoadingDialog();
                        }catch (IOError err){
                            Log.d("Err CCTV", err.toString());
                            loadingDialog.hideLoadingDialog();
                        }
                        SymbolLayer symbolcctv = style.getLayerAs("finalcctv");
                        if (userSetting.getCctv().equals(UserSetting.onSet)){
                            if (symbolcctv != null) {
                                symbolcctv.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                            }
                        }else{
                            if (symbolcctv != null) {
                                symbolcctv.setProperties(PropertyFactory.visibility(Property.NONE));
                            }
                        }
//                        serviceRealtime.handleRunServiceCCTV(style, mapboxMap, "finalcctv", "cctv", scope);
                        loadingDialog.hideLoadingDialog();

                        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @OptIn(markerClass = UnstableApi.class) @Override
                            public boolean onMapClick(LatLng pointcctv) {
                                PointF screenPointcctv = mapboxMap.getProjection().toScreenLocation(pointcctv);
                                List<Feature> featurescctv = mapboxMap.queryRenderedFeatures(screenPointcctv, "finalcctv");
                                if (!featurescctv.isEmpty()) {
                                    Feature selectedFeaturecctv = featurescctv.get(0);
                                    String cabang = selectedFeaturecctv.getStringProperty("nama_ruas");
                                    String jnscctv = selectedFeaturecctv.getStringProperty("jns_cctv");
                                    String camera_id = selectedFeaturecctv.getStringProperty("camera_id");
                                    String nama = selectedFeaturecctv.getStringProperty("nama");
                                    String status = selectedFeaturecctv.getStringProperty("status");
                                    key_id = selectedFeaturecctv.getStringProperty("key_id");
                                    id_ruas = selectedFeaturecctv.getStringProperty("id_ruas");
                                    boolean hls = selectedFeaturecctv.getBooleanProperty("is_hls");
                                    AlertDialog.Builder alert = new AlertDialog.Builder(Maps.this);
                                    alert.setCancelable(false);

                                    LayoutInflater inflater = getLayoutInflater();
                                    View dialoglayout = inflater.inflate(R.layout.dialog_maps_cctv, null);
                                    alert.setView(dialoglayout);

                                    handler_cctv = new Handler();

                                    img= dialoglayout.findViewById(R.id.showImg);
                                    loadingIMG= dialoglayout.findViewById(R.id.loadingIMG);
                                    playerView = dialoglayout.findViewById(R.id.cctv_hls);
//                                    TextView jns_cctv = dialoglayout.findViewById(R.id.jns_cctv);
//                                    TextView txt_cabang = dialoglayout.findViewById(R.id.txt_cabang);
                                    TextView nmKm = dialoglayout.findViewById(R.id.txt_nmKm);
                                    TextView txt_nama = dialoglayout.findViewById(R.id.nm_lokasi);
                                    set_cctv_off = dialoglayout.findViewById(R.id.set_cctv_off);
                                    TextView btn_close = dialoglayout.findViewById(R.id.btn_close);

//                                    jns_cctv.setText(jnscctv);
                                    txt_nama.setText(cabang);
                                    nmKm.setText(nama);

                                    final AlertDialog  alertDialog = alert.create();
                                    ArrayList<String> imagesList= new ArrayList<String>();
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    btn_close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            handler_cctv.removeCallbacksAndMessages(null);
//                                            serviceRealtime.removeCallbacksHandleCCTVservice();
                                            if (player != null) {
                                                // Pause the player
                                                player.setPlayWhenReady(false);
                                                // Release the player to free resources
                                                player.release();
                                                // Set the player to null to avoid memory leaks
                                                player = null;
                                            }
                                            alertDialog.cancel();
                                        }
                                    });
                                    if (hls){
                                        img.setVisibility(View.GONE);
                                        playerView.setVisibility(View.VISIBLE);
                                        setHLS();
                                    }else {
                                        img.setVisibility(View.VISIBLE);
                                        playerView.setVisibility(View.GONE);
                                        if(status.equals("0")){
                                            set_cctv_off.setVisibility(View.VISIBLE);
                                            loadingIMG.setVisibility(View.GONE);
                                        }else{
                                            imagesList.clear();
                                            set_cctv_off.setVisibility(View.GONE);
                                            handler_cctv.postDelayed(new Runnable(){
                                                public void run(){
                                                    img_url = "https://jid.jasamarga.com/cctv2/"+key_id+"?tx="+Math.random();
//                                                imagesList.add(convertUrlToBase64(img_url));
//                                                if(imagesList.size() == 10){
//                                                    imagesList.clear();
//                                                }
                                                    ServiceFunction.initStreamImg(getApplicationContext(),img_url,key_id, img, loadingIMG);
                                                    handler_cctv.postDelayed(this, 300);
                                                }
                                            }, 300);
                                        }
                                    }

                                    alertDialog.show();
                                    if (alertDialogLineToll != null){
                                        alertDialogLineToll.cancel();
                                    }
                                }
                                loadingDialog.hideLoadingDialog();

                                return false;
                            }
                        });
                    }else{
                        Log.d("Err DB CCTV", response.body().toString());
                        loadingDialog.hideLoadingDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.hideLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data CCTV", call.toString());
                loadingDialog.hideLoadingDialog();
                Toast.makeText(getApplicationContext(),"Gagal memuat CCTV ....",Toast.LENGTH_SHORT).show();
            }
        });

    }
    @OptIn(markerClass = UnstableApi.class) private void setHLS(){
        uri = "https://jmlive.jasamarga.com/hls/"+id_ruas+"/"+key_id+"/"+"index.m3u8";
        Uri videoUri = Uri.parse(uri);
        androidx.media3.datasource.DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
        HlsMediaSource hlsMediaSource =new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
        player = new ExoPlayer.Builder(getApplicationContext()).build();
        playerView.setPlayer(player);
        player.setMediaSource(hlsMediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
        loadingIMG.setVisibility(View.GONE);
        set_cctv_off.setVisibility(View.GONE);
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
            }
            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                Log.d(TAG, "onPlayerError: " + error);

                img.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
                handler_cctv.postDelayed(new Runnable(){
                    public void run(){
                        img_url = "https://jid.jasamarga.com/cctv2/"+key_id+"?tx="+Math.random();
                        ServiceFunction.initStreamImg(getApplicationContext(),img_url,key_id, img, loadingIMG);
                        handler_cctv.postDelayed(this, 300);
                    }
                }, 300);
            }
        });
    }
    private void initLoadVMS(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutegetvms(paramsIdruas,token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollectionvms = FeatureCollection.fromJson(dataRes.getString("data"));
                        String sourceId = "vms";
                        if (style.getSource(sourceId) == null) {
                            try {
                                // Tambahkan sumber jika belum ada
                                style.addSource(new GeoJsonSource(sourceId, featureCollectionvms.toJson()));
                            } catch (CannotAddSourceException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("Mapbox", "Source " + sourceId + " already exists, no need to add it again.");
                        }
                        style.addLayer(new SymbolLayer("finalvms", "vms").withProperties(
                                PropertyFactory.iconImage(get("icon")),
                                PropertyFactory.textAllowOverlap(false),
                                PropertyFactory.textField(get("nama_lokasi")),
                                PropertyFactory.textRadialOffset(1.3f),
                                PropertyFactory.textAnchor(ngisor),
                                PropertyFactory.textJustify(justify),
                                PropertyFactory.textSize(8f)
                        ));
                        SymbolLayer symbolvms = style.getLayerAs("finalvms");
                        if (userSetting.getVms().equals(UserSetting.onSet)){
                            if (symbolvms != null) {
                                symbolvms.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                            }
                        }else{
                            if (symbolvms != null) {
                                symbolvms.setProperties(PropertyFactory.visibility(Property.NONE));
                            }
                        }

//                        serviceRealtime.handleRunServiceVMS(style, mapboxMap, "finalvms", "vms", scope);

                        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @Override
                            public boolean onMapClick(LatLng pointvms) {
                                PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(pointvms);
                                List<Feature> featuresvsms = mapboxMap.queryRenderedFeatures(screenPointvms, "finalvms");
                                if (!featuresvsms.isEmpty()) {
                                    Feature selectedFeaturevms = featuresvsms.get(0);
                                    String kode_lokasi = selectedFeaturevms.getStringProperty("kode_lokasi");
                                    String nama_lokasi = selectedFeaturevms.getStringProperty("nama_lokasi");
                                    String cabang = selectedFeaturevms.getStringProperty("nama_tol");
                                    String status_koneksi = selectedFeaturevms.getStringProperty("status_koneksi");
                                    String waktu_kirim_terakhir = selectedFeaturevms.getStringProperty("waktu_kirim_terakhir");
                                    String pesan_item = selectedFeaturevms.getStringProperty("pesan_item");
                                    String jmlpesan = selectedFeaturevms.getStringProperty("jml_pesan");
                                    Log.d(TAG, "DATA DMS: " + selectedFeaturevms.toJson());

                                    AlertDialog.Builder alert = new AlertDialog.Builder(Maps.this);
                                    alert.setCancelable(false);

                                    LayoutInflater inflater = getLayoutInflater();
                                    View dialoglayout = inflater.inflate(R.layout.custom_vms_dialog, null);
                                    alert.setView(dialoglayout);

                                    ImageView img_pesan = dialoglayout.findViewById(R.id.img_pesan);
                                    ProgressBar loadingIMGvms= dialoglayout.findViewById(R.id.loadingIMGvms);
                                    TextView nm_lokasi = dialoglayout.findViewById(R.id.nm_lokasi);
                                    TextView nm_cabang = dialoglayout.findViewById(R.id.nm_cabang);
                                    TextView tgl_update = dialoglayout.findViewById(R.id.tgl_update);
                                    TextView jml_pesan = dialoglayout.findViewById(R.id.jml_pesan);
                                    Button btn_close = dialoglayout.findViewById(R.id.btn_close);
                                    TextView set_vms_off = dialoglayout.findViewById(R.id.set_vms_off);

                                    nm_lokasi.setText(nama_lokasi);
                                    nm_cabang.setText(cabang);
                                    tgl_update.setText(waktu_kirim_terakhir);
                                    jml_pesan.setText(jmlpesan+" pesan");

                                    JSONArray jsonarray = null;
                                    try {
                                        jsonarray = new JSONArray(pesan_item);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    handler_vms = new Handler();
                                    if(!status_koneksi.equals("TERHUBUNG")){
                                        count = 0;
                                        set_vms_off.setVisibility(View.VISIBLE);
                                        img_pesan.setVisibility(View.GONE);
                                    }else{
                                        img_pesan.setVisibility(View.VISIBLE);
                                        set_vms_off.setVisibility(View.GONE);

                                        count = 0;
                                        JSONArray finalJsonarray = jsonarray;
                                        handler_vms.postDelayed(new Runnable(){
                                            public void run(){
                                                if (count == finalJsonarray.length()){
                                                    count = 0;
                                                }
                                                try {
                                                    JSONObject pesanvms = finalJsonarray.getJSONObject(count);
                                                    img_vms = "https://api-provider-jid.jasamarga.com/client-api/getimg/"+kode_lokasi+"/"+
                                                            pesanvms.getString("NoUrut");
                                                    Log.d("urlvmsimg", img_vms);
                                                    setImageview(img_vms, img_pesan, loadingIMGvms);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                count++;
                                                handler_vms.postDelayed(this, 1500);
                                            }
                                        }, 700);

                                    }

                                    final AlertDialog  alertDialog = alert.create();
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    btn_close.setOnClickListener(v -> {
                                        count = 0;
                                        handler_vms.removeCallbacksAndMessages(null);
//                                        serviceRealtime.removeCallbacksHandleVMSservice();
                                        alertDialog.cancel();

                                    });

                                    alertDialog.show();
                                    if (alertDialogLineToll != null){
                                        alertDialogLineToll.cancel();
                                    }
                                }
                                return false;
                            }
                        });
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

    private void setImageview(String img_vms, ImageView imgPesan, ProgressBar loadingIMG) {
        Glide.with(this)
            .asBitmap()
            .load(img_vms)
            .fitCenter()
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
                                            @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) { imgPesan.setImageBitmap(resource);
                }
            });
    }

    private void GangguanLalin(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutegangguanlalin(paramsIdruas,token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("gangguan", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalgangguan", "gangguan").withProperties(
                                    PropertyFactory.iconImage("gangguan_lalin"),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("ket_tipe_gangguan")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.6f)
                            ));

                            SymbolLayer symbolgangguan = style.getLayerAs("finalgangguan");
                            if (userSetting.getGangguanLalin().equals(UserSetting.onSet)){
                                if (symbolgangguan != null) {
                                    symbolgangguan.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolgangguan != null) {
                                    symbolgangguan.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }

                        });

//                        serviceKondisiLalin.handleRunServiceGangguan(style, mapboxMap, "finalgangguan", "gangguan", scope);
                        mapboxMap.addOnMapClickListener(pointvms -> {
                            ShowAlert.showDialogGangguan(Maps.this,alertDialogLineToll,mapboxMap,pointvms);
                            return false;
                        });
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

    private void Pemeliharaan(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutepemeliharaan(paramsIdruas,token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("pemeliharaan", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalpemeliharaan", "pemeliharaan").withProperties(
                                    PropertyFactory.iconImage("pemeliharaanimg"),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("ket_jenis_kegiatan")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.6f)
                            ));

                            SymbolLayer symbolpemeliharaan = style.getLayerAs("finalpemeliharaan");
                            if (userSetting.getPemeliharaan().equals(UserSetting.onSet)){
                                if (symbolpemeliharaan != null) {
                                    symbolpemeliharaan.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolpemeliharaan != null) {
                                    symbolpemeliharaan.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });



//                        serviceKondisiLalin.handleRunServicePemeliharaan(style, mapboxMap, "finalpemeliharaan", "pemeliharaan", scope);

                        mapboxMap.addOnMapClickListener(pointvms -> {
                            ShowAlert.showDialogPemeliharaan(Maps.this,alertDialogLineToll,mapboxMap,pointvms);
                            return false;
                        });
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

    private void RekaysaLalin(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excuterekayasalalin(paramsIdruas,token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if(response.body() != null) {
                        JSONObject dataRes = new JSONObject(response.body().toString());
                        if (dataRes.getString("status").equals("1")){
                            FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                            mapboxMap.getStyle(style1 -> {
                                style1.addSource(new GeoJsonSource("rekayasalalin", featureCollection.toJson()));
                                SymbolLayer newLayer = new SymbolLayer("finalrekayasalalin", "rekayasalalin").withProperties(
                                        PropertyFactory.iconImage(get("icon")),
                                        PropertyFactory.iconAllowOverlap(false),
                                        PropertyFactory.textAllowOverlap(false),
                                        PropertyFactory.textField(get("ket_jenis_kegiatan")),
                                        PropertyFactory.textRadialOffset(1.5f),
                                        PropertyFactory.textAnchor(ngisor),
                                        PropertyFactory.textJustify(justify),
                                        PropertyFactory.textSize(8f),
                                        PropertyFactory.iconSize(0.7f)
                                );
                                style1.addLayer(newLayer);
//                                style1.addLayerAt(newLayer, 0);
//                                style1.addLayer(new SymbolLayer("finalrekayasalalin", "rekayasalalin").withProperties(
//                                        PropertyFactory.iconImage(get("icon")),
//                                        PropertyFactory.iconAllowOverlap(false),
//                                        PropertyFactory.textAllowOverlap(false),
//                                        PropertyFactory.textField(get("ket_jenis_kegiatan")),
//                                        PropertyFactory.textRadialOffset(1.5f),
//                                        PropertyFactory.textAnchor(ngisor),
//                                        PropertyFactory.textJustify(justify),
//                                        PropertyFactory.textSize(8f),
//                                        PropertyFactory.iconSize(0.7f)
//                                ));

                                FeatureCollection featureCollectionline = null;
                                try {
                                    featureCollectionline = FeatureCollection.fromJson(dataRes.getString("linedata"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                initLineRekLalin(featureCollectionline, style,mapboxMap);
                                SymbolLayer symbolrekayasalain = style1.getLayerAs("finalrekayasalalin");
                                if (userSetting.getRekayasaLalin().equals(UserSetting.onSet)){
                                    if (symbolrekayasalain != null){
                                        symbolrekayasalain.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                    }
                                }else{
                                    if (symbolrekayasalain != null){
                                        symbolrekayasalain.setProperties(PropertyFactory.visibility(Property.NONE));
                                    }
                                }

                                LineLayer linerekayasalalin = style1.getLayerAs("finalrekayasalalinline");
                                if (userSetting.getRekayasaLalin().equals(UserSetting.onSet)){
                                    if (symbolrekayasalain != null){
                                        linerekayasalalin.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                    }
                                }else{
                                    if (symbolrekayasalain != null){
                                        linerekayasalalin.setProperties(PropertyFactory.visibility(Property.NONE));
                                    }
                                }
                            });

//                            serviceKondisiLalin.handleRunServiceRekayasaLalin(style, mapboxMap, "finalrekayasalalin", "rekayasalalin", "finalrekayasalalinline","rekayasalalinline",scope);

//                            mapboxMap.addOnMapClickListener(pointvms -> {
//                                PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(pointvms);
//                                List<Feature> featuresvsms = mapboxMap.queryRenderedFeatures(screenPointvms, "finalrekayasalalin");
//                                if (!featuresvsms.isEmpty()) {
//                                    Feature selectedFeaturevms = featuresvsms.get(0);
//                                    String nama_ruas = selectedFeaturevms.getStringProperty("nama_ruas");
//                                    String km = selectedFeaturevms.getStringProperty("km");
//                                    String jalur = selectedFeaturevms.getStringProperty("jalur");
//                                    String lajur = selectedFeaturevms.getStringProperty("lajur");
//                                    String range_km_pekerjaan = selectedFeaturevms.getStringProperty("range_km_pekerjaan");
//                                    String waktu_awal = selectedFeaturevms.getStringProperty("waktu_awal");
//                                    String waktu_akhir = selectedFeaturevms.getStringProperty("waktu_akhir");
//                                    String ket_jenis_kegiatan = selectedFeaturevms.getStringProperty("ket_jenis_kegiatan");
//                                    String keterangan_detail = selectedFeaturevms.getStringProperty("keterangan_detail");
//                                    String ket_status = selectedFeaturevms.getStringProperty("ket_status");
//
//
//                                    AlertDialog.Builder alert = new AlertDialog.Builder(Maps.this);
//                                    alert.setCancelable(false);
//
//                                    LayoutInflater inflater = getLayoutInflater();
//                                    View dialoglayout = inflater.inflate(R.layout.custom_dialog_pemeliharaan, null);
//                                    alert.setView(dialoglayout);
//
//                                    TextView title_kondisi_lalin = dialoglayout.findViewById(R.id.title_kondisi_lalin);
//                                    TextView txt_nm_ruas = dialoglayout.findViewById(R.id.txt_nm_ruas);
//                                    TextView txt_nmKm = dialoglayout.findViewById(R.id.txt_nmKm);
//                                    TextView txt_jalur_lajur = dialoglayout.findViewById(R.id.txt_jalur_lajur);
//                                    TextView txt_range_km = dialoglayout.findViewById(R.id.txt_eange_km);
//                                    TextView txt_waktu_awal = dialoglayout.findViewById(R.id.txt_waktu_awal);
//                                    TextView txt_waktu_akhir = dialoglayout.findViewById(R.id.txt_waktu_akhir);
//                                    TextView txt_status = dialoglayout.findViewById(R.id.txt_status);
//                                    TextView txt_kegiatan = dialoglayout.findViewById(R.id.txt_jns_kegiatan);
//                                    TextView txt_keternagan = dialoglayout.findViewById(R.id.txt_keterangan);
//
//                                    Button btn_close = dialoglayout.findViewById(R.id.btn_close);
//
//                                    title_kondisi_lalin.setText("Rekayasa Lalin");
//                                    txt_nm_ruas.setText(nama_ruas);
//                                    txt_nmKm.setText(km);
//                                    txt_jalur_lajur.setText(jalur+" / "+lajur);
//                                    txt_range_km.setText(range_km_pekerjaan);
//                                    txt_waktu_awal.setText(waktu_awal);
//                                    txt_waktu_akhir.setText(waktu_akhir);
//                                    txt_status.setText(ket_status);
//                                    txt_kegiatan.setText(ket_jenis_kegiatan);
//                                    txt_keternagan.setText(keterangan_detail);
//
//                                    final AlertDialog  alertDialog = alert.create();
//                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                    btn_close.setOnClickListener(v -> {
//                                        alertDialog.cancel();
////                                        serviceKondisiLalin.removeCallbacksHandleRekayasa();
//                                    });
//
//                                    alertDialog.show();
//                                    if (alertDialogLineToll != null){
//                                        alertDialogLineToll.cancel();
//                                    }
//                                }
//                                return false;
//                            });

                        }else{
                            Log.d("Err DB", response.body().toString());
                        }
                    }else{
                        Log.d("adader", "CODE " + response.code());
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

    private void initLineRekLalin(FeatureCollection featureCollectionline, Style style,MapboxMap mapboxMap) {
        mapboxMap.getStyle(style1 -> {
            style1.addSource(new GeoJsonSource("rekayasalalinline", featureCollectionline.toJson()));
            style1.addLayer(new LineLayer("finalrekayasalalinline", "rekayasalalinline").withProperties(
                    PropertyFactory.lineColor(rgb(34, 167, 255)),
                    PropertyFactory.lineWidth(1.2f)
            ));
            style1.addLayer(new SymbolLayer("finalarrowline", "rekayasalalinline").withProperties(
                    PropertyFactory.iconImage("arrow"),
                    PropertyFactory.iconSize(0.5f),
                    PropertyFactory.symbolPlacement(Property.SYMBOL_PLACEMENT_LINE),
                    PropertyFactory.iconAllowOverlap(false),
                    PropertyFactory.symbolSpacing(5f)
            ));
        });

        initLalinLocal(style, mapboxMap);
        Pemeliharaan(style, mapboxMap);
        GangguanLalin(style, mapboxMap);

    }

    private void BatasKM(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutbataskm(token,paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("bataskm", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalbataskm", "bataskm").withProperties(
                                PropertyFactory.iconImage("bataskmimg"),
                                PropertyFactory.iconAllowOverlap(false),
                                PropertyFactory.textAllowOverlap(false),
                                PropertyFactory.textField(get("label")),
                                PropertyFactory.textRadialOffset(1.5f),
                                PropertyFactory.textAnchor(ngisor),
                                PropertyFactory.textJustify(justify),
                                PropertyFactory.textSize(8f),
                                PropertyFactory.iconSize(0.6f)
                            ));

                            SymbolLayer symbolbataskm = style.getLayerAs("finalbataskm");
                            if (userSetting.getBataskm().equals(UserSetting.onSet)){
                                if (symbolbataskm != null){
                                    symbolbataskm.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolbataskm != null){
                                    symbolbataskm.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });

                    }else{
                        Log.d("Err DB Btaskm", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data Batas Km", call.toString());
            }
        });

    }

    private void JalanPenghubung(Style style, MapboxMap mapboxMap){
        Log.d("Jalan Penghubung...", "Running");
        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutejalanpenghubung(token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("ramp", featureCollection.toJson()));
                            style.addLayer(new LineLayer("finalramp", "ramp").withProperties(
                                    PropertyFactory.lineColor(Color.GRAY),
                                    PropertyFactory.lineWidth(3f)
                            ));

                            LineLayer lineramp = style.getLayerAs("finalramp");
                            if (userSetting.getJalanpenghubung().equals(UserSetting.onSet)){
                                if (lineramp != null){
                                    lineramp.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (lineramp != null){
                                    lineramp.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });

                    }else{
                        Log.d("Err DB Jalan penghubung", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data ramp", call.toString());
            }
        });

    }

    private void GerbangTol(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);
        loadingDialog.showLoadingDialog("Memuat layer......");
        serviceAPI = ApiClient.getClient(this);
        ReqInterface serviceAPI2 = ApiClient.getServiceNew(this);
        Call<JsonObject> callsiputol = serviceAPI2.excutegerbangsisputol(token);

        Call<JsonObject> call = serviceAPI.excutegerbangtol(token,paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        callsiputol.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    if (response.body()!=null){
                                        Log.d(TAG, "onResponse: " + response.body());
                                        JSONObject dataResisputol = new JSONObject(response.body().toString());

                                        FeatureCollection featureCollectionsis = FeatureCollection.fromJson(dataResisputol.toString());
                                        mapboxMap.getStyle(style -> {
                                            style.addSource(new GeoJsonSource("gerbangsisputol", featureCollectionsis.toJson()));
                                            style.addLayer(new SymbolLayer("finalgerbangsisputol", "gerbangsisputol").withProperties(
                                                    PropertyFactory.iconImage(get("status")),
                                                    PropertyFactory.iconAllowOverlap(false),
                                                    PropertyFactory.textAllowOverlap(false),
                                                    PropertyFactory.textField(get("nama_gerbang")),
                                                    PropertyFactory.textRadialOffset(1.5f),
                                                    PropertyFactory.textAnchor(ngisor),
                                                    PropertyFactory.textJustify(justify),
                                                    PropertyFactory.textSize(8f),
                                                    PropertyFactory.iconSize(0.6f)
                                            ));
                                        });
                                        loadingDialog.hideLoadingDialog();
                                        mapboxMap.addOnMapClickListener(pointgerbangsisputol -> {
                                            ShowAlert.showDialogGerbangSispuTol(Maps.this,alertDialogLineToll,mapboxMap,pointgerbangsisputol);
                                            return false;
                                        });
                                    }else {
                                        loadingDialog.hideLoadingDialog();
                                        Log.d(TAG, "onResponse: " +response.code() + "CODE : " + response.toString());
                                    }
                                } catch (JSONException e) {
                                    loadingDialog.hideLoadingDialog();
                                    throw new RuntimeException(e);
                                }

                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                                Log.d("Error Data gerbang tol", call.toString());
                                loadingDialog.hideLoadingDialog();
                            }
                        });
                        FeatureCollection featureCollectiongerbang = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("gerbangtol", featureCollectiongerbang.toJson()));
                            style.addLayer(new SymbolLayer("finalgerbangtol", "gerbangtol").withProperties(
                                    PropertyFactory.iconImage(get("poi")),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("nama_gerbang")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.6f)
                            ));
                            SymbolLayer symbolgerbangtol = style.getLayerAs("finalgerbangtol");
                            SymbolLayer symbolgerbangsistol = style.getLayerAs("finalgerbangsisputol");

                            if (userSetting.getGerbangsistol() != null && userSetting.getGerbangsistol().equals(UserSetting.onSet)) {
                                if (symbolgerbangsistol != null) {
                                    symbolgerbangsistol.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            } else {
                                if (symbolgerbangsistol != null) {
                                    symbolgerbangsistol.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }

                            if (userSetting.getGerbangtol() != null && userSetting.getGerbangtol().equals(UserSetting.onSet)) {
                                if (symbolgerbangtol != null) {
                                    symbolgerbangtol.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            } else {
                                if (symbolgerbangtol != null) {
                                    symbolgerbangtol.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });


                        mapboxMap.addOnMapClickListener(pointgerbangtol -> {
                            ShowAlert.showDIalogGerbangTol(Maps.this,alertDialogLineToll,mapboxMap,pointgerbangtol);
                            return false;
                        });
                    }else{
                        Log.d("Err DB gerbang tol", response.body().toString());
                        loadingDialog.hideLoadingDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.hideLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data gerbang tol", call.toString());
                loadingDialog.hideLoadingDialog();
            }
        });

    }

    private void RestArea(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excuterestarea(token,paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("restarea", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalrestarea", "restarea").withProperties(
                                    PropertyFactory.iconImage(get("poi")),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("nama_rest_area")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.6f)
                            ));
                            SymbolLayer symbolrestarea = style.getLayerAs("finalrestarea");
                            if (userSetting.getRestarea().equals(UserSetting.onSet)){
                                if (symbolrestarea != null){
                                    symbolrestarea.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolrestarea != null){
                                    symbolrestarea.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });

                        mapboxMap.addOnMapClickListener(point -> {
                            ShowAlert.showRestArea(Maps.this,alertDialogLineToll,mapboxMap,point);
                            return false;
                        });
                    }else{
                        Log.d("Err DB gerbang tol", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data gerbang tol", call.toString());
            }
        });

    }

    private void RoughnesIndex(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutrougnesindex(token,paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("rougnesindex", featureCollection.toJson()));
                            style.addLayer(new LineLayer("finalrougnesindex", "rougnesindex").withProperties(
                                PropertyFactory.lineColor(
                                    match(get("color"), rgb(0, 0, 0),
                                            stop("#008800", "#008800"),
                                            stop("#00cc00", "#00cc00"),
                                            stop("#44ff00", "#44ff00"),
                                            stop("#ffff00", "#ffff00"),
                                            stop("#ff8800", "#ff8800"),
                                            stop("#ff0000", "#ff0000"),
                                            stop("#888888", "#888888")
                                    )),
                                PropertyFactory.lineWidth(2f)
                            ));

                            LineLayer symbolrougnesindex = style.getLayerAs("finalrougnesindex");
                            if (userSetting.getRestarea().equals(UserSetting.onSet)){
                                if (symbolrougnesindex != null){
                                    symbolrougnesindex.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolrougnesindex != null){
                                    symbolrougnesindex.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }

                        });

                    }else{
                        Log.d("Err DB gerbang tol", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data gerbang tol", call.toString());
            }
        });

    }

    private void RTMS(Style style, MapboxMap mapboxMap){
        Log.d("RTMS", "run rtms layar...");
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutertms(token,paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("rtms", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalrtms", "rtms").withProperties(
                                    PropertyFactory.iconImage(get("img")),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("nama_lokasi")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.8f)
                            ));
                            SymbolLayer symbolrtms = style.getLayerAs("finalrtms");
                            if (userSetting.getRtms().equals(UserSetting.onSet)){
                                if (symbolrtms != null){
                                    symbolrtms.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolrtms != null){
                                    symbolrtms.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });

                        mapboxMap.addOnMapClickListener(pointrtms -> {
                            ShowAlert.showDialogRTMS(Maps.this,alertDialogLineToll,mapboxMap,pointrtms);
                            return false;
                        });
                    }else{
                        Log.d("Err DB rtms", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data rtms", call.toString());
            }
        });

    }

    private void RTMS2(Style style, MapboxMap mapboxMap){
        Log.d("RTMS2", "run rtms2 layar...");
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutertms2(token,paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("rtms2", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalrtms2", "rtms2").withProperties(
                                    PropertyFactory.iconImage(get("img")),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("km")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.8f)
                            ));
                            SymbolLayer symbolrtms2 = style.getLayerAs("finalrtms2");
                            if (userSetting.getRtms2().equals(UserSetting.onSet)){
                                if (symbolrtms2 != null){
                                    symbolrtms2.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolrtms2 != null){
                                    symbolrtms2.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });

                        mapboxMap.addOnMapClickListener(pointrtms2 -> {
                            ShowAlert.showDialogRTMSCCTV(Maps.this,alertDialogLineToll,mapboxMap,pointrtms2);
                            return false;
                        });
                    }else{
                        Log.d("Err DB rtms cctv", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data rtms cctv", call.toString());
            }
        });

    }

    private void SpeedCounting(Style style, MapboxMap mapboxMap){
        Log.d("Speed", "run speed counting layar...");
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutespeed(token,paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("speed", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalspeed", "speed").withProperties(
                                    PropertyFactory.iconImage("speedimg"),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("nama_lokasi")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.8f)
                            ));
                            SymbolLayer symbolspeed = style.getLayerAs("finalspeed");
                            if (userSetting.getSpeed().equals(UserSetting.onSet)){
                                if (symbolspeed != null){
                                    symbolspeed.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolspeed != null){
                                    symbolspeed.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });

                        mapboxMap.addOnMapClickListener(point -> {
                            ShowAlert.showDialogSpeed(Maps.this,alertDialogLineToll,mapboxMap,point);
                            return false;
                        });
                    }else{
                        Log.d("Err DB speed counting", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("ErrorSpeed", call.toString());
            }
        });

    }

    private void WaterLevelSensor(Style style, MapboxMap mapboxMap){
        Log.d("Water", "run water level layar...");
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutewaterlevel(token,paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("level", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finallevel", "level").withProperties(
                                    PropertyFactory.iconImage("levelimg"),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("level_sensor")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.6f)
                            ));
                            SymbolLayer symbollevel = style.getLayerAs("finallevel");
                            if (userSetting.getWaterlevel().equals(UserSetting.onSet)){
                                if (symbollevel != null){
                                    symbollevel.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbollevel != null){
                                    symbollevel.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });

                        mapboxMap.addOnMapClickListener(point -> {
                            ShowAlert.showDialogWaterLevel(Maps.this,alertDialogLineToll,mapboxMap,point);
                            return false;
                        });
                    }else{
                        Log.d("Err DB water", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error water", call.toString());
            }
        });

    }

    private void PompaBajir(Style style, MapboxMap mapboxMap){
        Log.d("Water", "run pompa layar...");

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutepompa(token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("pompa", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalpompa", "pompa").withProperties(
                                    PropertyFactory.iconImage("pompaimg"),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("no_urut_pompa")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.8f)
                            ));
                            SymbolLayer symbolpompa = style.getLayerAs("finalpompa");
                            if (userSetting.getPompa().equals(UserSetting.onSet)){
                                if (symbolpompa != null){
                                    symbolpompa.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolpompa != null){
                                    symbolpompa.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });

                        mapboxMap.addOnMapClickListener(point -> {
                            ShowAlert.showDialogPompaBanjir(Maps.this,alertDialogLineToll,mapboxMap,point);
                            return false;
                        });
                    }else{
                        Log.d("Err DB pompa", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error pompa", call.toString());
            }
        });

    }

    private void WIMData(Style style, MapboxMap mapboxMap){
        Log.d("Water", "run WIM layar...");
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutewim(token,paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("wim", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalwim", "wim").withProperties(
                                    PropertyFactory.iconImage("wimimg"),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("level_sensor")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.6f)
                            ));
                            SymbolLayer symbolwim = style.getLayerAs("finalwim");
                            if (userSetting.getWim().equals(UserSetting.onSet)){
                                if (symbolwim != null){
                                    symbolwim.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolwim != null){
                                    symbolwim.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });

                        mapboxMap.addOnMapClickListener(point -> {
                            ShowAlert.showDialogWIM(Maps.this,alertDialogLineToll,mapboxMap,point);
                            return false;
                        });
                    }else{
                        Log.d("Err DB WIM", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error WIM", call.toString());
            }
        });

    }

    private void Bike(Style style, MapboxMap mapboxMap){
        Log.d("Bike", "run Bike layar...");

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutebike(token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("bike", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalbike", "bike").withProperties(
                                    PropertyFactory.iconImage("bikeimg"),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("nama_lokasi")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.7f)
                            ));
                            SymbolLayer symbolbike = style.getLayerAs("finalbike");
                            if (userSetting.getBike().equals(UserSetting.onSet)){
                                if (symbolbike != null){
                                    symbolbike.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolbike != null){
                                    symbolbike.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });

                        mapboxMap.addOnMapClickListener(point -> {
                            ShowAlert.showDialogBike(Maps.this,alertDialogLineToll,mapboxMap,point);
                            return false;
                        });
                    }else{
                        Log.d("Err DB WIM", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error WIM", call.toString());
            }
        });

    }

    private void GpsKendaraanOprasinal(Style style, MapboxMap mapboxMap){
        Log.d("Bike", "run Bike layar...");
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutegpskendaraan(token,paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("gpskendaraan", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalgpskendaraan", "gpskendaraan").withProperties(
                                    PropertyFactory.iconImage(get("poi")),
                                    PropertyFactory.iconAllowOverlap(false),
                                    PropertyFactory.textAllowOverlap(false),
                                    PropertyFactory.textField(get("vehicle_name")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.7f),
                                    PropertyFactory.iconRotate(get("heading")),
                                    PropertyFactory.iconIgnorePlacement(true),
                                    PropertyFactory.iconRotationAlignment(Property.ICON_ROTATION_ALIGNMENT_VIEWPORT)
                            ));
                            SymbolLayer symbolgpskend = style.getLayerAs("finalgpskendaraan");
                            if (userSetting.getGpskend().equals(UserSetting.onSet)){
                                if (symbolgpskend != null){
                                    symbolgpskend.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolgpskend != null){
                                    symbolgpskend.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }

                            serviceKondisiLalin.handleUpdateKendaraanOperasional(style, mapboxMap, "finalgpskendaraan", "gpskendaraan",scope);
                        });

                        mapboxMap.addOnMapClickListener(point -> {
                            ShowAlert.showDialogkendaraanOpera(Maps.this,alertDialogLineToll,mapboxMap,point);
                            return false;
                        });
                    }else{
                        Log.d("Err DB WIM", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error WIM", call.toString());
            }
        });

    }

    private void Radar(Style style, MapboxMap mapboxMap){
        Log.d("Radar", "run radar layar...");
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excuteradar(token,paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("radar", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalradar", "radar").withProperties(
                                    PropertyFactory.iconImage(get("icon")),
                                    PropertyFactory.iconAllowOverlap(true),
                                    PropertyFactory.textAllowOverlap(true),
                                    PropertyFactory.textField(get("nama_lokasi")),
                                    PropertyFactory.textRadialOffset(1.5f),
                                    PropertyFactory.textAnchor(ngisor),
                                    PropertyFactory.textJustify(justify),
                                    PropertyFactory.textSize(8f),
                                    PropertyFactory.iconSize(0.09f)
                            ));
                            SymbolLayer symbolradar = style.getLayerAs("finalradar");
                            if (userSetting.getRadar().equals(UserSetting.onSet)){
                                if (symbolradar != null){
                                    symbolradar.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolradar != null){
                                    symbolradar.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }
                        });
                        mapboxMap.addOnMapClickListener(point -> {
                            ShowAlert.showDialogRadar(Maps.this,alertDialogLineToll,mapboxMap,point, getApplicationContext());
                            return false;
                        });
                    }else{
                        Log.d("Err DB radar", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error radar", call.toString());
            }
        });

    }

    private void MidasData(Style style, MapboxMap mapboxMap){
        Log.d("Midas", "run Midas layar...");
        serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutemidas(token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    Log.d(TAG, "onResponse: Data Midas" + dataRes);
                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style -> {
                            style.addSource(new GeoJsonSource("midas", featureCollection.toJson()));
                            style.addLayer(new SymbolLayer("finalmidas", "midas").withProperties(
                                    PropertyFactory.iconImage("midasimg"),
                                    PropertyFactory.iconAllowOverlap(true),
                                    PropertyFactory.iconSize(0.01f)
                            ));

                            pulseIcon("finalmidas", mapboxMap);
                            serviceKondisiLalin.handleUpdateMidas(style, mapboxMap, "finalmidas", "midas",scope);

                        });

                        mapboxMap.addOnMapClickListener(point -> {
                            ShowAlert.showDialogMidas(Maps.this,alertDialogLineToll,mapboxMap,point, getApplicationContext());
                            return false;
                        });
                    }else{
                        Log.d("Err DB midas", response.body().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error midas", call.toString());
            }
        });

    }

    private void pulseIcon(String layarid, MapboxMap mapboxMap) {
        SymbolLayer symbolmidas = styleSet.getLayerAs(layarid);
        mapboxMap.getStyle(style -> {
            markerAnimator = new ValueAnimator();
            markerAnimator.setFloatValues(0.01f, 0.03f);
            markerAnimator.setDuration(900);
            markerAnimator.setRepeatCount(ValueAnimator.INFINITE);
            markerAnimator.setRepeatMode(ValueAnimator.REVERSE);
            markerAnimator.addUpdateListener(animator -> symbolmidas.setProperties(
                    PropertyFactory.iconSize((float) animator.getAnimatedValue())
            ));
            markerAnimator.start();
        });
    }

    private void delSession() {
        loadingDialog = new LoadingDialog(Maps.this);
        loadingDialog.showLoadingDialog("Loading...");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", username);

        ReqInterface serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutedelsession(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        serviceKondisiLalin.removeCallbacksHandle();
                        serviceRealtime.removeCallbacksHandle();
                        sessionmanager.logout();
                        finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (uri != null){
            setHLS();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        if (player != null) {
            // Pause the player
            player.setPlayWhenReady(false);
            // Release the player to free resources
            player.release();
            uri = null;
            // Set the player to null to avoid memory leaks
            player = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (player != null) {
            // Pause the player
            player.setPlayWhenReady(false);
            // Release the player to free resources
            player.release();
            // Set the player to null to avoid memory leaks
            player = null;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (sheetDialog!=null && sheetDialog.isShowing()){
            sheetDialog.dismiss();
        }
        if (player != null) {
            // Pause the player
            player.setPlayWhenReady(false);
            // Release the player to free resources
            player.release();
            // Set the player to null to avoid memory leaks
            player = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBackPressed() {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle("Warning");
            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari aplikasi ?");
            alertDialogBuilder.setIcon(R.drawable.logojm);
            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> {
                markerAnimator.cancel();
                serviceKondisiLalin.removeCallbacksHandle();
                serviceRealtime.removeCallbacksHandle();
                serviceKondisiLalin.removeCallKendaraanOperasional();
                serviceKondisiLalin.removeCallMidas();
                if (player != null) {
                    // Pause the player
                    player.setPlayWhenReady(false);
                    // Release the player to free resources
                    player.release();
                    // Set the player to null to avoid memory leaks
                    player = null;
                }
                finish();
            });
            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();

    }

    private void menuBottomnavbar(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.peta);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    if (markerAnimator!=null){
                        markerAnimator.cancel();
                    }
                    serviceKondisiLalin.removeCallbacksHandle();
                    serviceRealtime.removeCallbacksHandle();
                    serviceKondisiLalin.removeCallKendaraanOperasional();
                    serviceKondisiLalin.removeCallMidas();
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.cctv:
                    if (markerAnimator!=null){
                        markerAnimator.cancel();
                    }
                    serviceKondisiLalin.removeCallbacksHandle();
                    serviceRealtime.removeCallbacksHandle();
                    serviceKondisiLalin.removeCallKendaraanOperasional();
                    serviceKondisiLalin.removeCallMidas();
                    startActivity(new Intent(getApplicationContext(), Cctv.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.antrian_gerbang:
                    if (markerAnimator!=null){
                        markerAnimator.cancel();
                    }
                    serviceKondisiLalin.removeCallbacksHandle();
                    serviceRealtime.removeCallbacksHandle();
                    serviceKondisiLalin.removeCallKendaraanOperasional();
                    serviceKondisiLalin.removeCallMidas();
                    startActivity(new Intent(getApplicationContext(), Antrian.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.realtime_lalin:
                    if (markerAnimator!=null){
                        markerAnimator.cancel();
                    }
                    serviceKondisiLalin.removeCallbacksHandle();
                    serviceRealtime.removeCallbacksHandle();
                    serviceKondisiLalin.removeCallKendaraanOperasional();
                    serviceKondisiLalin.removeCallMidas();
                    startActivity(new Intent(getApplicationContext(), RealtimeTraffic.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;

            }
            return false;
        });
    }
}
