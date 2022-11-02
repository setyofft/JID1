package com.pim.jid.router;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ReqInterface {

    @PUT("auth_login/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutelogin(@Body JsonObject postData);

    @PUT("add_session/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excuteaddsession(@Body JsonObject postData);

    @PUT("del_session/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutedelsession(@Body JsonObject postData);

    @POST("showcctv/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutegetcctv(@Body JsonObject postData);

    @POST("showvms/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutegetvms(@Body JsonObject postData);

    @GET("jalantoll/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutejalantoll();

    @GET("showlalin/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutelalin();

    @POST("gangguanlalin/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutegangguanlalin(@Body JsonObject postData);

    @POST("pemeliharaan/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutepemeliharaan(@Body JsonObject postData);

    @POST("rekayasalalin/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excuterekayasalalin(@Body JsonObject postData);

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

    @POST("kejadian_lalin_by_ruas/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutekejadianlalin(@Body JsonObject postData);

    @PUT("data_ruas/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutedataruas(@Body JsonObject postData);

    @PUT("data/segment/")
    @Headers({
            "Content-Type: application/json", "Authorization: 954063685"
    })
    Call<JsonObject> excutedatasegment(@Body JsonObject postData);

    @PUT("segment/cctv/")
    @Headers({
            "Content-Type: application/json", "Authorization: 954063685"
    })
    Call<JsonObject> excutedatacctvseg(@Body JsonObject postData);

    @POST("data/bataskm/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutbataskm(@Body JsonObject postData);

    @GET("data/jalan_penghubung/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutejalanpenghubung();

    @POST("data/gerbangtol/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutegerbangtol(@Body JsonObject postData);

    @POST("data/rest_area/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excuterestarea(@Body JsonObject postData);

    @POST("data/rougnesindex/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutrougnesindex(@Body JsonObject postData);

    @POST("data/rtms/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutertms(@Body JsonObject postData);

    @POST("data/rtms2/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutertms2(@Body JsonObject postData);

    @POST("data/speed/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutespeed(@Body JsonObject postData);

    @POST("data/water_level/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutewaterlevel(@Body JsonObject postData);

    @GET("data/pompa/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutepompa();

    @POST("data/wim/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutewim(@Body JsonObject postData);

    @GET("data/bike/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutebike();

    @POST("data/gps_kendaraan/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutegpskendaraan(@Body JsonObject postData);

    @POST("data/radar/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excuteradar(@Body JsonObject postData);

    @GET("data/midas/")
    @Headers({
            "Content-Type: application/json", "Authorization: 2345391662"
    })
    Call<JsonObject> excutemidas();

}
