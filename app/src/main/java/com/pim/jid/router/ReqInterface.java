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

}
