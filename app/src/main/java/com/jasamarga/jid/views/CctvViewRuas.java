package com.jasamarga.jid.views;

import static com.jasamarga.jid.components.PopupDetailLalin.TAG;
import static java.lang.Integer.parseInt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.DefaultHlsDataSourceFactory;
import androidx.media3.exoplayer.hls.HlsDataSourceFactory;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.CctvSegmentAdapter;
import com.jasamarga.jid.models.CctvSegmentModel;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.ServiceFunction;

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
    PlayerView playerView;
    Handler handlerCctv,handlerAdapater;
    CctvSegmentAdapter mAdapter;
    RecyclerView.LayoutManager mManager;
    ArrayList<CctvSegmentModel> mItems;
    CctvSegmentAdapter.RecyclerViewClickListener listener;
    Intent intent;
    int row_index = 0, time = 300;
    String username,scope,judulRuas,id_ruas,id_segment,nmKm,nmLokasi,key_id,img_url,status;
    CardView cardViewPlayer,cardViewImage;
    private ExoPlayer player;
    private String uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cctv_ruas);
        initVar();

    }


    private void clickOn(){
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                clean();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        //click for item adapter
        listener = new CctvSegmentAdapter.RecyclerViewClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v, int position) {
                int is_hls = mItems.get(position).getIs_hls();
                String key_id = mItems.get(position).getKeyId();
                Log.d(TAG, "onClick: " + mItems.get(position).getNamaSegment() + is_hls);
                location.setText(!mItems.get(position).getKm().toLowerCase().contains("km") ? "KM " + mItems.get(position).getKm() + " | " + mItems.get(position).getNamaSegment() : mItems.get(position).getKm() + " | " + mItems.get(position).getNamaSegment());
                if (handlerCctv != null){
                    handlerCctv.removeCallbacksAndMessages(null);
                }
                if (player != null){
                    player.release();
                }
                if (imageCCTV.getDrawable()!= null){
                    Log.d(TAG, "initStreamImg: " + imageCCTV.getDrawable());
                    imageCCTV.clearFocus();
                    imageCCTV.setImageResource(0);
                    imageCCTV.setImageBitmap(null);
                }

                Glide.with(getApplicationContext())
                        .clear(imageCCTV);
                if (is_hls == 0){
                    cardViewImage.setVisibility(View.VISIBLE);
                    cardViewPlayer.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    if (handlerCctv != null){
                        handlerCctv.removeCallbacksAndMessages(null);
                    }
                    img_url = "https://jid.jasamarga.com/cctv2/"+key_id+"?tx="+Math.random();
                    initStreamImg(img_url, imageCCTV,loading );
                    handlerCctv.postDelayed(new Runnable(){
                        public void run(){
                            img_url = "https://jid.jasamarga.com/cctv2/"+mItems.get(position).getKeyId()+"?tx="+Math.random();
                            Log.d(TAG, "onClick: " + img_url);
                            initStreamImg(img_url, imageCCTV,loading );
                            handlerCctv.postDelayed(this, 300);
                        }
                    }, 300);
                }else {
                    loading.setVisibility(View.GONE);
                    cardViewImage.setVisibility(View.GONE);
                    cardViewPlayer.setVisibility(View.VISIBLE);
                    handlerCctv.removeCallbacksAndMessages(null);
                    String uri = "https://jmlive.jasamarga.com/hls/"+id_ruas+"/"+key_id+"/"+"index.m3u8";
                    Uri videoUri = Uri.parse(uri);
                    setHLS(videoUri,key_id);
                }
            }
        };

        button_exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(CctvViewRuas.this);
            alertDialogBuilder.setTitle("Logout");
            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> ServiceFunction.delSession(this,loadingDialog,username,sessionmanager));
            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();
        });
        button_back.setOnClickListener(v -> {
            clean();
        });
    }



    @SuppressLint("NotifyDataSetChanged")
    private void clean(){
        handlerCctv.removeCallbacksAndMessages(null);
        handlerAdapater.removeCallbacksAndMessages(null);
        mItems.clear();
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
        if (player != null) {
            // Pause the player
            player.setPlayWhenReady(false);
            // Release the player to free resources
            player.release();
            // Set the player to null to avoid memory leaks
            player = null;
        }
        finish();
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
        cardViewPlayer = findViewById(R.id.cardPlayerCctv);
        cardViewImage = findViewById(R.id.cardImageCctv);
        playerView = findViewById(R.id.playerView);
        sessionmanager = new Sessionmanager(this);
        mItems = new ArrayList<>();
        handlerCctv = new Handler();
        handlerAdapater = new Handler();

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

        getSegment();
    }

    private boolean isTablet() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        // Tambahkan kondisi untuk menentukan apakah perangkat adalah tablet atau bukan
        // Misalnya, jika lebar atau tinggi melebihi 600dp, kita anggap sebagai tablet
        return Math.min(dpWidth, dpHeight) >= 600;
    }

    private void getSegment() {
        loadingDialog = new LoadingDialog(CctvViewRuas.this);
        loadingDialog.showLoadingDialog("Loading...");

        mItems = new ArrayList<>();
        if (isTablet() && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false); // Non-tablet atau orientasi landscape
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            mManager = new GridLayoutManager(this, 4); // Tablet dalam orientasi portrait
        }else {
            mManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false); // Non-tablet atau orientasi landscape
        }
        dataRCv.setLayoutManager(mManager);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id_ruas", id_ruas);
        jsonObject.addProperty("id_segment", id_segment);
        Log.d(TAG, "getSegment: " + jsonObject.toString());
        ReqInterface serviceAPI = ApiClient.getClient(this);
        String token;
        token = userSession.get(Sessionmanager.nameToken);
        Call<JsonObject> call = serviceAPI.excutedatacctvseg(jsonObject,token);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("TAG", "onResponse:CCTV " + response.body());
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
                                CctvSegmentModel md = new CctvSegmentModel();
                                if(judulRuas.contains("Semua")){
                                    md.setNamaSegment(getdata.getString("cabang"));
                                    md.setCabang(getdata.getString("nama"));
                                    md.setKm(getdata.getString("nama"));
                                    md.setStatus(getdata.getString("status"));
                                    md.setKeyId(getdata.getString("key_id"));
                                }else {
                                    md.setNamaSegment(getdata.getString("nama_segment"));
                                    md.setCabang(getdata.getString("cabang"));
                                    md.setIdSegment(getdata.getString("id_segment"));
                                    md.setKm(getdata.getString("km"));
                                    md.setStatus(getdata.getString("status"));
                                    md.setKeyId(getdata.getString("key_id"));
                                }
                                md.setIs_hls(getdata.getInt("is_hls"));
                                mItems.add(md);
                            }
                            //get first cctv
                            JSONObject get = dataResult.getJSONObject(0);
                            status = get.getString("status");
                            nmKm = judulRuas.contains("Semua") ? get.getString("nama") : get.getString("km");
                            nmLokasi = judulRuas.contains("Semua") ? get.getString("cabang") : get.getString("nama_segment");
                            key_id=get.getString("key_id");
                            int is_hls =get.getInt("is_hls");
                            String loc = " | " + nmLokasi;
                            Log.d(TAG, "CameraCCTV: " + is_hls);
                            location.setText(nmKm.toLowerCase().contains("km") ? nmKm + loc : "KM " + nmKm + loc);
                            if (is_hls == 1){
                                uri = "https://jmlive.jasamarga.com/hls/"+id_ruas+"/"+key_id+"/"+"index.m3u8";
                                Uri videoUri = Uri.parse(uri);
                                setHLS(videoUri,key_id);
                                handlerCctv.removeCallbacksAndMessages(null);
                                cardViewImage.setVisibility(View.GONE);
                                cardViewPlayer.setVisibility(View.VISIBLE);
                            }else {
                                cardViewImage.setVisibility(View.VISIBLE);
                                cardViewPlayer.setVisibility(View.GONE);
                                setImageCCTV(key_id);
                            }
                            mAdapter = new CctvSegmentAdapter(CctvViewRuas.this, mItems,listener,row_index,handlerCctv);
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


    @OptIn(markerClass = UnstableApi.class) private void setHLS(Uri videouri,String key_id){
        Log.d(TAG, "setHLS: " + videouri);
        loading.setVisibility(View.GONE);

        androidx.media3.datasource.DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
        HlsMediaSource hlsMediaSource =
                new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videouri));
        player = new ExoPlayer.Builder(getApplicationContext()).build();
        playerView.setPlayer(player);
        player.setMediaSource(hlsMediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                Log.d(TAG, "onPlayerError: " + error);
                if (handlerCctv != null){
                    handlerCctv.removeCallbacksAndMessages(null);
                }
                cardViewImage.setVisibility(View.VISIBLE);
                cardViewPlayer.setVisibility(View.GONE);
                setImageCCTV(key_id);
            }
        });
    }
    private void setImageCCTV(String key_id){
        if(status.equals("0")){
            cctvOff.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }else {
            cctvOff.setVisibility(View.GONE);
            handlerCctv.postDelayed(new Runnable(){
                public void run(){
                    img_url = "https://jid.jasamarga.com/cctv2/"+key_id+"?tx="+Math.random();
                    initStreamImg(img_url, imageCCTV,loading );
//                    Log.d(TAG, "run: " + img_url);
                    handlerCctv.postDelayed(this, 300);
                }
            }, time);
        }
    }

    private void initStreamImg(String img_url, ImageView img, ProgressBar loadingIMG) {

        Glide.with(this)
                .asBitmap()
                .load(img_url)
                .override(350, 200)
                .centerCrop()
                .centerInside()
                .dontAnimate()
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
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // pastikan ImageView tidak dihapus saat ini, jika diperlukan
                        if (!isDestroyed() && !isFinishing()) {
                            img.setImageBitmap(resource); // atur gambar ke ImageView
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // dapatkan gambar placeholder saat dihapus dari memori atau saat Anda perlu memuat ulang
                        img.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        // tampilkan gambar kesalahan jika gambar tidak dapat dimuat
                        img.setImageDrawable(errorDrawable);
                    }
                });

    }

    @Override
    public void onBackPressed() {
        clean();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
            player.release();
            player = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (uri != null){
            setHLS(Uri.parse(uri),key_id);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            // Pause the player
            player.setPlayWhenReady(false);
            // Release the player to free resources
            player.release();
            // Set the player to null to avoid memory leaks
            player = null;
        }
        handlerCctv.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handlerCctv.removeCallbacksAndMessages(null);
    }
}
