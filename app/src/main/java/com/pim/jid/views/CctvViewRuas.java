package com.pim.jid.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.pim.jid.LoadingDialog;
import com.pim.jid.R;
import com.pim.jid.Sessionmanager;
import com.pim.jid.adapter.CctvSegmentAdapter;
import com.pim.jid.adapter.RuasAdapter;
import com.pim.jid.adapter.SegmentAdapter;
import com.pim.jid.models.CcctvSegmentModel;
import com.pim.jid.models.RuasModel;
import com.pim.jid.models.SegmentModel;
import com.pim.jid.router.ApiClient;
import com.pim.jid.router.ReqInterface;
import com.pim.jid.service.ServiceFunction;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CctvViewRuas extends AppCompatActivity{

    Sessionmanager sessionmanager;
    HashMap<String, String> userSession = null;

    private CardView button_exit;
    private MaterialButton button_back;
    private TextView nameInitial, nameuser,judulruas1,cctvOff,location,set_data_empty;
    private LoadingDialog loadingDialog;
    private RecyclerView dataRCv;
    private MaterialButton btnMap;
    private ImageView imageCCTV;
    private ProgressBar loading;
    private LinearLayout linearLayout;

    Handler handlerCctv;
    CctvSegmentAdapter mAdapter;
    RecyclerView.LayoutManager mManager;
    ArrayList<CcctvSegmentModel> mItems;
    CctvSegmentAdapter.RecyclerViewClickListener listener;

    Intent intent;
    int row_index = -1, time = 300;
    String username,scope,judulRuas,id_ruas,id_segment,nmKm,nmLokasi,key_id,img_url,status;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cctv_ruas);

        initVar();

    }


    private void clickOn(){

        listener = new CctvSegmentAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
//                imageCCTV.destroyDrawingCache();
                handlerCctv.removeCallbacksAndMessages(null);
                handlerCctv.removeCallbacks(null);
                imageCCTV.setImageResource(0);
                location.setText("KM " + mItems.get(position).getKm() + " | " + nmLokasi);
                handlerCctv.postDelayed(new Runnable(){
                    public void run(){
                        img_url = "https://jid.jasamarga.com/cctv2/"+mItems.get(position).getKeyId()+"?tx="+Math.random();
                        initStreamImg(img_url, imageCCTV,loading );
                        handlerCctv.postDelayed(this, 300);
                    }
                }, time);
            }
        };
        button_exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(CctvViewRuas.this);
            alertDialogBuilder.setTitle("Peringatan Akun");
            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> ServiceFunction.delSession(this,loadingDialog,username,sessionmanager));
            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();
        });
        button_back.setOnClickListener(v -> {
            handlerCctv.removeCallbacksAndMessages(null);
            finish();
        });
    }

    private void initVar(){
        cctvOff = findViewById(R.id.set_cctv_off);
        loading = findViewById(R.id.loadingIMG);
        nameInitial = findViewById(R.id.nameInitial);
        nameuser = findViewById(R.id.nameuser);
        button_exit = findViewById(R.id.button_exit);
        judulruas1 = findViewById(R.id.titleSegment);
        button_back = findViewById(R.id.back);
        dataRCv = findViewById(R.id.list_segment);
        imageCCTV = findViewById(R.id.image_cctv);
        location = findViewById(R.id.location);
        linearLayout = findViewById(R.id.SHOW_ALL);
        set_data_empty = findViewById(R.id.set_empty_data);
        sessionmanager = new Sessionmanager(getApplicationContext());
        mItems = new ArrayList<>();

        clickOn();
        deklarasiVar();
    }

    private void deklarasiVar(){
        userSession = sessionmanager.getUserDetails();
        username = userSession.get(Sessionmanager.kunci_id);
        nameuser.setText(username);
        nameInitial.setText(username.substring(0,1).toUpperCase());
        scope = userSession.get(Sessionmanager.set_scope);
        intent = getIntent();
        judulRuas = intent.getStringExtra("judul_segment");
        id_ruas = intent.getStringExtra("id_ruas");
        id_segment = intent.getStringExtra("id_segment");
        judulruas1.setText(judulRuas);
        handlerCctv = new Handler();

        getSegment();
    }
    private void getSegment() {
        loadingDialog = new LoadingDialog(CctvViewRuas.this);
        loadingDialog.showLoadingDialog("Loading...");

        mItems = new ArrayList<>();
        mManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        dataRCv.setLayoutManager(mManager);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id_ruas", id_ruas);
        jsonObject.addProperty("id_segment", id_segment);

        ReqInterface serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutedatacctvseg(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("TAG", "onResponse: " + response.body());
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        JSONArray dataResult = new JSONArray(dataRes.getString("results"));
                        if(dataResult.length() == 0){
                            set_data_empty.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.GONE);
                        }else {
                            set_data_empty.setVisibility(View.GONE);
                            for (int i = 0; i < dataResult.length(); i++) {
                                JSONObject getdata = dataResult.getJSONObject(i);
                                CcctvSegmentModel md = new CcctvSegmentModel();
                                md.setIdSegment(getdata.getString("id_segment"));
                                md.setNamaSegment(getdata.getString("nama_segment"));
                                md.setKeyId(getdata.getString("key_id"));
                                md.setCabang(getdata.getString("cabang"));
                                md.setKm(getdata.getString("km"));
                                md.setStatus(getdata.getString("status"));

                                mItems.add(md);
                            }
                            //get first cctv
                            JSONObject get = dataResult.getJSONObject(0);
                            status = get.getString("status");
                            nmKm = get.getString("km");
                            nmLokasi = get.getString("nama_segment");
                            key_id=get.getString("key_id");

                            location.setText("KM " + nmKm + " | " + nmLokasi);
                            setImageCCTV();
                            mAdapter = new CctvSegmentAdapter(CctvViewRuas.this, mItems,listener,row_index);
                            dataRCv.setAdapter(mAdapter);
                        }

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



    private void setImageCCTV(){
        if(status.equals("0")){
            cctvOff.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }else {
            cctvOff.setVisibility(View.GONE);
            handlerCctv.postDelayed(new Runnable(){
                public void run(){
                    img_url = "https://jid.jasamarga.com/cctv2/"+key_id+"?tx="+Math.random();
                    initStreamImg(img_url, imageCCTV,loading );
                    handlerCctv.postDelayed(this, 300);
                }
            }, time);
        }
    }

    private void initStreamImg(String img_url, ImageView img, ProgressBar loadingIMG) {
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(img_url)
                .override(350, 200)
                .centerCrop()
                .centerInside()
                .error(R.drawable.blank_photo)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        loadingIMG.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        loadingIMG.setVisibility(View.GONE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource,
                                                @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        img.setImageBitmap(resource);
                    }
                });

    }
}
