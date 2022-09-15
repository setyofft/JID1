package com.pim.jid.service;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.google.gson.JsonObject;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.pim.jid.R;
import com.pim.jid.router.ApiClient;
import com.pim.jid.router.ReqInterface;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceKondisiLalin {
    public ReqInterface serviceAPI;

    private Context context;
    private Style styleGangguan, stylePemeliharaan;
    public Handler handler_service_gangguan, handler_service_pemeliharaan, handler_service_rekayasa;

    public ServiceKondisiLalin(Context current){
        this.context = current;
    }

    public void UpdateGangguan(Style style, MapboxMap mapboxMap, String layar_id, String source_id, String scope){
        styleGangguan = style;
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
                        if (featureCollection.features() != null) {
                            mapboxMap.getStyle(style1 -> {
                                ((GeoJsonSource) style1.getSource(source_id)).setGeoJson(featureCollection.toJson());
                                SymbolLayer symbolLayer = style1.getLayerAs(layar_id);
                                symbolLayer.setProperties(
                                        PropertyFactory.iconImage("gangguan_lalin")
                                );
                            });
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

    public void handleRunServiceGangguan(Style style, MapboxMap mapboxMap, String layar_id, String source_id, String scope){
        handler_service_gangguan = new Handler();
        handler_service_gangguan.postDelayed(new Runnable(){
            public void run(){
                Log.d("UpdateGangguan", "Started Service Update gangguan...");
                UpdateGangguan(style, mapboxMap, layar_id, source_id, scope);
                handler_service_gangguan.postDelayed(this, 60000);
            }
        }, 60000);
    }

    public void UpdatePemeliharaan(Style style, MapboxMap mapboxMap, String layar_id, String source_id, String scope){
        stylePemeliharaan = style;
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
                        if (featureCollection.features() != null) {
                            mapboxMap.getStyle(style1 -> {
                                ((GeoJsonSource) style1.getSource(source_id)).setGeoJson(featureCollection.toJson());
                                SymbolLayer symbolLayer = style1.getLayerAs(layar_id);
                                symbolLayer.setProperties(
                                        PropertyFactory.iconImage("pemeliharaanimg")
                                );
                            });
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

    public void handleRunServicePemeliharaan(Style style, MapboxMap mapboxMap, String layar_id, String source_id, String scope){
        handler_service_pemeliharaan = new Handler();
        handler_service_pemeliharaan.postDelayed(new Runnable(){
            public void run(){
                Log.d("UpdatePemeliharaan", "Started Service Update pemeriharaan...");
                UpdatePemeliharaan(style, mapboxMap, layar_id, source_id, scope);
                handler_service_pemeliharaan.postDelayed(this, 60000);
            }
        }, 60000);
    }

    public void UpdateRekayasaLalin(Style style, MapboxMap mapboxMap, String layar_id, String source_id, String layer_id_line, String source_id_line, String scope){
        stylePemeliharaan = style;
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excuterekayasalalin(paramsIdruas);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollection = FeatureCollection.fromJson(dataRes.getString("data"));
                        if (featureCollection.features() != null) {
                            mapboxMap.getStyle(style1 -> {
                                ((GeoJsonSource) style1.getSource(source_id)).setGeoJson(featureCollection.toJson());
                                SymbolLayer symbolLayer = style1.getLayerAs(layar_id);
                                symbolLayer.setProperties(
                                        PropertyFactory.iconImage("rekayasalalinimg")
                                );
                            });
                        }
                        FeatureCollection featureCollectionline = FeatureCollection.fromJson(dataRes.getString("linedata"));
                        if (featureCollection.features() != null) {
                            mapboxMap.getStyle(style1 -> {
                                ((GeoJsonSource) style1.getSource(source_id_line)).setGeoJson(featureCollectionline.toJson());
                                LineLayer lineLayer = style1.getLayerAs(layer_id_line);
                                lineLayer.setProperties(
                                        PropertyFactory.lineColor(Color.RED),
                                        PropertyFactory.lineWidth(1f)
                                );
                            });
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

    public void handleRunServiceRekayasaLalin(Style style, MapboxMap mapboxMap, String layar_id, String source_id, String layer_id_line, String source_id_line, String scope){
        handler_service_rekayasa = new Handler();
        handler_service_rekayasa.postDelayed(new Runnable(){
            public void run(){
                Log.d("UpdateRekayasaLalin", "Started Service Update rekayasa lalin...");
                UpdateRekayasaLalin(style, mapboxMap, layar_id, source_id, layer_id_line, source_id_line, scope);
                handler_service_rekayasa.postDelayed(this, 60000);
            }
        }, 60000);
    }

    public void removeCallbacksHandle(){
        if (handler_service_gangguan != null){
            handler_service_gangguan.removeCallbacksAndMessages(null);
        }
        if (handler_service_pemeliharaan != null){
            handler_service_pemeliharaan.removeCallbacksAndMessages(null);
        }
        if (handler_service_rekayasa != null){
            handler_service_rekayasa.removeCallbacksAndMessages(null);
        }
    }

    public void removeCallbacksHandleGangguanLalin(){
        if (handler_service_gangguan != null) {
            handler_service_gangguan.removeCallbacksAndMessages(null);
        }
    }

    public void removeCallbacksHandlePemeliharaan(){
        if (handler_service_pemeliharaan != null){
            handler_service_pemeliharaan.removeCallbacksAndMessages(null);
        }
    }

    public void removeCallbacksHandleRekayasa(){
        if (handler_service_rekayasa != null){
            handler_service_rekayasa.removeCallbacksAndMessages(null);
        }
    }
}
