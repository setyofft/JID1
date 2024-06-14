package com.jasamarga.jid.views;

import static com.jasamarga.jid.components.PopupDetailLalin.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.jasamarga.jid.Dashboard;
import com.jasamarga.jid.components.Appbar;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.RuasAdapter;
import com.jasamarga.jid.models.RuasModel;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.ServiceFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private ShimmerFrameLayout mShimmerViewContainer;


    List<String> ListRuas = new ArrayList<>();
    ArrayAdapter<String> adapter;
    RuasAdapter mAdapter;
    RecyclerView.LayoutManager mManager;
    ArrayList<RuasModel> mItems;

    String username,scope,error;
    private TextView badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);

        sessionmanager = new Sessionmanager(getApplicationContext());
        userSession = sessionmanager.getUserDetails();

        menuBottomnavbar();
        initVar();
        ServiceFunction.addLogActivity(this,"CCTV",error,"List CCTV");

    }

    private void initVar(){
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        nameInitial = findViewById(R.id.nameInitial);
        nameuser = findViewById(R.id.nameuser);
        button_exit = findViewById(R.id.button_exit);
        dataRCv = findViewById(R.id.dataRCv);
        btnMap = findViewById(R.id.btnMap);
        cariruas = findViewById(R.id.search);
        badge = findViewById(R.id.cart_badge);
        Appbar.appBar(this,getWindow().getDecorView());

        clickOn();
        deklarasiVar();
    }

    private void clickOn(){

        btnMap.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Maps.class));
            overridePendingTransition(0, 0);
            finish();
        });
        cariruas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!mItems.isEmpty()){
                    search(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void deklarasiVar(){
        username = userSession.get(Sessionmanager.kunci_id);
        nameuser.setText(username);
        nameInitial.setText(username.substring(0,1).toUpperCase());
        scope = userSession.get(Sessionmanager.set_scope);
        if (ServiceFunction.Terkoneksi(this)){
            getRuas();
        }else {
            ServiceFunction.pesanNosignalDefault( this);
        }
    }

    private void setAdapter(){
//        adapter = new ArrayAdapter<String>(this, R.layout.dropdown_custom,R.id.item_text, ListRuas);
//        cariruas.setAdapter(adapter);
//        cariruas.setDropDownBackgroundResource(R.drawable.popup_search);
//        cariruas.setDropDownVerticalOffset(20);
//        cariruas.setThreshold(1);
    }
    private void getRuas() {
        mShimmerViewContainer.startShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.VISIBLE);

        mItems = new ArrayList<>();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mManager = new GridLayoutManager(this, 2); // Angka 2 bisa diubah sesuai kebutuhan
        } else {
            // Jika orientasi portrait, gunakan LinearLayoutManager
            mManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
        dataRCv.setLayoutManager(mManager);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id_ruas", scope);

        ReqInterface serviceAPI = ApiClient.getClient();
        String token ;
        token = userSession.get(Sessionmanager.nameToken);
        Log.d(TAG, "getRuas: " + token);
        Call<JsonObject> call = serviceAPI.excutedataruas(jsonObject,token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.body() != null){
                        JSONObject dataRes = new JSONObject(response.body().toString());
                        if (dataRes.getString("status").equals("1")){
                            JSONArray dataResult = new JSONArray(dataRes.getString("results"));
                            for (int i = 0; i < dataResult.length(); i++) {
                                JSONObject getdata = dataResult.getJSONObject(i);
                                RuasModel md = new RuasModel();
                                md.setId_ruas(getdata.getString("id_ruas"));
                                md.setNama_ruas(getdata.getString("nama_ruas"));
                                md.setNama_ruas_2(getdata.getString("nama_ruas_2"));
                                Log.d(TAG, "onResponse: " + getdata);
                                ListRuas.add(md.getNama_ruas());
                                mItems.add(md);
                            }
                            mAdapter = new RuasAdapter(Cctv.this, mItems);
                            dataRCv.setAdapter(mAdapter);
                            setAdapter();

                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                        }else{
                            error = "Gagal Get Ruas CCTV" + response.message();
                            Log.d("STATUS ERR", response.toString());
                        }

                    }
//                    if (dataRCv.isShown()) {
//                    } else {
//                        loadingDialog.showLoadingDialog("Loading...");
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = "Gagal Get Ruas CCTV" + e.getMessage();
                    Log.d(TAG, "ERROR CCTV: " + e.getMessage());
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", t.getMessage());
                error = "Gagal Get Ruas CCTV" + t.getMessage();

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        });
    }
    private void search(String text){
        ArrayList<RuasModel> filteredList = new ArrayList<>();
        for (RuasModel item : mItems) {
            if (item.getNama_ruas().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }else if (item.getNama_ruas_2().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        mAdapter.filterList(filteredList);
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
                    startActivity(new Intent(getApplicationContext(), Antrian.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.realtime_lalin:
                    startActivity(new Intent(getApplicationContext(), RealtimeTraffic.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
            }
            return false;
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        Appbar.appBar(this,getWindow().getDecorView());
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}