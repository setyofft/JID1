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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.jasamarga.jid.router.ApiClientNew;
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
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CctvViewRuas extends AppCompatActivity{
    int retryCountHLS = 0;  // Variabel untuk menghitung jumlah percobaan

    Sessionmanager sessionmanager;
    HashMap<String, String> userSession = null;
    private Handler handlerRetryHLS;
    private Runnable retryRunnableHLS;
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
    SwipeRefreshLayout swipeRefreshLayout;
    private ExoPlayer player;
    private String uri;
    private String allSegment;
    private boolean isRetryingHLS = false;
    private boolean cctv2Error = false;
    private boolean hlserror = false;

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
                boolean is_hls = mItems.get(position).getIs_hls();
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
                if (!is_hls){
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
        if (handlerRetryHLS != null){
            handlerRetryHLS.removeCallbacks(retryRunnableHLS);
        }
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
        swipeRefreshLayout = findViewById(R.id.refresh);
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
        allSegment = intent.getStringExtra("nama_segment");
        id_ruas = intent.getStringExtra("id_ruas");
        id_segment = intent.getStringExtra("id_segment");
        judulruas1.setText(judulRuas);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
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

    // Function to refresh data
    private void refreshData() {
        // Clear the data first
        if (mItems != null) {
            mItems.clear();  // Clear the list of items
            mAdapter.notifyDataSetChanged();  // Notify adapter about data changes
        }

        // Call the API again
        getSegment();
    }
    private void getSegment() {
        loadingDialog = new LoadingDialog(CctvViewRuas.this);
        loadingDialog.showLoadingDialog("Loading...");
        ReqInterface apiClientNew = ApiClientNew.getServiceNew();
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
        Call<JsonObject> call = apiClientNew.cctvMatrix(token,judulRuas.toLowerCase().contains("semua") ? allSegment:judulRuas,id_ruas,"500","0");
        HttpUrl url = call.request().url();
        Log.d("Full URL CCTV", url.toString());
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("TAG", "onResponse:CCTV " + response.body());
                try {
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    JSONArray dataResult = new JSONArray(dataRes.getString("rows"));
                    if(dataResult.length() == 0){
                        set_data_empty.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                    }else {
                        set_data_empty.setVisibility(View.GONE);
                        for (int i = 0; i < dataResult.length(); i++) {
                            JSONObject getdata = dataResult.getJSONObject(i);
                            Log.d(TAG, "onResponse: CCTV BARU" + getdata);
                            CctvSegmentModel md = new CctvSegmentModel();
                            md.setNamaSegment(getdata.getString("nama_segment"));
                            md.setCabang(getdata.getString("nama"));
                            md.setKm(getdata.getString("km"));
                            md.setKeyId(getdata.getString("key_id"));

                            md.setIs_hls(getdata.getBoolean("is_hls"));
                            mItems.add(md);
                        }
                        //get first cctv
                        JSONObject get = dataResult.getJSONObject(0);
                        nmKm =  get.getString("nama");
                        nmLokasi = get.getString("nama_segment");
                        key_id=get.getString("key_id");
                        boolean is_hls =get.getBoolean("is_hls");
                        String loc = " | " + nmLokasi;
                        Log.d(TAG, "CameraCCTV: " + is_hls);
                        location.setText(nmKm + loc);

                        uri = "https://jmlive.jasamarga.com/hls/"+id_ruas+"/"+key_id+"/"+"index.m3u8";
                        Uri videoUri = Uri.parse(uri);

                        if (is_hls){
                            setHLS(videoUri,key_id);
                            handlerCctv.removeCallbacksAndMessages(null);
                            cardViewImage.setVisibility(View.GONE);
                            cardViewPlayer.setVisibility(View.VISIBLE);
                        }else {
                            cardViewImage.setVisibility(View.VISIBLE);
                            cardViewPlayer.setVisibility(View.GONE);
                            setImageCCTV(key_id,videoUri);
                        }
                        mAdapter = new CctvSegmentAdapter(CctvViewRuas.this, mItems,listener,row_index,handlerCctv);
                        dataRCv.setAdapter(mAdapter);
                    }
                    loadingDialog.hideLoadingDialog();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.hideLoadingDialog();
                    swipeRefreshLayout.setRefreshing(false);

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
                loadingDialog.hideLoadingDialog();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    @OptIn(markerClass = UnstableApi.class) private void setHLS(Uri videouri,String key_id){
        Handler test = new Handler();

        Log.d(TAG, "setHLS: " + videouri);
        loading.setVisibility(View.GONE);
        if (imageCCTV.getVisibility() == View.VISIBLE){

            cardViewImage.setVisibility(View.GONE);
            cardViewPlayer.setVisibility(View.VISIBLE);
        }
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
                hlserror = false;
                if (isPlaying){
                    Log.d(TAG, "HLS stream kembali online.");
                    cardViewImage.setVisibility(View.GONE);
                    cardViewPlayer.setVisibility(View.VISIBLE);
                    isRetryingHLS = false;

                    if (handlerCctv != null){
                        handlerCctv.removeCallbacksAndMessages(null);
                    }
                    if (handlerRetryHLS != null){
                        handlerRetryHLS.removeCallbacks(retryRunnableHLS);
                    }
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                hlserror = true;
                Log.d(TAG, "onPlayerError: " + error);
                if (handlerCctv != null){
                    handlerCctv.removeCallbacksAndMessages(null);
                }

                // Pindah ke CCTV kedua
                cardViewImage.setVisibility(View.VISIBLE);
                cardViewPlayer.setVisibility(View.GONE);
                setImageCCTV(key_id,videouri);
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                if (hour == 0 && minute == 0 && second == 5) {
                    // Jika jam 00:10, refresh data
                    swipeRefreshLayout.setRefreshing(true);
                    refreshData();
                } else {
                    // Jika tidak, lakukan retry HLS
                    if (!isRetryingHLS) {
                        isRetryingHLS = true;

                        // Coba ulangi streaming HLS setelah beberapa detik
                        handlerRetryHLS = new Handler();
                        retryRunnableHLS = new Runnable() {
                            @Override
                            public void run() {
                                if (hlserror && cctv2Error) {
                                    //Jika HLS dan cctv2Error maka refresh data
                                    Log.d(TAG, "HLS DAN CCTV 2 Error, sekarang refresh data.");
                                    swipeRefreshLayout.setRefreshing(true);
                                    refreshData();
                                    retryCountHLS = 0;  // Reset ulang jumlah percobaan
                                    isRetryingHLS = false;

                                } else {
                                    //Kalau salah satu tidak berarti retry hls
                                    Log.d(TAG, "Mencoba ulang HLS stream (Percobaan ke-" + retryCountHLS + ")... " + videouri);
                                    setHLS(videouri, key_id);
                                    // Ulangi pengecekan setiap 10 detik
                                    handlerRetryHLS.postDelayed(this, 7000);
                                }
                            }
                        };

                        // Ulangi pengecekan setiap 10 detik
                        handlerRetryHLS.postDelayed(retryRunnableHLS, 7000);
                    }
                }
            }
        });
    }
    private void setImageCCTV(String key_id,Uri videouri){
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
                        cctv2Error = true;
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        loadingIMG.setVisibility(View.GONE);
                        cctv2Error = false;
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
        if (handlerRetryHLS != null){
            handlerRetryHLS.removeCallbacks(retryRunnableHLS);
        }
        handlerCctv.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handlerRetryHLS != null){
            handlerRetryHLS.removeCallbacks(retryRunnableHLS);
        }
        handlerCctv.removeCallbacksAndMessages(null);
    }
}
