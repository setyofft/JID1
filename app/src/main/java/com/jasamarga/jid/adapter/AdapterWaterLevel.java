package com.jasamarga.jid.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jasamarga.jid.R;
import com.jasamarga.jid.models.DataWaterLevel;
import com.jasamarga.jid.service.ServiceFunction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AdapterWaterLevel extends RecyclerView.Adapter<AdapterWaterLevel.ViewHolder> {
    private List<DataWaterLevel.Data> itemList;
    private Context context;
    private Handler handler_cctv;
    private LayoutInflater layoutInflater;
    private OnEyeButtonClickListener listener;


    public AdapterWaterLevel(List<DataWaterLevel.Data> itemList, Context context,LayoutInflater layoutInflater,OnEyeButtonClickListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.listener = listener;
        this.handler_cctv = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_water_level, parent, false);
        return new ViewHolder(view);
    }
    int normal = Color.argb(255, 34, 197, 94);

    int siaga1 = Color.argb(255, 234, 179, 8);
    int siaga2 = Color.argb(255, 249, 115, 22);
    int siaga3 = Color.argb(255, 239, 68, 68);
    int awas = Color.argb(255, 153, 27, 27);

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataWaterLevel.Data item = itemList.get(position);
        // Bind data to the views

        holder.idVendor.setText(item.getKodeAlatVendor());
//        holder.date.setText(item.getWaktuUpdate());

        String inputDate = item.getWaktuUpdate();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        try {
            Date date = inputFormat.parse(inputDate);
            String formattedDate = outputFormat.format(date);
            holder.date.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the ParseException here
        }
        holder.angka.setText(String.valueOf(item.getLevelSensor()));
        holder.namaPompa.setText(item.getPompa());
        holder.namaRuas.setText(item.getNamaLokasi());
        holder.warning.setText(item.getLevel());
        holder.pesan_hujan.setText(item.getHujan());
        if (item.getLevel().toLowerCase().contains("disconnect")){
            holder.layout_angka.setBackgroundTintList(ColorStateList.valueOf(awas));
            holder.layout_pompa.setVisibility(View.GONE);
        }
        if (holder.layout_angka != null) {
            if (item.getLevel().equalsIgnoreCase("normal")) {
                holder.layout_angka.setBackgroundTintList(ColorStateList.valueOf(normal));
            } else if (item.getLevel().contains("1")) {
                holder.layout_angka.setBackgroundTintList(ColorStateList.valueOf(siaga1));
            } else if (item.getLevel().contains("2")) {
                holder.layout_angka.setBackgroundTintList(ColorStateList.valueOf(siaga2));
            } else if (item.getLevel().contains("3")) {
                holder.layout_angka.setBackgroundTintList(ColorStateList.valueOf(siaga3));
            } else {
                holder.layout_angka.setBackgroundTintList(ColorStateList.valueOf(awas));
            }
        }

        holder.eyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onEyeButtonClick(item.getUrlCctv(), item.getNamaLokasi(), item.getNamaRuas());
                }
            }
        });
        // Set up other views similarly
        // holder.eyeButton, holder.date, holder.namaPompa, holder.switchPompa, holder.onOffPompa, holder.namaRuas, holder.angka
    }


    public interface OnEyeButtonClickListener {
        void onEyeButtonClick(String urlCctv, String namaLokasi, String namaRuas);
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idVendor, date, namaPompa, onOffPompa, namaRuas, angka,warning,pesan_hujan;
        LinearLayout layout_pompa;
        ImageView eyeButton;
        Switch switchPompa;
        RelativeLayout layout_angka;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idVendor = itemView.findViewById(R.id.id_vendor);
            eyeButton = itemView.findViewById(R.id.btn_eye);
            date = itemView.findViewById(R.id.date);
            namaPompa = itemView.findViewById(R.id.nama_pompa);
            switchPompa = itemView.findViewById(R.id.switch_pompa);
            onOffPompa = itemView.findViewById(R.id.onoff_pompa);
            namaRuas = itemView.findViewById(R.id.nama_ruas);
            angka = itemView.findViewById(R.id.angka);
            warning = itemView.findViewById(R.id.warning);
            pesan_hujan = itemView.findViewById(R.id.pesan_hujan);
            layout_pompa = itemView.findViewById(R.id.layout_pompa);
            layout_angka = itemView.findViewById(R.id.layout_angka);
            onOffPompa.setVisibility(View.GONE);
        }
    }
}
