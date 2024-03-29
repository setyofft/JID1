package com.jasamarga.jid.adapter;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.jasamarga.jid.R;
import com.jasamarga.jid.models.CctvSegmentModel;
import com.jasamarga.jid.service.ServiceFunction;

import java.util.ArrayList;

public class CctvViewPagerAdapter extends RecyclerView.Adapter<CctvViewPagerAdapter.CctvViewHolder>{

    private ArrayList<CctvSegmentModel> models;
    private ViewPager2 viewPager2;
    Context context;
    int row ;
    Handler handler_cctv;
    private String img_url;

    public CctvViewPagerAdapter(ArrayList<CctvSegmentModel> models, ViewPager2 viewPager2,String status,Context context,Handler handler) {
        this.context = context;
        this.models = models;
        this.handler_cctv = handler;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public CctvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CctvViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_maps_cctv,parent,false));
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull CctvViewHolder holder, int position) {
        final CctvSegmentModel cctvSegmentModel = models.get(position);
        holder.km.setText(cctvSegmentModel.getKm());
        holder.lokasi.setVisibility(View.VISIBLE);
        holder.lokasi.setText(cctvSegmentModel.getNamaSegment());
        for (CctvSegmentModel item : models){
            Log.d(TAG, "onBindViewHolder: "+item.getNamaSegment());
        }
        if(cctvSegmentModel.getStatus().equals("0")){
            holder.cctv_off.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        }else{
            if(row == position){
                holder.cctv_off.setVisibility(View.GONE);
                handler_cctv.postDelayed(new Runnable(){
                    public void run(){
                        img_url = "https://jid.jasamarga.com/cctv2/"+cctvSegmentModel.getKeyId()+"?tx="+Math.random();
                        ServiceFunction.initStreamImg(context,img_url,cctvSegmentModel.getKeyId(), holder.cctvImage, holder.progressBar);
                        handler_cctv.postDelayed(this, 0);
                    }
                }, 0);
            }else {
                holder.cctv_off.setText("Play");
                holder.cctv_off.setTextColor(Color.WHITE);
                holder.cctv_off.setOnClickListener(v->{
                    handler_cctv.removeCallbacksAndMessages(null);
                    row = holder.getLayoutPosition();
                    notifyItemChanged(row);
                });
                holder.progressBar.setVisibility(View.GONE);
            }
        }

        holder.btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler_cctv.removeCallbacksAndMessages(null);

            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
    class CctvViewHolder extends RecyclerView.ViewHolder{
        public ImageView cctvImage;
        public ProgressBar progressBar;
        public TextView km,lokasi,cctv_off,btn_close,tutup;

        public CctvViewHolder(@NonNull View itemView) {
            super(itemView);
            cctvImage = itemView.findViewById(R.id.showImg);
            km = itemView.findViewById(R.id.txt_nmKm);
            lokasi = itemView.findViewById(R.id.nm_lokasi);
            btn_close= itemView.findViewById(R.id.btn_close);
            progressBar = itemView.findViewById(R.id.loadingIMG);
            cctv_off = itemView.findViewById(R.id.set_cctv_off);
            btn_close.setVisibility(View.GONE);
        }

    }
}
