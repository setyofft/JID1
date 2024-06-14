package com.jasamarga.jid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.jasamarga.jid.R;
import com.jasamarga.jid.components.PopupDetailLalin;
import com.jasamarga.jid.models.ModelListGangguan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class TableView extends RecyclerView.Adapter<TableView.ViewHolder> {
    private ArrayList<ModelListGangguan> listGangguan;
    String lalin;
    private Context context;
    int colorText,backTint;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format3 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    Date date;

    @SuppressLint("NotifyDataSetChanged")
    public TableView(ArrayList<ModelListGangguan> listGangguan,Context context,String lalin) {
        this.listGangguan = listGangguan;
        this.context = context;
        this.lalin = lalin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_list, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = holder.getLayoutPosition();
        if(pos ==0){
            holder.km.setTypeface(holder.km.getTypeface(), Typeface.BOLD);
            holder.nama_ruas.setTypeface(holder.nama_ruas.getTypeface(), Typeface.BOLD);
            holder.dampak.setTypeface(holder.dampak.getTypeface(), Typeface.BOLD);
//            holder.status.setTypeface(holder.status.getTypeface(), Typeface.BOLD);
            int size = 11;
            holder.km.setTextSize(size);
            holder.nama_ruas.setTextSize(size);
            holder.dampak.setTextSize(size);
//            holder.status.setTextSize(size);

            holder.km.setText("Ruas/KM");
            holder.nama_ruas.setText("Waktu");
            holder.dampak.setText("Jenis");
            holder.indicator.setBackgroundColor(Color.TRANSPARENT);
//            holder.status.setText("Status");
        }
//        Log.d(TAG, "onBindViewHolder: " + listGangguan.get(position -1).getNama_ruas());
        else{
            holder.km.setMaxLines(3);
            ModelListGangguan list = listGangguan.get(pos- 1);
            backColor(list.getStatus());
            holder.linearLayout.setBackgroundColor(Color.WHITE);
            holder.dampak.setTextColor(colorText);
            holder.km.setTextColor(colorText);
            holder.nama_ruas.setTextColor(colorText);
            holder.indicator.setBackgroundTintList(ContextCompat.getColorStateList(context,backTint));
            try {
                date = format.parse(list.getWaktu());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.km.setText(list.getNama_ruas2() +"\n"+list.getKm() + " " + list.getJalur());
            holder.nama_ruas.setText(format2.format(date));
            holder.dampak.setText(list.getTipe());
            holder.itemView.setOnClickListener(v ->{
                holder.linearLayout.setElevation(20);
                PopupDetailLalin popupDetailLalin = new PopupDetailLalin(list,lalin);
                popupDetailLalin.show(((AppCompatActivity) context).getSupportFragmentManager(),PopupDetailLalin.TAG);
                holder.linearLayout.setElevation(0);
            });
        }
    }
    public int backColor(String s){
        colorText = Color.BLACK;
        if(s.toLowerCase().contains("dalam penanganan") || s.toLowerCase().contains("dalam proses")){
            colorText = context.getResources().getColor(R.color.black);
            backTint = R.color.status_onProg;
            return ContextCompat.getColor(context,R.color.status_onProg);
        }else if(s.toLowerCase().contains("dalam rencana") || s.toLowerCase().contains("belum ditangani")){
            colorText = context.getResources().getColor(R.color.black);
            backTint = R.color.status_none;

            return ContextCompat.getColor(context,R.color.status_none);
        }else if (s.toLowerCase().contains("selesai")){
            colorText = context.getResources().getColor(R.color.black);
            backTint = R.color.status_done;

            return ContextCompat.getColor(context,R.color.status_done);
        }
        return ContextCompat.getColor(context,R.color.white);
    }
    @Override
    public int getItemCount() {
        return listGangguan.size() +1;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView km,dampak,nama_ruas,arah_lajur,status;
        MaterialButton indicator;
        LinearLayout linearLayout;
        ViewHolder(View itemView) {
            super(itemView);
            km = itemView.findViewById(R.id.km);
            dampak = itemView.findViewById(R.id.dampak);
            nama_ruas = itemView.findViewById(R.id.ruas);
            linearLayout = itemView.findViewById(R.id.list_back);
            indicator = itemView.findViewById(R.id.indicatorStatus);
//            status = itemView.findViewById(R.id.status);
        }
    }
}
