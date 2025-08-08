package com.jasamarga.jid.fragment;

import static com.jasamarga.jid.components.PopupDetailLalin.TAG;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.AdapterWaterLevel;
import com.jasamarga.jid.models.DataWaterLevel; // Pastikan model sudah diperbarui
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.LoadingDialog;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
// Tidak perlu import java.util.regex.Matcher; dan Pattern jika tidak lagi dipakai isHlsUrl()

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentWaterLevel extends Fragment {
    Sessionmanager sessionmanager;
    String token, scope;
    LoadingDialog loadingDialog;
    RecyclerView listData;
    AdapterWaterLevel adapterWaterLevel;
    private Handler handler_cctv; // Handler untuk refresh gambar statis
    private Handler handlerRetryHLS; // Handler untuk retry HLS
    private Runnable retryRunnableHLS;
    private ExoPlayer player; // ExoPlayer instance for HLS
    private boolean isRetryingHLS = false;
    private boolean cctvStreamError = false; // Menunjukkan apakah stream CCTV (HLS atau gambar) bermasalah
    private boolean hlsError = false; // Menunjukkan apakah HLS spesifik bermasalah

    // Simpan detail CCTV yang sedang aktif untuk penanganan fallback
    private String currentHlsUrl = null;
    private String currentCctv2Url = null; // Ini adalah url_cctv dari model
    private String currentNamaLokasi;
    private String currentNamaRuas;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_water_level, container, false);
        sessionmanager = new Sessionmanager(requireActivity());
        HashMap<String, String> userDetails = sessionmanager.getUserDetails();
        listData = view.findViewById(R.id.list_data);
        token = userDetails.get(Sessionmanager.nameToken);
        scope = userDetails.get(Sessionmanager.set_scope);
        handler_cctv = new Handler();
        handlerRetryHLS = new Handler(); // Inisialisasi handler untuk retry HLS
        loadingDialog = new LoadingDialog(requireActivity());
        getData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Pastikan untuk membersihkan handler dan player saat fragment dihancurkan
        if (handler_cctv != null) {
            handler_cctv.removeCallbacksAndMessages(null);
        }
        if (handlerRetryHLS != null) {
            handlerRetryHLS.removeCallbacksAndMessages(null);
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void getData() {
        loadingDialog.showLoadingDialog("Loading Data Water Level. . .");
        Log.d(TAG, "getDataPemeliharaan: " + token + scope);
        ReqInterface newService = ApiClientNew.getServiceNew();
        Call<DataWaterLevel> call = newService.getWaterLevet(token);
        String fullUrl = call.request().url().toString();
        Log.d(TAG, "getDataPemeliharaan URL: " + fullUrl);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        listData.setLayoutManager(linearLayoutManager);
        call.enqueue(new Callback<DataWaterLevel>() {
            @Override
            public void onResponse(Call<DataWaterLevel> call, Response<DataWaterLevel> response) {
                if (response.body() != null) {
                    DataWaterLevel data = response.body();
                    boolean result = data.isStatus();
                    Gson gson = new Gson();
                    if (result) {
                        Log.d(TAG, "onResponse: " + gson.toJson(data));
                        adapterWaterLevel = new AdapterWaterLevel(data.getDataList(), requireContext(), getLayoutInflater(), (urlCctv, namaLokasi, namaRuas, idRuas, keyId) -> {
                            // Simpan detail CCTV yang akan ditampilkan
                            currentNamaLokasi = namaLokasi;
                            currentNamaRuas = namaRuas;
                            currentCctv2Url = urlCctv; // Ini adalah URL untuk gambar statis

                            // Coba bangun URL HLS jika ada ID ruas dan Key ID
                            if (idRuas != null && idRuas != null && keyId != null) {
                                currentHlsUrl = "https://jmlive.jasamarga.com/hls/" + idRuas + "/" + keyId + "/index.m3u8";
                                Log.d(TAG, "Constructed HLS URL: " + currentHlsUrl);
                            } else {
                                currentHlsUrl = null; // Tidak ada HLS
                                Log.d(TAG, "No HLS URL can be constructed. Will use CCTV2.");
                            }

                            // Tampilkan dialog CCTV
                            showCctv(getLayoutInflater(), currentHlsUrl, currentCctv2Url, namaLokasi, namaRuas);
                        });
                        listData.setAdapter(adapterWaterLevel);
                    } else {
                        Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Error onResponse: " + response);
                }
                loadingDialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<DataWaterLevel> call, Throwable t) {
                Toast.makeText(getContext(), "Maaf ada kesalahan data " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.hideLoadingDialog();
                Log.d("Error Data Pemeliharaan", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showCctv(LayoutInflater inflater, String hlsUrl, String cctv2Url, String nmKM, String txtnama) {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
        alert.setCancelable(false);
        View dialoglayout = inflater.inflate(R.layout.dialog_maps_cctv, null);
        alert.setView(dialoglayout);

        ImageView img = dialoglayout.findViewById(R.id.showImg); // ImageView utama untuk gambar statis
        ProgressBar loadingIMG = dialoglayout.findViewById(R.id.loadingIMG);
        TextView nmKm = dialoglayout.findViewById(R.id.txt_nmKm);
        TextView txt_nama = dialoglayout.findViewById(R.id.nm_lokasi);
        TextView set_cctv_off = dialoglayout.findViewById(R.id.set_cctv_off);
        TextView btn_close = dialoglayout.findViewById(R.id.btn_close);
        PlayerView playerView = dialoglayout.findViewById(R.id.cctv_hls); // PlayerView untuk HLS
        ImageView imgStillBackup = dialoglayout.findViewById(R.id.image_cctv_backup); // ImageView backup untuk gambar statis

        final AlertDialog alertDialog = alert.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        nmKm.setText(nmKM);
        txt_nama.setText(txtnama);
        txt_nama.setVisibility(View.GONE);

        btn_close.setOnClickListener(v -> {
            // Bersihkan semua handler dan player saat dialog ditutup
            cleanCctvResources();
            alertDialog.cancel();
        });

        // Selalu bersihkan sumber daya sebelum menampilkan CCTV baru
        cleanCctvResources();

        // PRIORITAS: Coba HLS dulu
        if (hlsUrl != null && !hlsUrl.isEmpty()) {
            Log.d(TAG, "Attempting to play HLS: " + hlsUrl);
            playerView.setVisibility(View.VISIBLE);
            img.setVisibility(View.GONE);
//            imgStillBackup.setVisibility(View.GONE);
            loadingIMG.setVisibility(View.GONE);
            set_cctv_off.setVisibility(View.GONE);
            setupHLSPlayer(Uri.parse(hlsUrl), playerView, loadingIMG, set_cctv_off, imgStillBackup, cctv2Url);
        } else if (cctv2Url != null && !cctv2Url.isEmpty()) {
            // Jika HLS tidak ada, langsung ke CCTV2 (gambar statis)
            Log.d(TAG, "HLS not available, playing CCTV2 (still image): " + cctv2Url);
            playerView.setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE); // Gunakan img utama untuk CCTV2
//            imgStillBackup.setVisibility(View.GONE);
            loadingIMG.setVisibility(View.VISIBLE);
            set_cctv_off.setVisibility(View.GONE);
            startStillImageStream(cctv2Url, img, loadingIMG, set_cctv_off);
        } else {
            // Jika tidak ada URL CCTV sama sekali
            Log.d(TAG, "No CCTV URL (HLS or CCTV2) available.");
            playerView.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
//            imgStillBackup.setVisibility(View.GONE);
            loadingIMG.setVisibility(View.GONE);
            set_cctv_off.setText("Tidak ada stream CCTV tersedia untuk lokasi ini.");
            set_cctv_off.setVisibility(View.VISIBLE);
        }

        alertDialog.show();
    }

    private void cleanCctvResources() {
        if (handler_cctv != null) {
            handler_cctv.removeCallbacksAndMessages(null);
        }
        if (handlerRetryHLS != null) {
            handlerRetryHLS.removeCallbacksAndMessages(null);
        }
        if (player != null) {
            player.release();
            player = null;
        }
        isRetryingHLS = false;
        cctvStreamError = false;
        hlsError = false;
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setupHLSPlayer(Uri videoUri, PlayerView playerView, ProgressBar loadingIMG, TextView set_cctv_off, ImageView imgStillBackup, String cctv2UrlFallback) {
        hlsError = false;
        cctvStreamError = false;

        if (player != null) {
            player.release();
        }

        DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory();
        httpDataSourceFactory.setAllowCrossProtocolRedirects(true); // Izinkan redirect
        HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(httpDataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));

        player = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);
        player.setMediaSource(hlsMediaSource);
        player.prepare();
        player.setPlayWhenReady(true);

        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if (isPlaying) {
                    Log.d(TAG, "HLS stream kembali online.");
                    playerView.setVisibility(View.VISIBLE);
//                    imgStillBackup.setVisibility(View.GONE);
                    set_cctv_off.setVisibility(View.GONE);
                    loadingIMG.setVisibility(View.GONE);
                    isRetryingHLS = false;
                    hlsError = false;
                    cctvStreamError = false;

                    if (handlerRetryHLS != null) {
                        handlerRetryHLS.removeCallbacks(retryRunnableHLS);
                    }
                    if (handler_cctv != null) {
                        handler_cctv.removeCallbacksAndMessages(null);
                    }
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                Log.e(TAG, "HLS Player Error: " + error.getMessage() + " (ErrorCode: " + error.getErrorCodeName() + ")");
                hlsError = true;
                cctvStreamError = true;

                // Release HLS player immediately
                if (player != null) {
                    player.release();
                    player = null;
                }

                playerView.setVisibility(View.GONE);
                loadingIMG.setVisibility(View.VISIBLE);
                set_cctv_off.setText("CCTV tidak tersedia. Mencoba alternatif (gambar statis)...");
                set_cctv_off.setVisibility(View.VISIBLE);

                // Fallback ke gambar statis (CCTV2) menggunakan imgStillBackup
                if (cctv2UrlFallback != null && !cctv2UrlFallback.isEmpty()) {
                    Log.d(TAG, "Falling back to CCTV2 (still image): " + cctv2UrlFallback);
//                    imgStillBackup.setVisibility(View.VISIBLE);
                    startStillImageStream(cctv2UrlFallback, imgStillBackup, loadingIMG, set_cctv_off);
                } else {
                    Log.d(TAG, "No CCTV2 fallback URL available.");
                    set_cctv_off.setText("CCTV tidak tersedia. Tidak ada alternatif.");
                    loadingIMG.setVisibility(View.GONE);
//                    imgStillBackup.setVisibility(View.GONE); // Pastikan tersembunyi jika tidak ada fallback
                }

                // Mulai logika retry HLS di latar belakang
                if (!isRetryingHLS) {
                    isRetryingHLS = true;
                    handlerRetryHLS = new Handler();
                    retryRunnableHLS = () -> {
                        Log.d(TAG, "Retrying HLS stream... " + videoUri);
                        set_cctv_off.setText("CCTV tidak tersedia. Mencoba HLS ulang...");
                        setupHLSPlayer(videoUri, playerView, loadingIMG, set_cctv_off, imgStillBackup, cctv2UrlFallback);
                        // Coba lagi setelah 7 detik
                        handlerRetryHLS.postDelayed(retryRunnableHLS, 7000);
                    };
                    handlerRetryHLS.postDelayed(retryRunnableHLS, 7000); // Mulai retry setelah 7 detik
                }
            }
        });
    }

    private void startStillImageStream(String img_url, ImageView img, ProgressBar loadingIMG, TextView set_cctv_off) {
        if (handler_cctv != null) {
            handler_cctv.removeCallbacksAndMessages(null);
        }

        img.setVisibility(View.VISIBLE);
        loadingIMG.setVisibility(View.VISIBLE);
        set_cctv_off.setVisibility(View.GONE);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String urlWithCacheBuster = img_url + "?t=" + System.currentTimeMillis();
                initStreamImg(urlWithCacheBuster, img, loadingIMG, set_cctv_off);
                handler_cctv.postDelayed(this, 300);
            }
        };
        handler_cctv.post(runnable);
    }

    private void initStreamImg(String img_url, ImageView img, ProgressBar loadingIMG, TextView set_cctv_off) {
        Glide.with(requireContext())
                .asBitmap()
                .load(img_url)
                .override(350, 200)
                .centerCrop()
                .centerInside()
                .dontAnimate()
                .error(R.drawable.blank_photo)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        Log.e(TAG, "Still Image Load Failed: " + (e != null ? e.getMessage() : "Unknown error"));
                        loadingIMG.setVisibility(View.VISIBLE);
                        set_cctv_off.setText("Gambar CCTV tidak tersedia.");
                        set_cctv_off.setVisibility(View.VISIBLE);
                        cctvStreamError = true; // Set status stream keseluruhan error
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        loadingIMG.setVisibility(View.GONE);
                        set_cctv_off.setVisibility(View.GONE);
                        cctvStreamError = false;
                        return false;
                    }
                })
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource,
                                                @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            img.setImageBitmap(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable android.graphics.drawable.Drawable placeholder) {
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            img.setImageDrawable(placeholder);
                        }
                    }
                });
    }
}