package com.jasamarga.jid.views;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.se.omapi.Session;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.jasamarga.jid.Sessionmanager;
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
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.jasamarga.jid.components.PopupCctv;
import com.jasamarga.jid.Dashboard;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.R;
import com.jasamarga.jid.models.CctvSegmentModel;
import com.jasamarga.jid.models.RuasModel;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.ServiceFunction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsNew extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private MapView mapView;
    private ArrayList<CctvSegmentModel> models;
    private ArrayList<RuasModel> item;
    private LoadingDialog loadingDialog;
    private MaterialButton btnMap;

    MapboxMap mapbox;
    BottomNavigationView bottomNavigationView;
    Intent intent;
    String judulRuas,id_ruas,lat,lon,datalalin,ngisor,justify;
    Handler handler;
    private ReqInterface serviceAPI;
    private Style style;
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_new_maps);
        floatingActionButton = findViewById(R.id.filter);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        mapView = findViewById(R.id.mapView);
        btnMap = findViewById(R.id.btnMap);
        models = new ArrayList<>();
        intent = getIntent();
        Sessionmanager sessionmanager = new Sessionmanager(this);
        token = sessionmanager.getUserDetails().get(Sessionmanager.nameToken);
        datalalin = ServiceFunction.getFile("lalin.json",this);
        judulRuas = intent.getStringExtra("judul_ruas");
        id_ruas = intent.getStringExtra("id_ruas");

        handler = new Handler();
        ngisor = "top";
        justify = "center";
        setClick();
        initMaps();
        setBottomNav();
//        getRuas();


    }
    private void initMaps(){
        loadingDialog = new LoadingDialog(MapsNew.this);
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
                    MapsNew.this.style = style;
                    UiSettings uiSettings = mapboxMap.getUiSettings();
                    uiSettings.setCompassEnabled(false);
                    uiSettings.setRotateGesturesEnabled(false);
                    uiSettings.setLogoEnabled(false);
                    uiSettings.setAttributionEnabled(false);
                    ServiceFunction.iconImage(style,MapsNew.this);
                    ServiceFunction.initLineToll(style,datalalin);
                    initCctv();

                    loadingDialog.hideLoadingDialog();
                }
            });
            mapbox = mapboxMap;
        });
    }

    private void initCctv(){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", id_ruas);

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutegetcctv(paramsIdruas,token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    String status = dataRes.getString("status");
                    if (dataRes.getString("status").equals("1")) {
                        FeatureCollection featureCollectioncctv = FeatureCollection.fromJson(dataRes.getString("data"));
                        style.addSource(new GeoJsonSource("cctv", featureCollectioncctv.toJson()));
                        style.addLayer(new SymbolLayer("finalcctv", "cctv").withProperties(
                                PropertyFactory.iconImage(get("poi")),
                                PropertyFactory.textAllowOverlap(false),
                                PropertyFactory.textField(get("nama")),
                                PropertyFactory.textAnchor(ngisor),
                                PropertyFactory.textJustify(justify),
                                PropertyFactory.textRadialOffset(1.8f),
                                PropertyFactory.textSize(8f)
                        ));

                        for(int i = 0; i < Objects.requireNonNull(featureCollectioncctv.features()).size(); i++){
                            Feature selectedFeaturecctv = featureCollectioncctv.features().get(i);
                            CctvSegmentModel cctvSegmentModel = new CctvSegmentModel();
                            selectedFeaturecctv.geometry();
                            String cabang = selectedFeaturecctv.getStringProperty("cabang");
                            String nama = selectedFeaturecctv.getStringProperty("nama");
                            String nama_ruas = selectedFeaturecctv.getStringProperty("nama_ruas_2");
                            String key_id = selectedFeaturecctv.getStringProperty("key_id");

                            cctvSegmentModel.setCabang(cabang);
                            cctvSegmentModel.setKm(nama);
                            cctvSegmentModel.setNamaSegment(nama_ruas);
                            cctvSegmentModel.setKeyId(key_id);
                            cctvSegmentModel.setStatus(status);
                            models.add(cctvSegmentModel);

                        }
                        PopupCctv.display(getSupportFragmentManager(),models,status,handler,0);
                        clickMarker(status);
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

    private void clickMarker(String status){
        mapbox.addOnMapClickListener(point -> {
            PointF screenPointcctv = mapbox.getProjection().toScreenLocation(point);
            List<Feature> featurescctv = mapbox.queryRenderedFeatures(screenPointcctv, "finalcctv");
            if(!featurescctv.isEmpty()){
                Feature selectedFeaturecctv = featurescctv.get(0);
                int pos = 0;
                List<String> namaRuas = new ArrayList<>();
                for(CctvSegmentModel item : models){
                    namaRuas.add(item.getKm());
                }

                pos = namaRuas.indexOf(selectedFeaturecctv.getStringProperty("nama"));
                PopupCctv.display(getSupportFragmentManager(),models,status,handler,pos);
            }else {
                Toast.makeText(getApplicationContext(),"Gagal Memuat Data",Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }


    private void setBottomNav(){
        bottomNavigationView.setSelectedItemId(R.id.peta);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    overridePendingTransition(0,0);
                    handler.removeCallbacksAndMessages(null);
                    finish();
                    return true;
                case R.id.cctv:
                    startActivity(new Intent(getApplicationContext(), Cctv.class));
                    overridePendingTransition(0,0);
                    handler.removeCallbacksAndMessages(null);
                    finish();
                    return true;
                case R.id.antrian_gerbang:
                    startActivity(new Intent(getApplicationContext(), Antrian.class));
                    overridePendingTransition(0,0);
                    handler.removeCallbacksAndMessages(null);
                    finish();
                    return  true;
                case R.id.realtime_lalin:
                    Toast.makeText(getApplicationContext(), "Sedang tahap pembuatan !", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        });
    }
    private void setClick(){
        btnMap.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), Maps.class);
            overridePendingTransition(0,0);
            handler.removeCallbacksAndMessages(null);
            startActivity(intent);
            finish();
        });
        floatingActionButton.setOnClickListener(v->{

        });
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
            handler.removeCallbacksAndMessages(null);
            finish();
        });
        alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
        alertDialogBuilder.show();

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
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
