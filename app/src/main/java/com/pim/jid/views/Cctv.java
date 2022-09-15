package com.pim.jid.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.pim.jid.Dashboard;
import com.pim.jid.LoadingDialog;
import com.pim.jid.R;
import com.pim.jid.Sessionmanager;
import com.pim.jid.adapter.RuasAdapter;
import com.pim.jid.models.RuasModel;
import com.pim.jid.router.ApiClient;
import com.pim.jid.router.ReqInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cctv extends AppCompatActivity{

    Sessionmanager sessionmanager;
    HashMap<String, String> userSession = null;

    private CardView button_exit;
    private TextView nameInitial, nameuser;
    private LoadingDialog loadingDialog;
    private AutoCompleteTextView cariruas;
    private RecyclerView dataRCv;
    private MaterialButton btnMap;

    RuasAdapter mAdapter;
    RecyclerView.LayoutManager mManager;
    ArrayList<RuasModel> mItems;

    String username,scope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);

        sessionmanager = new Sessionmanager(getApplicationContext());
        userSession = sessionmanager.getUserDetails();

        menuBottomnavbar();
        initVar();
    }

    private void initVar(){
        nameInitial = findViewById(R.id.nameInitial);
        nameuser = findViewById(R.id.nameuser);
        button_exit = findViewById(R.id.button_exit);
        dataRCv = findViewById(R.id.dataRCv);
        btnMap = findViewById(R.id.btnMap);

        clickOn();
        deklarasiVar();
    }

    private void clickOn(){
        button_exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Cctv.this);
            alertDialogBuilder.setTitle("Peringatan Akun");
            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> delSession());
            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();
        });

        btnMap.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Home.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void deklarasiVar(){
        username = userSession.get(Sessionmanager.kunci_id);
        nameuser.setText(username);
        nameInitial.setText(username.substring(0,1).toUpperCase());
        scope = userSession.get(Sessionmanager.set_scope);

        getRuas();
    }

    private void getRuas() {
        loadingDialog = new LoadingDialog(Cctv.this);
        loadingDialog.showLoadingDialog("Loading...");

        mItems = new ArrayList<>();
        mManager = new LinearLayoutManager(Cctv.this, LinearLayoutManager.VERTICAL, false);
        dataRCv.setLayoutManager(mManager);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id_ruas", scope);

        ReqInterface serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutedataruas(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        JSONArray dataResult = new JSONArray(dataRes.getString("results"));
                        for (int i = 0; i < dataResult.length(); i++) {
                            JSONObject getdata = dataResult.getJSONObject(i);
                            RuasModel md = new RuasModel();
                            md.setId_ruas(getdata.getString("id_ruas"));
                            md.setNama_ruas(getdata.getString("nama_ruas"));
                            md.setNama_ruas_2(getdata.getString("nama_ruas_2"));

                            mItems.add(md);
                        }
                        mAdapter = new RuasAdapter(Cctv.this, mItems);
                        dataRCv.setAdapter(mAdapter);
                    }else{
                        Log.d("STATUS", response.toString());
                    }
                    loadingDialog.hideLoadingDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.hideLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
                loadingDialog.hideLoadingDialog();
            }
        });
    }

    private void delSession() {
        loadingDialog = new LoadingDialog(Cctv.this);
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

    private void menuBottomnavbar(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.cctv);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.cctv:
                    return true;
                case R.id.antrian_gerbang:
                case R.id.realtime_lalin:
                    Toast.makeText(getApplicationContext(), "Sedang tahap pembuatan !", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        });
    }
}