package com.pim.jid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.pim.jid.R;
import com.pim.jid.models.CctvSegmentModel;
import com.pim.jid.service.ServiceFunction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CctvSegmentAdapter extends RecyclerView.Adapter<CctvSegmentAdapter.ViewProcessHolder>{
    Context context;
    String img_url;
    Handler handler_cctv;
    int limit = 300;

    private RecyclerViewClickListener  listener;
    private ArrayList<CctvSegmentModel> item;
    private int row_index =-1;

    public CctvSegmentAdapter(Context context, ArrayList<CctvSegmentModel> item , RecyclerViewClickListener listener , int row_index, Handler handler) {
        this.context = context;
        this.item = item;
        this.listener = listener;
        this.row_index = row_index;
        this.handler_cctv = handler;
    }

    @Override
    public CctvSegmentAdapter.ViewProcessHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_card_imagecctv, parent, false);
        return new CctvSegmentAdapter.ViewProcessHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CctvSegmentAdapter.ViewProcessHolder holder, int position) {

        final CctvSegmentModel data = item.get(position);
        holder.nmLokasi.setText(data.getNamaSegment());
        holder.nmKm.setText("SS KM "+data.getKm());
        if(data.getStatus().equals("0")){
            holder.set_cctv_off.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        }else{
            holder.set_cctv_off.setVisibility(View.GONE);
            handler_cctv.postDelayed(new Runnable(){
                public void run(){

                    ServiceFunction.initStreamImg(context,img_url, data.getKeyId(),holder.imgCCTV, holder.progressBar);
                    handler_cctv.postDelayed(this, 2000);
                }
            }, 300);
        }

        if(row_index == position){
                holder.cardView.setCardBackgroundColor(Color.parseColor("#DFEFFF"));

            }else{
                holder.cardView.setCardBackgroundColor(Color.WHITE);

            }
        holder.cardView.setRadius(20);

    }

    @Override
    public int getItemCount() {
        return item.size();
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
            row_index=getLayoutPosition();
            listener.onClick(view,getLayoutPosition());
        }
    }
    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }
}
