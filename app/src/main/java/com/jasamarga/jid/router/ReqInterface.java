package com.jasamarga.jid.router;

import com.google.gson.JsonObject;
import com.jasamarga.jid.models.DataGate;
import com.jasamarga.jid.models.DataJalurModel;
import com.jasamarga.jid.models.DataPemeliharaanModel;
import com.jasamarga.jid.models.DataWaterLevel;
import com.jasamarga.jid.models.KegiatanModel;
import com.jasamarga.jid.models.ModelEventGangguan;
import com.jasamarga.jid.models.ModelEventLalin;
import com.jasamarga.jid.models.ModelGangguan;
import com.jasamarga.jid.models.ModelGangguanLalin;
import com.jasamarga.jid.models.ModelRegion;
import com.jasamarga.jid.models.ModelRekayasa;
import com.jasamarga.jid.models.ModelRuas;
import com.jasamarga.jid.models.ModelUsers;
import com.jasamarga.jid.models.PemeliharaanData;
import com.jasamarga.jid.models.RealtimeTrModel;
import com.jasamarga.jid.models.model_notif.ControllerNotif;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ReqInterface {
    //ENDPOINT BARUU
    @GET("auth/v1/logout/")
    Call<JsonObject> logout(
            @Header("Authorization") String authHeader);
    @GET("antrian_gerbang/v1/getDataMenuju")
    Call<DataGate> getAntrianGMenuju(
            @Header("Authorization") String authHeader);

    @GET("antrian_gerbang/v1/getDataDari")
    Call<DataGate> getAntrianDari(
            @Header("Authorization") String authHeader);
    @GET("/segmentation/v1/region")
    Call<ModelRegion> getRegionFilter(@Header("Authorization") String authHeader);
    @GET("/segmentation/v1/ruas")
    Call<ModelRuas> getRuasFilter(@Header("Authorization") String authHeader);
    @GET("dashboard_pemeliharaan/v1/getPemeliharaan")
    Call<PemeliharaanData> getDataPemeliharaan(
            @Query("filter_ruas") String ruas,
            @Query("waktu") String waktu,
            @Query("dari") String dari,
            @Query("sampai") String sampai,
            @Header("Authorization") String authHeader
    );
    @GET("pemeliharaan/v1/getPemeliharaan")
    Call<ModelEventLalin> getEventPemeliharaan(
            @Query("waktu_dari") String dari,
            @Query("waktu_sampai") String sampai,
            @Query("waktu") String waktu,
            @Header("Authorization") String authHeader
    );

    @GET("gangguan_lalin/v1/getGangguan")
    Call<ModelEventGangguan> getEventGangguan(
            @Query("waktu_dari") String dari,
            @Query("waktu_sampai") String sampai,
            @Query("waktu") String waktu,
            @Header("Authorization") String authHeader
    );

    @GET("rekayasa/v1/getRekayasa")
    Call<ModelEventLalin> getEventRekayasa(
            @Query("waktu_dari") String dari,
            @Query("waktu_sampai") String sampai,
            @Query("waktu") String waktu,
            @Header("Authorization") String authHeader
    );
    @GET("water_level_sensor/v1/getWaterLevelSensor")
    Call<DataWaterLevel> getWaterLevet(@Header("Authorization") String auth);
    @GET("realtime_lalin/v1/getDataSegment")
    Call<RealtimeTrModel> getDataRT(@Query("id_ruas") String id,
                                    @Query("jalur") String jalur,
                                    @Header("Authorization") String auth);

    @GET("mobile/v1/version-check")
    Call<JsonObject> cekVersion(
            @Query("version") String version
    );

    @GET("dashboard_pemeliharaan/v1/getDataJalur")
    Call<DataJalurModel> getDataPJalur(
            @Query("ruas") String ruas,
            @Query("waktu") String waktu,
            @Query("dari") String dari,
            @Query("sampai") String sampai,
            @Header("Authorization") String authHeader
    );
    @GET("pemeliharaan/v1/getPemeliharaan")
    Call<DataPemeliharaanModel> getPemeliharaan(
            @Query("filter_ruas") String ruas,
            @Query("filter_km") String km,
            @Query("filter_jalur") String jalur,
            @Query("filter_kegiatan") String kegiatan,
            @Query("filter_status") String status,
            @Query("dari") String waktuDari,
            @Query("sampai") String waktuSampai,
            @Query("waktu") String filterWaktu,
            @Header("Authorization") String authHeader
    );
    @GET("gangguan_lalin/v1/getGangguan")
    Call<ModelGangguanLalin> getGangguanLalin(
            @Query("filter_ruas") String ruas,
            @Query("filter_km") String km,
            @Query("filter_jalur") String jalur,
            @Query("filter_kegiatan") String kegiatan,
            @Query("filter_status") String status,
            @Query("dari") String waktuDari,
            @Query("sampai") String waktuSampai,
            @Query("waktu") String filterWaktu,
            @Header("Authorization") String authHeader
    );
    @GET("dashboard_pemeliharaan/v1/getKegiatanTot")
    Call<KegiatanModel> getDataPKegiatan(
            @Query("ruas") String ruas,
            @Query("waktu") String waktu,
            @Query("dari") String dari,
            @Query("sampai") String sampai,
            @Header("Authorization") String authHeader
    );
    @GET("dashboard_lalin/v1/countGangguan")
    Call<ModelGangguan> getDataCountGangguan(@Header("Authorization") String authHeader);
    @GET("dashboard_lalin/v1/countRekayasa")
    Call<ModelRekayasa> getDataCountRekayasa(@Header("Authorization") String authHeader);

    @POST("auth/v2/login")
    Call<ModelUsers> login(@Body JsonObject postData);
    @PATCH("auth/v1/refresh-session")
    Call<JsonObject> refreshSession(@Header("Authorization") String authHeader);

    //ENDPOINT LAMA

    @PUT("auth_login/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutelogin(@Body JsonObject postData);
    @PUT("add_session_dev/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excuteaddsession(@Body JsonObject postData);

    @PUT("del_session/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutedelsession(@Body JsonObject postData);
    //MAPS
    @POST("showcctv/")
    Call<JsonObject> excutegetcctv(@Body JsonObject postData,@Header("Authorization") String token);

    @POST("showvms/")
    Call<JsonObject> excutegetvms(@Body JsonObject postData,@Header("Authorization") String token);

    @GET("jalantoll/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutejalantoll();
    @GET("map/tol.json")
    Call<JsonObject> excutelinetoll();

    @GET("showlalin/")
    Call<JsonObject> excutelalin(@Header("Authorization") String token);

    @POST("gangguanlalin/")
    Call<JsonObject> excutegangguanlalin(@Body JsonObject postData,@Header("Authorization") String token);

    @POST("pemeliharaan/")
    Call<JsonObject> excutepemeliharaan(@Body JsonObject postData,@Header("Authorization") String token);

    @POST("rekayasalalin/")
    Call<JsonObject> excuterekayasalalin(@Body JsonObject postData,@Header("Authorization") String token);

    @PUT("scopeanditem/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excuteupdatepravilage(@Body JsonObject postData);

    @POST("cek_versi/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutecekversi(@Body JsonObject postData);
    @GET("update/toll")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excuteupdatetol();

    @POST("kejadian_lalin_by_ruas/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutekejadianlalin(@Body JsonObject postData);

    @PUT("data_ruas/")
    Call<JsonObject> excutedataruas(@Body JsonObject postData, @Header("Authorization") String token);

    @PUT("data/segment/")
    Call<JsonObject> excutedatasegment(@Body JsonObject postData, @Header("Authorization") String token);

    @PUT("segment/cctv/")
    Call<JsonObject> excutedatacctvseg(@Body JsonObject postData, @Header("Authorization") String token);
    @POST("data/bataskm/")
    Call<JsonObject> excutbataskm(@Header("Authorization") String authorization, @Body JsonObject postData);

    @GET("data/jalan_penghubung/")
    Call<JsonObject> excutejalanpenghubung(@Header("Authorization") String authorization);

    @POST("data/gerbangtol/")
    Call<JsonObject> excutegerbangtol(@Header("Authorization") String authorization, @Body JsonObject postData);
    @GET("map_display/v1/lokasi/gerbang")
    Call<JsonObject> excutegerbangsisputol(@Header("Authorization") String authorization);


    @POST("data/rest_area/")
    Call<JsonObject> excuterestarea(@Header("Authorization") String authorization, @Body JsonObject postData);

    @POST("data/rougnesindex/")
    Call<JsonObject> excutrougnesindex(@Header("Authorization") String authorization, @Body JsonObject postData);

    @POST("data/rtms/")
    Call<JsonObject> excutertms(@Header("Authorization") String authorization, @Body JsonObject postData);

    @POST("data/rtms2/")
    Call<JsonObject> excutertms2(@Header("Authorization") String authorization, @Body JsonObject postData);

    @POST("data/speed/")
    Call<JsonObject> excutespeed(@Header("Authorization") String authorization, @Body JsonObject postData);

    @POST("data/water_level/")
    Call<JsonObject> excutewaterlevel(@Header("Authorization") String authorization, @Body JsonObject postData);

    @GET("data/pompa/")
    Call<JsonObject> excutepompa(@Header("Authorization") String authorization);

    @POST("data/wim/")
    Call<JsonObject> excutewim(@Header("Authorization") String authorization, @Body JsonObject postData);

    @GET("data/bike/")
    Call<JsonObject> excutebike(@Header("Authorization") String authorization);

    @POST("data/gps_kendaraan/")
    Call<JsonObject> excutegpskendaraan(@Header("Authorization") String authorization, @Body JsonObject postData);

    @POST("data/radar/")
    Call<JsonObject> excuteradar(@Header("Authorization") String authorization, @Body JsonObject postData);

    @GET("data/midas/")
    Call<JsonObject> excutemidas(@Header("Authorization") String authorization);
    @FormUrlEncoded
    @POST("push_notif/getAll/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<ControllerNotif> excutenotif(@Field("platform") String platform,@Field("limit") String limit);

    @POST("push_notif/readedByUser/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excuteRead(@Body JsonObject postData);

    @POST("log_user/logging")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutelogactivity(@Body JsonObject postData);

}
