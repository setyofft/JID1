package com.jasamarga.jid;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;

import android.content.Context;
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
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ReqInterface;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceRealtime {

    public ReqInterface serviceAPI;

    private Context context;
    private  Style styleCCTV, styleVMS, styleLalin;
    String token;
    private Handler handler_service_cctv, handler_service_vms, handler_service_lalin;

    public ServiceRealtime(Context current){
        this.context = current;
        Sessionmanager sessionmanager = new Sessionmanager(current);
        token = sessionmanager.getUserDetails().get(Sessionmanager.nameToken);
    }

    public void UpdateDataCCTV(Style style, MapboxMap mapboxMap, String layar_id, String source_id, String scope){
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutegetcctv(paramsIdruas,token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollectioncctv = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style1 -> {
                            ((GeoJsonSource) style1.getSource(source_id)).setGeoJson(featureCollectioncctv.toJson());
                            SymbolLayer symbolLayer = style1.getLayerAs(layar_id);
                            symbolLayer.setProperties(
                                    PropertyFactory.iconImage(get("poi"))
                            );
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

    public void UpdateDataVMS(Style style, MapboxMap mapboxMap, String layar_id, String source_id, String scope){
        styleVMS = style;
        JsonObject paramsIdruas = new JsonObject();
        paramsIdruas.addProperty("id_ruas", scope);

        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutegetvms(paramsIdruas,token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());

                    if (dataRes.getString("status").equals("1")){
                        FeatureCollection featureCollectioncctv = FeatureCollection.fromJson(dataRes.getString("data"));
                        mapboxMap.getStyle(style1 -> {
                            ((GeoJsonSource) style1.getSource(source_id)).setGeoJson(featureCollectioncctv.toJson());
                            SymbolLayer symbolLayer = style1.getLayerAs(layar_id);
                            symbolLayer.setProperties(
                                    PropertyFactory.iconImage(get("icon"))
                            );
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
    public void UpdateDataLalin(Style style, MapboxMap mapboxMap, String layar_id, String source_id){
        serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutelalin(token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    Log.d("RSdd", response.toString());
                    if (response.body() != null){
                        JSONObject dataRes = new JSONObject(response.body().toString());
                        Log.d("dataLalin", response.body().toString());
                        if (dataRes.getString("status").equals("1")){
                            FeatureCollection featureCollectioncctv = FeatureCollection.fromJson(dataRes.getString("data"));
                            mapboxMap.getStyle(style1 -> {
                                ((GeoJsonSource) style1.getSource(source_id)).setGeoJson(featureCollectioncctv.toJson());
                                LineLayer lineLayer = style1.getLayerAs(layar_id);
                                lineLayer.setProperties(
                                        PropertyFactory.lineColor(
                                                match(get("color"), rgb(0, 0, 0),
                                                        stop("#ffcc00", "#ffcc00"),
                                                        stop("#ff0000", "#ff0000"),
                                                        stop("#bb0000", "#bb0000"),
                                                        stop("#440000", "#440000"),
                                                        stop("#00ff00", "#00ff00")
                                                ))
                                );
                            });

                        }else{
                            Log.d("Err DB", response.body().toString());
                        }
                    }else {
                        Log.d("dataLalin", response.toString());

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

    public void handleRunServiceCCTV(Style style, MapboxMap mapboxMap, String layar_id, String source_id, String scope){
        handler_service_cctv = new Handler();
        handler_service_cctv.postDelayed(new Runnable(){
            public void run(){
                Log.d("UPDATECCTV", "Started Service Update CCTV...");
                UpdateDataCCTV(style, mapboxMap, layar_id, source_id, scope);
                handler_service_cctv.postDelayed(this, 60000);
            }
        }, 60000);
    }

    public void handleRunServiceVMS(Style style, MapboxMap mapboxMap, String layar_id, String source_id, String scope){
        handler_service_vms = new Handler();
        handler_service_vms.postDelayed(new Runnable(){
            public void run(){
                Log.d("UPDATEVMS", "Started Service Update VMS...");
                UpdateDataVMS(style, mapboxMap, layar_id, source_id, scope);
                handler_service_vms.postDelayed(this, 60000);
            }
        }, 60000);
    }

    public void handleRunServiceLalin(Style style, MapboxMap mapboxMap, String layar_id, String source_id){
        handler_service_lalin = new Handler();
        handler_service_lalin.postDelayed(new Runnable(){
            public void run(){
                Log.d("UPDATELALIN", "Started Service Update LALIN...");
                UpdateDataLalin(style, mapboxMap, layar_id, source_id);
                handler_service_lalin.postDelayed(this, 120000);
            }
        }, 120000);
    }

    public void removeCallbacksHandle(){
        if (handler_service_cctv != null){
            handler_service_cctv.removeCallbacksAndMessages(null);
        }
        if (handler_service_vms != null){
            handler_service_vms.removeCallbacksAndMessages(null);
        }
        if (handler_service_lalin != null){
            handler_service_lalin.removeCallbacksAndMessages(null);
        }

    }

    public void removeCallbacksHandleCCTVservice(){
        if (handler_service_cctv != null){
            handler_service_cctv.removeCallbacksAndMessages(null);
        }
    }

    public void removeCallbacksHandleVMSservice(){
        if (handler_service_vms != null){
            handler_service_vms.removeCallbacksAndMessages(null);
        }
    }

    public void removeCallbacksHandleLalinservice(){
        if (handler_service_lalin != null){
            handler_service_lalin.removeCallbacksAndMessages(null);
        }

    }

}
