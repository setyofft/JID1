package com.pim.jid.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.pim.jid.service.ServiceFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        nameInitial = findViewById(R.id.nameInitial);
        nameuser = findViewById(R.id.nameuser);
        button_exit = findViewById(R.id.button_exit);
        dataRCv = findViewById(R.id.dataRCv);
        btnMap = findViewById(R.id.btnMap);
        cariruas = findViewById(R.id.search);

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
            startActivity(new Intent(getApplicationContext(), Maps.class));
            overridePendingTransition(0, 0);
            finish();
        });
        cariruas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = ListRuas.indexOf(cariruas.getText().toString());
                String nama = mItems.get(position).getNama_ruas();
                String id = mItems.get(position).getId_ruas();
                Intent intent = new Intent(getApplicationContext(),CctvRuas.class);
                intent.putExtra("judul_ruas",nama);
                intent.putExtra("id_ruas",id);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }

    public static int getIndexOfItemInArray(List<String> stringArray, String name) {
        if (stringArray != null && stringArray.size() > 0) {
            ArrayList<String> list = new ArrayList<String>(Arrays.<String>asList(String.valueOf(stringArray)));
            int index = list.indexOf(name);
            list.clear();
            return index;
        }
        return -1;
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
        adapter = new ArrayAdapter<String>(this, R.layout.dropdown_custom,R.id.item_text, ListRuas);
        cariruas.setAdapter(adapter);
        cariruas.setDropDownBackgroundResource(R.drawable.popup_search);
        cariruas.setDropDownVerticalOffset(20);
        cariruas.setThreshold(1);
    }
    private void getRuas() {
        mShimmerViewContainer.startShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.VISIBLE);

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
                            ListRuas.add(md.getNama_ruas());
                            mItems.add(md);
                        }
                        mAdapter = new RuasAdapter(Cctv.this, mItems);
                        dataRCv.setAdapter(mAdapter);
                        setAdapter();

                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    }else{
                        Log.d("STATUS ERR", response.toString());
                    }

//                    if (dataRCv.isShown()) {
//                    } else {
//                        loadingDialog.showLoadingDialog("Loading...");
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
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
                    startActivity(new Intent(getApplicationContext(), Antrian.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.realtime_lalin:
                    Toast.makeText(getApplicationContext(), "Sedang tahap pembuatan !", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}