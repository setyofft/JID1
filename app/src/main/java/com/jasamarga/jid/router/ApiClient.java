package com.jasamarga.jid.router;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.os.BuildCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jasamarga.jid.Sessionmanager;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static ReqInterface service;
    private static ReqInterface service3;
    private static ReqInterface service2;
    static String BASE_URL = "https://api-provider-jid.jasamarga.com/";
    static String DEV_URL = "https://jid-fe-dev.jasamarga.com/";
    static String DEV_URL_CLIENT = "https://jid-fe-dev.jasamarga.com/";
    static String BASE_URL_CLIEN = "https://api-provider-jid.jasamarga.com/client-api/";

    public static ReqInterface getServiceNew(Activity context) {
        @SuppressLint("CustomX509TrustManager") TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .addInterceptor(new AuthInterceptor(context))
                    .addInterceptor(chain -> {
                        String userAgent = getUserAgent();
                        Request originalRequest = chain.request();
                        Request requestWithUserAgent = originalRequest.newBuilder()
                                .header("User-Agent", userAgent)
                                .header("Device", "Android")
                                .build();
                        return chain.proceed(requestWithUserAgent);
                    })
                    .build();
            if (service3 == null) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(DEV_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(okHttpClient)
                        .build();

                service3 = retrofit.create(ReqInterface.class);
            }
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }

        return service3;
    }
    public static String getUserAgent() {
        String version = System.getProperty("http.agent");
        return String.format("Dalvik/%s (Linux; U; Android %s; %s Build/%s)",
                version,
                Build.VERSION.RELEASE,
                Build.MODEL,
                Build.ID);
    }
    public static ReqInterface getClient(Activity context) {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(new AuthInterceptor(context))
                .build();
        if (service == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(DEV_URL_CLIENT)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();

            service = retrofit.create(ReqInterface.class);
        }
        return service;
    }

    public static ReqInterface getNoClient(Activity context) {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(context))
                .build();
        if (service2 == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://jid.jasamarga.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();

            service2 = retrofit.create(ReqInterface.class);
        }
        return service2;
    }
    public static class AuthInterceptor implements Interceptor {
        Sessionmanager sessionmanager;

        public AuthInterceptor(Activity context) {
            this.sessionmanager = new Sessionmanager(context);
        }
        @NonNull
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            String url = request.url().toString();
            if (!url.contains("login")){
                if (response.code() == 401) {
                    // Handle 401 Unauthorized error
                    Log.d("AuthInterceptor", "Unauthorized: " + response.message() + url);
                    // Perform logout and other necessary actions
                    sessionmanager.logout();
//                    sessionmanager.checkLogin();
                    // Note: If this is an activity, you might need to call finish() within the activity context
                }
            }


            return response;
        }
    }

}
