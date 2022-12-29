package com.jasamarga.jid.views;

import static com.jasamarga.jid.components.PopupDetailLalin.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.jasamarga.jid.components.Appbar;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.SegmentAdapter;
import com.jasamarga.jid.models.SegmentModel;
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

public class CctvRuas extends AppCompatActivity {

    Sessionmanager sessionmanager;
    HashMap<String, String> userSession = null;

    private CardView button_exit;
    private MaterialButton button_back,button_all_segment;
    private TextView nameInitial, nameuser,judulruas1;
    private LoadingDialog loadingDialog;
    private AutoCompleteTextView search;
    private RecyclerView dataRCv;
    private MaterialButton btnMap;
    private ShimmerFrameLayout mShimmerViewContainer;
    List<String> ListRuas = new ArrayList<>();
    ArrayAdapter<String> adapter;
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
//        button_exit.setOnClickListener(v -> {
//            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(CctvRuas.this);
//            alertDialogBuilder.setTitle("Peringatan Akun");
//            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
//            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
//            alertDialogBuilder.setCancelable(false);
//            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> ServiceFunction.delSession(this,loadingDialog,username,sessionmanager));
//            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
//            alertDialogBuilder.show();
//        });
        button_back.setOnClickListener(v -> {
            finish();
        });

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = ListRuas.indexOf(search.getText().toString());
                Intent intent = new Intent(getApplicationContext(), CctvViewRuas.class);
                intent.putExtra("judul_segment",mItems.get(position).getNamaSegment());
                intent.putExtra("id_segment",mItems.get(position).getIdSegment());
                intent.putExtra("id_ruas",mItems.get(0).getIdRUas());
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        button_all_segment.setOnClickListener(view -> {
                Intent intent = new Intent(CctvRuas.this, CctvViewRuas.class);
                intent.putExtra("judul_segment","Semua Segment");
                intent.putExtra("id_segment","0");
                intent.putExtra("id_ruas",mItems.get(0).getIdRUas());
                startActivity(intent);
               overridePendingTransition(0,0);

        });
    }

    private void initVar(){
        button_all_segment = findViewById(R.id.button_all_segment);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        search = findViewById(R.id.search);
        nameInitial = findViewById(R.id.nameInitial);
        nameuser = findViewById(R.id.nameuser);
        button_exit = findViewById(R.id.button_exit);
        judulruas1 = findViewById(R.id.titleRuas);
        button_back = findViewById(R.id.back);
        dataRCv = findViewById(R.id.listruas);
        sessionmanager = new Sessionmanager(getApplicationContext());
        mItems = new ArrayList<>();
        search = findViewById(R.id.search);
        Appbar.appBar(this,getWindow().getDecorView());

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

        if (ServiceFunction.Terkoneksi(this)){
            getRuas();
        }else {
            ServiceFunction.pesanNosignalDefault( this);
        }
    }

    private void setAdapter(){
        adapter = new ArrayAdapter<String>(this, R.layout.dropdown_custom,R.id.item_text, ListRuas);
        search.setAdapter(adapter);
        search.setDropDownBackgroundResource(R.drawable.popup_search);
        search.setDropDownVerticalOffset(20);
        search.setThreshold(1);
    }

    private void getRuas() {
        loadingDialog = new LoadingDialog(CctvRuas.this);
        loadingDialog.showLoadingDialog("Loading...");

        mItems = new ArrayList<>();
        mManager = new LinearLayoutManager(CctvRuas.this);
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
                            Log.d(TAG, "onResponse: " + getdata);

                            ListRuas.add(md.getNamaSegment());
                            mItems.add(md);
                        }


                        mAdapter = new SegmentAdapter(CctvRuas.this, mItems);
                        dataRCv.setAdapter(mAdapter);
                        setAdapter();
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        if(dataRCv.isShown()){
                            loadingDialog.hideLoadingDialog();
                        }else {
                            loadingDialog.showLoadingDialog("Loading...");
                        }
                    }else{
                        Log.d("STATUS", response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    loadingDialog.hideLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                loadingDialog.hideLoadingDialog();
            }
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
