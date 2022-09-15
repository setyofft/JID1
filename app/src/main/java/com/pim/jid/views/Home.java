package com.pim.jid.views;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
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
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.pim.jid.Dashboard;
import com.pim.jid.LoadingDialog;
import com.pim.jid.R;
import com.pim.jid.ServiceRealtime;
import com.pim.jid.Sessionmanager;
import com.pim.jid.adapter.MenuAdapter;
import com.pim.jid.adapter.UserSetting;
import com.pim.jid.router.ApiClient;
import com.pim.jid.router.ReqInterface;
import com.pim.jid.service.ServiceKondisiLalin;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {

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
    private Boolean aktif_gangguan_lalin = true;
    private Boolean aktif_pemeliharaan = true;
    private Boolean aktif_rekayasa_lalin = true;
    private Boolean aktif_kondisi_traffic = true;
    private Boolean aktif_toll = true;
    private Boolean aktif_cctv = false;
    private Boolean aktif_vms = false;

    private List<String> datalistmenu;
    private MenuAdapter menuAdapter;

    private UserSetting userSetting;
    AlertDialog alertDialogLineToll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        sessionmanager = new Sessionmanager(getApplicationContext());
        serviceRealtime = new ServiceRealtime(getApplicationContext());
        serviceKondisiLalin = new ServiceKondisiLalin(getApplicationContext());

        userSetting = (UserSetting)getApplication();

        HashMap<String, String> user = sessionmanager.getUserDetails();
        scope = user.get(Sessionmanager.set_scope);
        username = user.get(Sessionmanager.kunci_id);
        item = user.get(Sessionmanager.set_item);

        ngisor = "top";
        justify = "center";

        mapView = findViewById(R.id.mapView);

        menuBottomnavbar();

        initMaps();
        initAction();

    }

    private void initAction() {
        logout = findViewById(R.id.logout);
        setting_layer = findViewById(R.id.setting_layer);
        list_menu = findViewById(R.id.list_menu);
        center_fit = findViewById(R.id.center_fit);
        overlap_symbol = findViewById(R.id.overlap_symbol);
//        refresh_data = findViewById(R.id.refresh_data);
        bottom_sheet = findViewById(R.id.bottom_sheet_dialog);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        logout.setText(username);
        logout.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Home.this);
            alertDialogBuilder.setTitle("Peringatan Akun");
            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> delSession());
            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();
        });

        setting_layer.setOnClickListener(v -> {
            View view = getLayoutInflater().inflate(R.layout.layer_dialog, null);

            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            sheetDialog = new BottomSheetDialog(this, R.style.BottomSheetTheme);
            Switch switch_jalan_toll = view.findViewById(R.id.switch_jalan_toll);
            Switch switch_kondisi_traffic = view.findViewById(R.id.switch_kondisi_traffic);
            Switch switch_cctv = view.findViewById(R.id.switch_cctv);
            Switch switch_vms = view.findViewById(R.id.switch_vms);
            Switch switch_pemeliharaan = view.findViewById(R.id.switch_pemeliharaan);
            Switch switch_gangguan_lalin = view.findViewById(R.id.switch_gangguan_lalin);
            Switch switch_rekayasalalin = view.findViewById(R.id.switch_rekayasalalin);
            Button set_layer = view.findViewById(R.id.set_layer);

            initSetChecked(switch_jalan_toll, switch_kondisi_traffic, switch_cctv, switch_vms, switch_pemeliharaan, switch_gangguan_lalin, switch_rekayasalalin);
            set_layer.setOnClickListener(v1 -> initKondisiAktif(sheetDialog, switch_jalan_toll, switch_kondisi_traffic, switch_cctv, switch_vms, switch_pemeliharaan,
                    switch_gangguan_lalin, switch_rekayasalalin));
            sheetDialog.setContentView(view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                sheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            sheetDialog.show();

            sheetDialog.setOnDismissListener(dialog -> sheetDialog.dismiss());
        });

        list_menu.setOnClickListener(v -> {
            View view = getLayoutInflater().inflate(R.layout.layer_list_menu, null);
            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            sheetDialog = new BottomSheetDialog(this, R.style.BottomSheetTheme);
            datalistmenu = new ArrayList<>();
            datalistmenu.add("Realtime Lalin");
            datalistmenu.add("Antrian Gerbang");
            datalistmenu.add("Lalin Perjam");

            RecyclerView recyclerView = view.findViewById(R.id.data_list_menu);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            menuAdapter = new MenuAdapter(this, datalistmenu);

            menuAdapter.setClickListener((view1, position) -> {
                String selekmenu = datalistmenu.get(position);
                if (selekmenu.equals("Realtime Lalin")){
                    Intent intent = new Intent(this, Realtimelalin.class);
                    this.startActivity(intent);
                    sheetDialog.hide();
                }else if(selekmenu.equals("Antrian Gerbang")){
                    Intent intent = new Intent(getApplicationContext(), Activitiweb.class);
                    intent.putExtra("hosturl", "https://jid.jasamarga.com/graph/antrian_gerbang_mobile");
                    this.startActivity(intent);
                    sheetDialog.dismiss();
                }else if(selekmenu.equals("Lalin Perjam")){
                    Intent intent = new Intent(getApplicationContext(), Activitiweb.class);
                    intent.putExtra("hosturl", "https://jid.jasamarga.com/graph/lalin_perjam");
                    this.startActivity(intent);
                    sheetDialog.dismiss();
                }
            });

            recyclerView.setAdapter(menuAdapter);
            sheetDialog.setContentView(view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                sheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            sheetDialog.show();
            sheetDialog.setOnDismissListener(dialog -> sheetDialog = null);
        });

        center_fit.setOnClickListener(v -> mapboxMapSet.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(-2.9638907, 109.24058))
                .zoom(3.5)
                .tilt(1.0)
                .bearing(0)
                .build()),
        1000));

        overlap_symbol.setOnClickListener(v -> {
            if (userSetting.getCctv().equals(UserSetting.onSet) || userSetting.getVms().equals(UserSetting.onSet)){
                if(overlap_status.equals("aktif")){
                    overlap_symbol.setImageDrawable(getResources().getDrawable(R.drawable.ic_overlap_true));
                    cekAktifChecked();
                    overlap_status = "Nonaktif";
                    Toast.makeText(getApplicationContext(), "Overlap Semua Icon Aktif", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Overlap Semua Icon NonAktif", Toast.LENGTH_SHORT).show();
                    overlap_symbol.setImageDrawable(getResources().getDrawable(R.drawable.ic_overlap_false));
                    cekNonAktifChecked();
                    overlap_status = "aktif";
                }
            }else{
                Toast.makeText(getApplicationContext(), "Tidak ada CCTV atau VMS yang aktif", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void cekAktifChecked(){
        SymbolLayer symbolcctv = styleSet.getLayerAs("finalcctv");
        SymbolLayer symbolvms = styleSet.getLayerAs("finalvms");
        if (userSetting.getCctv().equals(UserSetting.onSet)){
            symbolcctv.setProperties(
                    PropertyFactory.textAllowOverlap(true),
                    PropertyFactory.iconAllowOverlap(true)
            );
        }

        if (userSetting.getVms().equals(UserSetting.onSet)){
            symbolvms.setProperties(
                    PropertyFactory.textAllowOverlap(true),
                    PropertyFactory.iconAllowOverlap(true)
            );
        }
    }

    private void cekNonAktifChecked(){
        SymbolLayer symbolcctv = styleSet.getLayerAs("finalcctv");
        SymbolLayer symbolvms = styleSet.getLayerAs("finalvms");
        if (userSetting.getCctv().equals(UserSetting.onSet)){
            symbolcctv.setProperties(
                    PropertyFactory.textAllowOverlap(false),
                    PropertyFactory.iconAllowOverlap(false)
            );
        }

        if (userSetting.getVms().equals(UserSetting.onSet)){
            symbolvms.setProperties(
                    PropertyFactory.textAllowOverlap(false),
                    PropertyFactory.iconAllowOverlap(false)
            );
        }
    }

    private void initSetChecked(Switch switch_jalan_toll, Switch switch_kondisi_traffic, Switch switch_cctv, Switch switch_vms, Switch switch_pemeliharaan, Switch switch_gangguan_lalin, Switch switch_rekayasalalin) {
        if(userSetting.getJalanToll().equals(UserSetting.onSet)){
            switch_jalan_toll.setChecked(true);
        }else{
            switch_jalan_toll.setChecked(false);
        }
        if(userSetting.getKondisiTraffic().equals(UserSetting.onSet)){
            switch_kondisi_traffic.setChecked(true);
        }else{
            switch_kondisi_traffic.setChecked(false);
        }
        if(userSetting.getCctv().equals(UserSetting.onSet)){
            switch_cctv.setChecked(true);
        }else{
            switch_cctv.setChecked(false);
        }
        if(userSetting.getVms().equals(UserSetting.onSet)){
            switch_vms.setChecked(true);
        }else{
            switch_vms.setChecked(false);
        }
        if(userSetting.getPemeliharaan().equals(UserSetting.onSet)){
            switch_pemeliharaan.setChecked(true);
        }else{
            switch_pemeliharaan.setChecked(false);
        }
        if(userSetting.getGangguanLalin().equals(UserSetting.onSet)){
            switch_gangguan_lalin.setChecked(true);
        }else{
            switch_gangguan_lalin.setChecked(false);
        }
        if(userSetting.getRekayasaLalin().equals(UserSetting.onSet)){
            switch_rekayasalalin.setChecked(true);
        }else{
            switch_rekayasalalin.setChecked(false);
        }
    }

    private void initKondisiAktif(BottomSheetDialog sheetDialog, Switch switch_jalan_toll, Switch switch_kondisi_traffic, Switch switch_cctv, Switch switch_vms, Switch switch_pemeliharaan, Switch switch_gangguan_lalin, Switch switch_rekayasalalin) {
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
        //VMS
        if (switch_vms.isChecked()){
            if (symbolvms != null) {
                symbolvms.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }else{
                initLoadVMS(styleSet, mapboxMapSet);
            }
            userSetting.setVms(UserSetting.onSet);
            editor.putString(UserSetting.VMSSET, userSetting.getVms());
            editor.apply();
        }else{
            if (symbolvms != null) {
                symbolvms.setProperties(PropertyFactory.visibility(Property.NONE));
            }
            userSetting.setVms(UserSetting.offSet);
            editor.putString(UserSetting.VMSSET, userSetting.getVms());
            editor.apply();
        }
        //oemeliharaan
        if (switch_pemeliharaan.isChecked()){
            if (symbolpemeliharaan != null) {
                symbolpemeliharaan.setProperties(PropertyFactory.visibility(Property.VISIBLE));
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
        if (switch_gangguan_lalin.isChecked()){
            if (symbolgangguan != null) {
                symbolgangguan.setProperties(PropertyFactory.visibility(Property.VISIBLE));
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
        if (switch_rekayasalalin.isChecked()){
            if (symbolrekayasalain != null){
                symbolrekayasalain.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                linerekayasalalin.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                symbollinerekayasalain.setProperties(PropertyFactory.visibility(Property.VISIBLE));
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

        sheetDialog.dismiss();
    }

    private void initMaps() {
        loadingDialog = new LoadingDialog(Home.this);
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
                    iconImage(style);

                    initLineToll(style);
                    RekaysaLalin(style, mapboxMap);

                    loadingDialog.hideLoadingDialog();
                }
            });
        });
    }

    private void initLalinLocal(Style style, MapboxMap mapboxMap) {
        String datalain = getFile("lalin.json");
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

        serviceRealtime.handleRunServiceLalin(style, mapboxMap, "finallalin", "lalin");

        mapboxMap.addOnMapClickListener(pointlalin -> {
            aktif_popup_lalin = "true";
            PointF screenPointcctv = mapboxMap.getProjection().toScreenLocation(pointlalin);
            List<Feature> featurescctv = mapboxMap.queryRenderedFeatures(screenPointcctv, "finallalin");
            if (!featurescctv.isEmpty()) {
                Feature selectedFeaturecctv = featurescctv.get(0);
                String kec_google = selectedFeaturecctv.getStringProperty("kec_google");
                String nmsegment = selectedFeaturecctv.getStringProperty("nama_segment");
                String nmsubsegment = selectedFeaturecctv.getStringProperty("nama_sub_segment");
                String ruas_tol = selectedFeaturecctv.getStringProperty("ruas_tol");
                String nama_jalur = selectedFeaturecctv.getStringProperty("nama_jalur");
                String kondisi = selectedFeaturecctv.getStringProperty("kondisi");
                String himbauan = selectedFeaturecctv.getStringProperty("himbauan");
                String panjang_segment = selectedFeaturecctv.getStringProperty("panjang_segment");
                String waktu_tempuh = selectedFeaturecctv.getStringProperty("waktu_tempuh");
                String update_time = selectedFeaturecctv.getStringProperty("update_time");

                AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
                alert.setCancelable(false);

                LayoutInflater inflater = getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.custom_dialog_lalin, null);
                alert.setView(dialoglayout);

                TextView txt_nm_segment = (TextView) dialoglayout.findViewById(R.id.txt_nm_segment);
                TextView txt_sub_Segment = (TextView) dialoglayout.findViewById(R.id.txt_sub_Segment);
                TextView txt_ruas_tol = (TextView) dialoglayout.findViewById(R.id.txt_ruas_tol);
                TextView txt_nm_lajur = (TextView) dialoglayout.findViewById(R.id.txt_nm_lajur);
                TextView txt_kondisi = (TextView) dialoglayout.findViewById(R.id.txt_kondisi);
                TextView txt_himbauan = (TextView) dialoglayout.findViewById(R.id.txt_himbauan);
                TextView txt_kec_google = (TextView) dialoglayout.findViewById(R.id.txt_kec_google);
                TextView txt_panjang_segment = (TextView) dialoglayout.findViewById(R.id.txt_panjang_segment);
                TextView txt_waktu_tempuh = (TextView) dialoglayout.findViewById(R.id.txt_waktu_tempuh);
                TextView txt_update = (TextView) dialoglayout.findViewById(R.id.txt_update);

                txt_nm_segment.setText(nmsegment);
                txt_sub_Segment.setText(nmsubsegment);
                txt_ruas_tol.setText(ruas_tol);
                txt_nm_lajur.setText(nama_jalur);
                txt_kondisi.setText(kondisi);
                txt_himbauan.setText(himbauan);
                txt_kec_google.setText(kec_google);
                txt_panjang_segment.setText(panjang_segment);
                txt_waktu_tempuh.setText(waktu_tempuh);
                txt_update.setText(update_time);
                Button btn_close = (Button) dialoglayout.findViewById(R.id.btn_close);

                alertDialogLineToll = alert.create();
                alertDialogLineToll.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                btn_close.setOnClickListener(v -> {
//                    serviceRealtime.removeCallbacksHandleLalinservice();
                    alertDialogLineToll.cancel();
                });
                alertDialogLineToll.show();
            }
            return false;
        });

        LineLayer linelalin = styleSet.getLayerAs("finallalin");
        if(userSetting.getKondisiTraffic().equals(UserSetting.onSet)){
            if (linelalin != null) {
                linelalin.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }
        }else{
            if (linelalin != null) {
                linelalin.setProperties(PropertyFactory.visibility(Property.NONE));
            }
        }
    }

    private void initLineToll(Style style) {
        try {
            style.addSource(new GeoJsonSource("toll", new URI("https://jid.jasamarga.com/map/tol.json")));
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
        }
        style.addLayer(new LineLayer("finaltoll", "toll").withProperties(
                PropertyFactory.lineColor(Color.GRAY),
                PropertyFactory.lineWidth(3f)
        ));

        LineLayer linetol = styleSet.getLayerAs("finaltoll");
        if(userSetting.getJalanToll().equals(UserSetting.onSet)){
            if (linetol != null) {
                linetol.setProperties(PropertyFactory.visibility(Property.VISIBLE));
            }
        }else{
            if (linetol != null) {
                linetol.setProperties(PropertyFactory.visibility(Property.NONE));
            }
        }
    }

    private void initLoadCCTV(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutegetcctv(paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
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

//                        serviceRealtime.handleRunServiceCCTV(style, mapboxMap, "finalcctv", "cctv", scope);

                        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @Override
                            public boolean onMapClick(LatLng pointcctv) {
                                PointF screenPointcctv = mapboxMap.getProjection().toScreenLocation(pointcctv);
                                List<Feature> featurescctv = mapboxMap.queryRenderedFeatures(screenPointcctv, "finalcctv");
                                if (!featurescctv.isEmpty()) {
                                    Feature selectedFeaturecctv = featurescctv.get(0);
                                    String cabang = selectedFeaturecctv.getStringProperty("cabang");
                                    String jnscctv = selectedFeaturecctv.getStringProperty("jns_cctv");
                                    String camera_id = selectedFeaturecctv.getStringProperty("camera_id");
                                    String nama = selectedFeaturecctv.getStringProperty("nama");
                                    String status = selectedFeaturecctv.getStringProperty("status");
                                    String key_id = selectedFeaturecctv.getStringProperty("key_id");

                                    AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
                                    alert.setCancelable(false);

                                    LayoutInflater inflater = getLayoutInflater();
                                    View dialoglayout = inflater.inflate(R.layout.custom_dialog, null);
                                    alert.setView(dialoglayout);

                                    handler_cctv = new Handler();

                                    ImageView img= (ImageView) dialoglayout.findViewById(R.id.showImg);
                                    ProgressBar loadingIMG= (ProgressBar) dialoglayout.findViewById(R.id.loadingIMG);
                                    TextView jns_cctv = (TextView) dialoglayout.findViewById(R.id.jns_cctv);
                                    TextView txt_cabang = (TextView) dialoglayout.findViewById(R.id.txt_cabang);
                                    TextView txt_nama = (TextView) dialoglayout.findViewById(R.id.txt_nama);
                                    TextView set_cctv_off = (TextView) dialoglayout.findViewById(R.id.set_cctv_off);
                                    Button btn_close = (Button) dialoglayout.findViewById(R.id.btn_close);

                                    jns_cctv.setText(jnscctv);
                                    txt_cabang.setText(cabang);
                                    txt_nama.setText(nama);

                                    final AlertDialog  alertDialog = alert.create();
                                    ArrayList<String> imagesList= new ArrayList<String>();

                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    btn_close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            handler_cctv.removeCallbacksAndMessages(null);
//                                            serviceRealtime.removeCallbacksHandleCCTVservice();
                                            alertDialog.cancel();
                                        }
                                    });

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
                                                initStreamImg(img_url, img, loadingIMG);
                                                handler_cctv.postDelayed(this, 300);
                                            }
                                        }, 300);
                                    }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void iconImage(Style style) {
        style.addImageAsync("main_rood_off", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.cctv_r_24)));
        style.addImageAsync("main_rood_on", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.cctv_b_24)));
        style.addImageAsync("arteri_off", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.arteri_off)));
        style.addImageAsync("arteri_on", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.arteri_on)));
        style.addImageAsync("genangan_off", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.genangan_off)));
        style.addImageAsync("genangan_on", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.genangan_on)));
        style.addImageAsync("gerbang_off", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.gerbang_off)));
        style.addImageAsync("gerbang_on", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.gerbang_on)));
        style.addImageAsync("ss_off", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.ss_off)));
        style.addImageAsync("ss_on", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.ss_on)));
        style.addImageAsync("ramp_off", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.rampp_off)));
        style.addImageAsync("ramp_on", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.ramp_on)));
        style.addImageAsync("elevated_on", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.elevated_on)));
        style.addImageAsync("elevated_off", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.elevated_off)));
        style.addImageAsync("vms_off", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.vms_off)));
        style.addImageAsync("vms_on", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.vms_on)));
        style.addImageAsync("gangguan_lalin", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.crash_32)));
        style.addImageAsync("pemeliharaanimg", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.repair_32)));
        style.addImageAsync("rekayasalalinimg", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.control_32)));
        style.addImageAsync("arrow", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.arrow_line_biru)));
        style.addImageAsync("rekayasapengalihan", BitmapUtils.getBitmapFromDrawable(
                getDrawable(R.drawable.noway_24)));
    }

    private void initLoadVMS(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutegetvms(paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollectionvms = FeatureCollection.fromJson(dataRes.getString("data"));
                        style.addSource(new GeoJsonSource("vms", featureCollectionvms.toJson()));

                        style.addLayer(new SymbolLayer("finalvms", "vms").withProperties(
                                PropertyFactory.iconImage(get("icon")),
                                PropertyFactory.textAllowOverlap(false),
                                PropertyFactory.textField(get("nama_lokasi")),
                                PropertyFactory.textRadialOffset(1.3f),
                                PropertyFactory.textAnchor(ngisor),
                                PropertyFactory.textJustify(justify),
                                PropertyFactory.textSize(8f)
                        ));

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
                                    String cabang = selectedFeaturevms.getStringProperty("cabang");
                                    String status_koneksi = selectedFeaturevms.getStringProperty("status_koneksi");
                                    String waktu_kirim_terakhir = selectedFeaturevms.getStringProperty("waktu_kirim_terakhir");
                                    String pesan_item = selectedFeaturevms.getStringProperty("pesan_item").toString();
                                    String jmlpesan = selectedFeaturevms.getStringProperty("jml_pesan");

                                    AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
                                    alert.setCancelable(false);

                                    LayoutInflater inflater = getLayoutInflater();
                                    View dialoglayout = inflater.inflate(R.layout.custom_vms_dialog, null);
                                    alert.setView(dialoglayout);

                                    ImageView img_pesan = (ImageView) dialoglayout.findViewById(R.id.img_pesan);
                                    ProgressBar loadingIMGvms= (ProgressBar) dialoglayout.findViewById(R.id.loadingIMGvms);
                                    TextView nm_lokasi = (TextView) dialoglayout.findViewById(R.id.nm_lokasi);
                                    TextView nm_cabang = (TextView) dialoglayout.findViewById(R.id.nm_cabang);
                                    TextView tgl_update = (TextView) dialoglayout.findViewById(R.id.tgl_update);
                                    TextView jml_pesan = (TextView) dialoglayout.findViewById(R.id.jml_pesan);
                                    Button btn_close = (Button) dialoglayout.findViewById(R.id.btn_close);
                                    TextView set_vms_off = (TextView) dialoglayout.findViewById(R.id.set_vms_off);

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
                                                    img_vms = "https://jid.jasamarga.com/client-api/getimg/"+kode_lokasi+"/"+
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

    private void initStreamImg(String img_url, ImageView img, ProgressBar loadingIMG) {
        Glide.with(this)
                .asBitmap()
                .load(img_url)
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

//    public String convertUrlToBase64(String url) {
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

    private void GangguanLalin(Style style, MapboxMap mapboxMap){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutegangguanlalin(paramsIdruas);
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
                        });

                        SymbolLayer symbolgangguan = styleSet.getLayerAs("finalgangguan");
                        if (userSetting.getGangguanLalin().equals(UserSetting.onSet)){
                            if (symbolgangguan != null) {
                                symbolgangguan.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                            }
                        }else{
                            if (symbolgangguan != null) {
                                symbolgangguan.setProperties(PropertyFactory.visibility(Property.NONE));
                            }
                        }

//                        serviceKondisiLalin.handleRunServiceGangguan(style, mapboxMap, "finalgangguan", "gangguan", scope);
                        mapboxMap.addOnMapClickListener(pointvms -> {
                            PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(pointvms);
                            List<Feature> featuresvsms = mapboxMap.queryRenderedFeatures(screenPointvms, "finalgangguan");
                            if (!featuresvsms.isEmpty()) {
                                Feature selectedFeaturevms = featuresvsms.get(0);
                                String nama_ruas = selectedFeaturevms.getStringProperty("nama_ruas");
                                String km = selectedFeaturevms.getStringProperty("km");
                                String jalur = selectedFeaturevms.getStringProperty("jalur");
                                String lajur = selectedFeaturevms.getStringProperty("lajur");
                                String ket_tipe_gangguan = selectedFeaturevms.getStringProperty("ket_tipe_gangguan");
                                String waktu_kejadian = selectedFeaturevms.getStringProperty("waktu_kejadian");
                                String detail_kejadian = selectedFeaturevms.getStringProperty("detail_kejadian");
                                String ket_status = selectedFeaturevms.getStringProperty("ket_status");
                                String dampak = selectedFeaturevms.getStringProperty("dampak");
                                String waktu_selsai = selectedFeaturevms.getStringProperty("waktu_selsai");

                                if (waktu_selsai == null){
                                    waktu_selsai = "-";
                                }else {
                                    waktu_selsai = waktu_selsai;
                                }

                                AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
                                alert.setCancelable(false);

                                LayoutInflater inflater = getLayoutInflater();
                                View dialoglayout = inflater.inflate(R.layout.custome_dialog_gangguan, null);
                                alert.setView(dialoglayout);

                                TextView title_kondisi_lalin = dialoglayout.findViewById(R.id.title_kondisi_lalin);
                                TextView txt_status = dialoglayout.findViewById(R.id.txt_status);
                                TextView txt_nm_ruas = dialoglayout.findViewById(R.id.txt_nm_ruas);
                                TextView txt_nmKm = dialoglayout.findViewById(R.id.txt_nmKm);
                                TextView txt_jalur_lajur = dialoglayout.findViewById(R.id.txt_jalur_lajur);
                                TextView txt_tipegangguan = dialoglayout.findViewById(R.id.txt_tipegangguan);
                                TextView txt_waktu_kejadian = dialoglayout.findViewById(R.id.txt_waktu_kejadian);
                                TextView txt_dampak = dialoglayout.findViewById(R.id.txt_dampak);
                                TextView txt_waktu_selesai = dialoglayout.findViewById(R.id.txt_waktu_selesai);
                                TextView txt_keterangan = dialoglayout.findViewById(R.id.txt_keterangan);
                                Button btn_close = (Button) dialoglayout.findViewById(R.id.btn_close);

                                title_kondisi_lalin.setText("Gangguan Lalin");
                                txt_status.setText(ket_status);
                                txt_nm_ruas.setText(nama_ruas);
                                txt_nmKm.setText(km);
                                txt_jalur_lajur.setText(jalur+" / "+lajur);
                                txt_tipegangguan.setText(ket_tipe_gangguan);
                                txt_waktu_kejadian.setText(waktu_kejadian);
                                txt_dampak.setText(dampak);
                                txt_waktu_selesai.setText(waktu_selsai);
                                txt_keterangan.setText(detail_kejadian);

                                final AlertDialog  alertDialog = alert.create();
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                btn_close.setOnClickListener(v -> {
                                    alertDialog.cancel();
//                                        serviceKondisiLalin.removeCallbacksHandleGangguanLalin();
                                });

                                alertDialog.show();
                                if (alertDialogLineToll != null){
                                    alertDialogLineToll.cancel();
                                }
                            }
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

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutepemeliharaan(paramsIdruas);
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
                        });

                        SymbolLayer symbolpemeliharaan = styleSet.getLayerAs("finalpemeliharaan");
                        if (userSetting.getPemeliharaan().equals(UserSetting.onSet)){
                            if (symbolpemeliharaan != null) {
                                symbolpemeliharaan.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                            }
                        }else{
                            if (symbolpemeliharaan != null) {
                                symbolpemeliharaan.setProperties(PropertyFactory.visibility(Property.NONE));
                            }
                        }

//                        serviceKondisiLalin.handleRunServicePemeliharaan(style, mapboxMap, "finalpemeliharaan", "pemeliharaan", scope);

                        mapboxMap.addOnMapClickListener(pointvms -> {
                            PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(pointvms);
                            List<Feature> featuresvsms = mapboxMap.queryRenderedFeatures(screenPointvms, "finalpemeliharaan");
                            if (!featuresvsms.isEmpty()) {
                                Feature selectedFeaturevms = featuresvsms.get(0);
                                String nama_ruas = selectedFeaturevms.getStringProperty("nama_ruas");
                                String km = selectedFeaturevms.getStringProperty("km");
                                String jalur = selectedFeaturevms.getStringProperty("jalur");
                                String lajur = selectedFeaturevms.getStringProperty("lajur");
                                String range_km_pekerjaan = selectedFeaturevms.getStringProperty("range_km_pekerjaan");
                                String waktu_awal = selectedFeaturevms.getStringProperty("waktu_awal");
                                String waktu_akhir = selectedFeaturevms.getStringProperty("waktu_akhir");
                                String ket_jenis_kegiatan = selectedFeaturevms.getStringProperty("ket_jenis_kegiatan");
                                String keterangan_detail = selectedFeaturevms.getStringProperty("keterangan_detail");
                                String ket_status = selectedFeaturevms.getStringProperty("ket_status");


                                AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
                                alert.setCancelable(false);

                                LayoutInflater inflater = getLayoutInflater();
                                View dialoglayout = inflater.inflate(R.layout.custom_dialog_pemeliharaan, null);
                                alert.setView(dialoglayout);

                                TextView title_kondisi_lalin = dialoglayout.findViewById(R.id.title_kondisi_lalin);
                                TextView txt_nm_ruas = dialoglayout.findViewById(R.id.txt_nm_ruas);
                                TextView txt_nmKm = dialoglayout.findViewById(R.id.txt_nmKm);
                                TextView txt_jalur_lajur = dialoglayout.findViewById(R.id.txt_jalur_lajur);
                                TextView txt_range_km = dialoglayout.findViewById(R.id.txt_eange_km);
                                TextView txt_waktu_awal = dialoglayout.findViewById(R.id.txt_waktu_awal);
                                TextView txt_waktu_akhir = dialoglayout.findViewById(R.id.txt_waktu_akhir);
                                TextView txt_status = dialoglayout.findViewById(R.id.txt_status);
                                TextView txt_kegiatan = dialoglayout.findViewById(R.id.txt_jns_kegiatan);
                                TextView txt_keternagan = dialoglayout.findViewById(R.id.txt_keterangan);

                                Button btn_close = dialoglayout.findViewById(R.id.btn_close);

                                title_kondisi_lalin.setText("Pemeliharaan");
                                txt_nm_ruas.setText(nama_ruas);
                                txt_nmKm.setText(km);
                                txt_jalur_lajur.setText(jalur+" / "+lajur);
                                txt_range_km.setText(range_km_pekerjaan);
                                txt_waktu_awal.setText(waktu_awal);
                                txt_waktu_akhir.setText(waktu_akhir);
                                txt_status.setText(ket_status);
                                txt_kegiatan.setText(ket_jenis_kegiatan);
                                txt_keternagan.setText(keterangan_detail);

                                final AlertDialog  alertDialog = alert.create();
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                btn_close.setOnClickListener(v -> {
                                    alertDialog.cancel();
//                                    serviceKondisiLalin.removeCallbacksHandlePemeliharaan();
                                });
                                alertDialog.show();
                                if (alertDialogLineToll != null){
                                    alertDialogLineToll.cancel();
                                }
                            }
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

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excuterekayasalalin(paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if(response.body() != null) {
                        JSONObject dataRes = new JSONObject(response.body().toString());
                        if (dataRes.getString("status").equals("1")){
                            FeatureCollection featureCollectionline = FeatureCollection.fromJson(dataRes.getString("linedata"));
                            initLineRekLalin(featureCollectionline, style,mapboxMap);

                            FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                            mapboxMap.getStyle(style1 -> {
                                style1.addSource(new GeoJsonSource("rekayasalalin", featureCollection.toJson()));
                                style1.addLayer(new SymbolLayer("finalrekayasalalin", "rekayasalalin").withProperties(
                                        PropertyFactory.iconImage(get("icon")),
                                        PropertyFactory.iconAllowOverlap(false),
                                        PropertyFactory.textAllowOverlap(false),
                                        PropertyFactory.textField(get("ket_jenis_kegiatan")),
                                        PropertyFactory.textRadialOffset(1.5f),
                                        PropertyFactory.textAnchor(ngisor),
                                        PropertyFactory.textJustify(justify),
                                        PropertyFactory.textSize(8f),
                                        PropertyFactory.iconSize(0.7f)
                                ));
                            });

                            SymbolLayer symbolrekayasalain = styleSet.getLayerAs("finalrekayasalalin");
                            if (userSetting.getGangguanLalin().equals(UserSetting.onSet)){
                                if (symbolrekayasalain != null){
                                    symbolrekayasalain.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolrekayasalain != null){
                                    symbolrekayasalain.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }

                            LineLayer linerekayasalalin = styleSet.getLayerAs("finalrekayasalalinline");
                            if (userSetting.getGangguanLalin().equals(UserSetting.onSet)){
                                if (symbolrekayasalain != null){
                                    linerekayasalalin.setProperties(PropertyFactory.visibility(Property.VISIBLE));
                                }
                            }else{
                                if (symbolrekayasalain != null){
                                    linerekayasalalin.setProperties(PropertyFactory.visibility(Property.NONE));
                                }
                            }

//                            serviceKondisiLalin.handleRunServiceRekayasaLalin(style, mapboxMap, "finalrekayasalalin", "rekayasalalin", "finalrekayasalalinline","rekayasalalinline",scope);

                            mapboxMap.addOnMapClickListener(pointvms -> {
                                PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(pointvms);
                                List<Feature> featuresvsms = mapboxMap.queryRenderedFeatures(screenPointvms, "finalrekayasalalin");
                                if (!featuresvsms.isEmpty()) {
                                    Feature selectedFeaturevms = featuresvsms.get(0);
                                    String nama_ruas = selectedFeaturevms.getStringProperty("nama_ruas");
                                    String km = selectedFeaturevms.getStringProperty("km");
                                    String jalur = selectedFeaturevms.getStringProperty("jalur");
                                    String lajur = selectedFeaturevms.getStringProperty("lajur");
                                    String range_km_pekerjaan = selectedFeaturevms.getStringProperty("range_km_pekerjaan");
                                    String waktu_awal = selectedFeaturevms.getStringProperty("waktu_awal");
                                    String waktu_akhir = selectedFeaturevms.getStringProperty("waktu_akhir");
                                    String ket_jenis_kegiatan = selectedFeaturevms.getStringProperty("ket_jenis_kegiatan");
                                    String keterangan_detail = selectedFeaturevms.getStringProperty("keterangan_detail");
                                    String ket_status = selectedFeaturevms.getStringProperty("ket_status");


                                    AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
                                    alert.setCancelable(false);

                                    LayoutInflater inflater = getLayoutInflater();
                                    View dialoglayout = inflater.inflate(R.layout.custom_dialog_pemeliharaan, null);
                                    alert.setView(dialoglayout);

                                    TextView title_kondisi_lalin = dialoglayout.findViewById(R.id.title_kondisi_lalin);
                                    TextView txt_nm_ruas = dialoglayout.findViewById(R.id.txt_nm_ruas);
                                    TextView txt_nmKm = dialoglayout.findViewById(R.id.txt_nmKm);
                                    TextView txt_jalur_lajur = dialoglayout.findViewById(R.id.txt_jalur_lajur);
                                    TextView txt_range_km = dialoglayout.findViewById(R.id.txt_eange_km);
                                    TextView txt_waktu_awal = dialoglayout.findViewById(R.id.txt_waktu_awal);
                                    TextView txt_waktu_akhir = dialoglayout.findViewById(R.id.txt_waktu_akhir);
                                    TextView txt_status = dialoglayout.findViewById(R.id.txt_status);
                                    TextView txt_kegiatan = dialoglayout.findViewById(R.id.txt_jns_kegiatan);
                                    TextView txt_keternagan = dialoglayout.findViewById(R.id.txt_keterangan);

                                    Button btn_close = dialoglayout.findViewById(R.id.btn_close);

                                    title_kondisi_lalin.setText("Rekayasa Lalin");
                                    txt_nm_ruas.setText(nama_ruas);
                                    txt_nmKm.setText(km);
                                    txt_jalur_lajur.setText(jalur+" / "+lajur);
                                    txt_range_km.setText(range_km_pekerjaan);
                                    txt_waktu_awal.setText(waktu_awal);
                                    txt_waktu_akhir.setText(waktu_akhir);
                                    txt_status.setText(ket_status);
                                    txt_kegiatan.setText(ket_jenis_kegiatan);
                                    txt_keternagan.setText(keterangan_detail);

                                    final AlertDialog  alertDialog = alert.create();
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    btn_close.setOnClickListener(v -> {
                                        alertDialog.cancel();
//                                        serviceKondisiLalin.removeCallbacksHandleRekayasa();
                                    });

                                    alertDialog.show();
                                    if (alertDialogLineToll != null){
                                        alertDialogLineToll.cancel();
                                    }
                                }
                                return false;
                            });

                        }else{
                            Log.d("Err DB", response.body().toString());
                        }
                    }else{
                        Log.d("adader", response.body().toString());
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
        if (userSetting.getCctv().equals(UserSetting.onSet)){
            initLoadCCTV(style, mapboxMap);
        }
        if (userSetting.getVms().equals(UserSetting.onSet)){
            initLoadVMS(style, mapboxMap);
        }
    }

    private String getFile(String nmFile){
        String retDatajson = null;
        try {
            String path = getApplicationContext().getExternalFilesDir(null) + "/datajid/";
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

    private void delSession() {
        loadingDialog = new LoadingDialog(Home.this);
        loadingDialog.showLoadingDialog("Loading...");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", username);

        ReqInterface serviceAPI = ApiClient.getClient();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBackPressed() {
        serviceKondisiLalin.removeCallbacksHandle();
        serviceRealtime.removeCallbacksHandle();
        finish();
    }

    private void menuBottomnavbar(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.peta);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    serviceKondisiLalin.removeCallbacksHandle();
                    serviceRealtime.removeCallbacksHandle();
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.cctv:
                    startActivity(new Intent(getApplicationContext(), Cctv.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.antrian_gerbang:
                    Toast.makeText(getApplicationContext(), "Sedang tahap pembuatan !", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.realtime_lalin:
                    Toast.makeText(getApplicationContext(), "Sedang tahap pembuatan !", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        });
    }
}
