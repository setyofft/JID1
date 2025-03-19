package com.jasamarga.jid.fragment;

import static com.jasamarga.jid.components.PopupDetailLalin.TAG;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
import com.jasamarga.jid.adapter.DataPemAdapter;
import com.jasamarga.jid.models.DataPemeliharaanModel;
import com.jasamarga.jid.models.DataWaterLevel;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.views.Maps;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentWaterLevel extends Fragment {
    Sessionmanager sessionmanager;
    String token,scope;
    LoadingDialog loadingDialog;
    RecyclerView listData;
    AdapterWaterLevel adapterWaterLevel;
    private Handler handler_cctv;

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
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                // Perform your periodic task here
//                initStreamImg("https://jid.jasamarga.com/cctv2/4faadb9?v=" + Math.random(), imgShw, loadingIMG);
//                // Post the same runnable again with a delay (300 milliseconds in this case)
//                handler_cctv.postDelayed(this, 300);
//            }
//        };
//        handler_cctv.post(runnable);
        loadingDialog = new LoadingDialog(requireActivity());
        getData();
        return view;
    }
    public void getData(){
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
                if (response.body() != null){
                    DataWaterLevel data = response.body();
                    boolean result = data.isStatus();
                    Gson gson = new Gson();
                    if (result){
                        Log.d(TAG, "onResponse: " + gson.toJson(data));
                        adapterWaterLevel = new AdapterWaterLevel(data.getDataList(), requireContext(), getLayoutInflater(), new AdapterWaterLevel.OnEyeButtonClickListener() {
                            @Override
                            public void onEyeButtonClick(String urlCctv, String namaLokasi, String namaRuas) {
                                showCctv(getLayoutInflater(),urlCctv,namaLokasi,namaRuas);
                            }
                        });
                        listData.setAdapter(adapterWaterLevel);
                    }else {
                        Toast.makeText(requireContext(),response.message(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.d(TAG, "Error onResponse: " + response);

                }
                loadingDialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<DataWaterLevel> call, Throwable t) {
                Toast.makeText(getContext(),"Maaf ada kesalahan data " +t.getMessage(),Toast.LENGTH_SHORT).show();
                loadingDialog.hideLoadingDialog();
                Log.d("Error Data Pemeliharaan", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
    public void showCctv(LayoutInflater inflater,String img_url,String nmKM , String txtnama){
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
        alert.setCancelable(false);
        View dialoglayout = inflater.inflate(R.layout.dialog_maps_cctv, null);
        alert.setView(dialoglayout);
        ImageView img = dialoglayout.findViewById(R.id.showImg);
        ProgressBar loadingIMG= dialoglayout.findViewById(R.id.loadingIMG);
        TextView nmKm = dialoglayout.findViewById(R.id.txt_nmKm);
        TextView txt_nama = dialoglayout.findViewById(R.id.nm_lokasi);
        TextView set_cctv_off = dialoglayout.findViewById(R.id.set_cctv_off);
        TextView btn_close = dialoglayout.findViewById(R.id.btn_close);
        final AlertDialog  alertDialog = alert.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nmKm.setText(nmKM);
        txt_nama.setText(txtnama);
        txt_nama.setVisibility(View.GONE);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler_cctv.removeCallbacksAndMessages(null);
                alertDialog.cancel();
            }
        });
        set_cctv_off.setVisibility(View.GONE);
        Log.d(TAG, "showCctv: " + img_url);
        Runnable runnable = new Runnable(){
            public void run(){
//                Log.d("CCTVHandler", "Handler running");
                initStreamImg(img_url+ "?v=" + Math.random(), img, loadingIMG);
                handler_cctv.postDelayed(this, 300);
            }
        };
        handler_cctv.post(runnable);
        alertDialog.show();

    }

    private void initStreamImg(String img_url, ImageView img, ProgressBar loadingIMG) {
        Glide.with(requireContext())
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
