package com.pim.jid.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.pim.jid.R;
import com.pim.jid.models.CcctvSegmentModel;
import com.pim.jid.models.SegmentModel;
import com.pim.jid.views.CctvViewRuas;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CctvSegmentAdapter extends RecyclerView.Adapter<CctvSegmentAdapter.ViewProcessHolder>{
    Context context;
    String img_url;
    Handler handler_cctv;

    private RecyclerViewClickListener  listener;
    private ArrayList<CcctvSegmentModel> item;
    private int row_index =-1;

    public CctvSegmentAdapter(Context context, ArrayList<CcctvSegmentModel> item ,RecyclerViewClickListener listener , int row_index) {
        this.context = context;
        this.item = item;
        this.listener = listener;
        this.row_index = row_index;
    }

    @Override
    public CctvSegmentAdapter.ViewProcessHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_card_imagecctv, parent, false);

        return new CctvSegmentAdapter.ViewProcessHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CctvSegmentAdapter.ViewProcessHolder holder, int position) {

        final CcctvSegmentModel data = item.get(position);
        handler_cctv = new Handler();
        img_url = "https://jid.jasamarga.com/cctv2/"+data.getKeyId()+"?tx="+Math.random();
        holder.nmLokasi.setText(data.getNamaSegment());
        holder.nmKm.setText("SS KM "+data.getKm());
        if(data.getStatus().equals("0")){
            holder.set_cctv_off.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        }else{
            holder.set_cctv_off.setVisibility(View.GONE);
            img_url = "https://jid.jasamarga.com/cctv2/"+data.getKeyId()+"?tx="+Math.random();
            initStreamImg(img_url,holder.imgCCTV, holder.progressBar);
            handler_cctv.postDelayed(new Runnable(){
                public void run(){
                    img_url = "https://jid.jasamarga.com/cctv2/"+data.getKeyId()+"?tx="+Math.random();
                    initStreamImg(img_url, holder.imgCCTV, holder.progressBar);
                    handler_cctv.postDelayed(this, 1000);
                }
            }, 300);
        }
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onClick(View view) {
//                row_index=holder.getLayoutPosition();
//                notifyDataSetChanged();
//            }
//        });

        if(row_index == position){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#C0C0C0"));
        }else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }
        holder.cardView.setRadius(20);

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    private void initStreamImg(String img_url, ImageView img, ProgressBar loadingIMG) {
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(img_url)
                .override(350, 200)
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

    public class ViewProcessHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imgCCTV;
        public CardView cardView;
        public TextView nmKm,nmLokasi,set_cctv_off;
        public ProgressBar progressBar;

        public ViewProcessHolder(@NonNull View itemView) {
            super(itemView);
            imgCCTV = itemView.findViewById(R.id.image_cctv);
            nmKm = itemView.findViewById(R.id.txt_nmKm);
            nmLokasi = itemView.findViewById(R.id.nm_lokasi);
            cardView = itemView.findViewById(R.id.card);
            progressBar = itemView.findViewById(R.id.loadingIMG);
            set_cctv_off = itemView.findViewById(R.id.set_cctv_off);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getLayoutPosition());
        }
    }
    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }
}
