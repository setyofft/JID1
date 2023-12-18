package com.jasamarga.jid.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jasamarga.jid.R;
import com.jasamarga.jid.models.DataWaterLevel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterWaterLevel extends RecyclerView.Adapter<AdapterWaterLevel.ViewHolder> {
    private List<DataWaterLevel.Data> itemList;
    private Context context;

    public AdapterWaterLevel(List<DataWaterLevel.Data> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_water_level, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataWaterLevel.Data item = itemList.get(position);
        // Bind data to the views

        holder.idVendor.setText(item.getKodeAlatVendor());
//        holder.date.setText(item.getWaktuUpdate());

        String inputDate = item.getWaktuUpdate();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
            holder.layout_pompa.setVisibility(View.GONE);
        }
        // Set up other views similarly
        // holder.eyeButton, holder.date, holder.namaPompa, holder.switchPompa, holder.onOffPompa, holder.namaRuas, holder.angka
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
            onOffPompa.setVisibility(View.GONE);
        }
    }
}
