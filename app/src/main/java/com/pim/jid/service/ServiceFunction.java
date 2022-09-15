package com.pim.jid.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.pim.jid.LoadingDialog;
import com.pim.jid.Sessionmanager;
import com.pim.jid.router.ApiClient;
import com.pim.jid.router.ReqInterface;
import com.pim.jid.views.Cctv;
import com.pim.jid.views.CctvRuas;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceFunction {

    public static void delSession(Context context, LoadingDialog loadingDialog, String username, Sessionmanager sessionmanager) {
        loadingDialog = new LoadingDialog(((Activity) context));
        loadingDialog.showLoadingDialog("Loading...");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", username);

        ReqInterface serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutedelsession(jsonObject);
        LoadingDialog finalLoadingDialog = loadingDialog;
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        sessionmanager.logout();
                        ((Activity) context).finish();
                    }else{
                        Log.d("STATUS", response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finalLoadingDialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
                finalLoadingDialog.hideLoadingDialog();
            }
        });
    }
}
