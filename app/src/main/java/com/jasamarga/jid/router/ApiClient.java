package com.jasamarga.jid.router;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static ReqInterface service;
    private static ReqInterface service2;

    public static ReqInterface getClient() {
        if (service == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://jid.jasamarga.com/client-api/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            service = retrofit.create(ReqInterface.class);
        }
        return service;
    }
    public static ReqInterface getNoClient() {
        if (service2 == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://jid.jasamarga.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            service2 = retrofit.create(ReqInterface.class);
        }
        return service2;
    }
}
