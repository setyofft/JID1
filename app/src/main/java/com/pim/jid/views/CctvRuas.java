package com.pim.jid.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.pim.jid.LoadingDialog;
import com.pim.jid.R;
import com.pim.jid.Sessionmanager;
import com.pim.jid.adapter.RuasAdapter;
import com.pim.jid.adapter.SegmentAdapter;
import com.pim.jid.models.RuasModel;
import com.pim.jid.models.SegmentModel;
import com.pim.jid.router.ApiClient;
import com.pim.jid.router.ReqInterface;
import com.pim.jid.service.ServiceFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CctvRuas extends AppCompatActivity {

    Sessionmanager sessionmanager;
    HashMap<String, String> userSession = null;

    private CardView button_exit;
    private MaterialButton button_back;
    private TextView nameInitial, nameuser,judulruas1;
    private LoadingDialog loadingDialog;
    private RecyclerView dataRCv;
    private MaterialButton btnMap;

    SegmentAdapter mAdapter;
    RecyclerView.LayoutManager mManager;
    ArrayList<SegmentModel> mItems;

    Intent intent;
    String username,scope,judulRuas,id_ruas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv_ruas);

        initVar();

    }


    private void clickOn(){
        button_exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(CctvRuas.this);
            alertDialogBuilder.setTitle("Peringatan Akun");
            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> ServiceFunction.delSession(this,loadingDialog,username,sessionmanager));
            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();
        });
        button_back.setOnClickListener(v -> {
            finish();
        });
    }

    private void initVar(){
        nameInitial = findViewById(R.id.nameInitial);
        nameuser = findViewById(R.id.nameuser);
        button_exit = findViewById(R.id.button_exit);
        judulruas1 = findViewById(R.id.titleRuas);
        button_back = findViewById(R.id.back);
        dataRCv = findViewById(R.id.listruas);
        sessionmanager = new Sessionmanager(getApplicationContext());
        mItems = new ArrayList<>();
        deklarasiVar();
        clickOn();
    }

    private void deklarasiVar(){
        userSession = sessionmanager.getUserDetails();
        username = userSession.get(Sessionmanager.kunci_id);
        nameuser.setText(username);
        nameInitial.setText(username.substring(0,1).toUpperCase());
        scope = userSession.get(Sessionmanager.set_scope);
        intent = getIntent();
        judulRuas = intent.getStringExtra("judul_ruas");
        id_ruas = intent.getStringExtra("id_ruas");
        judulruas1.setText(judulRuas);

        getRuas();

    }


    private void getRuas() {
        loadingDialog = new LoadingDialog(CctvRuas.this);
        loadingDialog.showLoadingDialog("Loading...");

        mItems = new ArrayList<>();
        mManager = new GridLayoutManager(CctvRuas.this,2);
        dataRCv.setLayoutManager(mManager);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id_ruas", id_ruas);

        ReqInterface serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutedatasegment(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("TAG", "onResponse: " + response.body());
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        JSONArray dataResult = new JSONArray(dataRes.getString("results"));
                        for (int i = 0; i < dataResult.length(); i++) {
                            JSONObject getdata = dataResult.getJSONObject(i);
                            SegmentModel md = new SegmentModel();
                            md.setIdSegment(getdata.getString("idx"));
                            md.setNamaSegment(getdata.getString("nama_segment"));
                            md.setIdRUas(id_ruas);

                            mItems.add(md);
                        }
                        mAdapter = new SegmentAdapter(CctvRuas.this, mItems);
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
}
