package com.jasamarga.jid.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.button.MaterialButton;
import com.jasamarga.jid.R;
import com.jasamarga.jid.components.PopupDetailDataPem;
import com.jasamarga.jid.components.PopupDetailLalin;
import com.jasamarga.jid.models.DataPemeliharaanModel;
import com.jasamarga.jid.models.RuasModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataPemAdapter extends RecyclerView.Adapter<DataPemAdapter.ViewHolder> {

    private List<DataPemeliharaanModel.PemeliharaanData> itemList;
    private Context context;

    public DataPemAdapter(List<DataPemeliharaanModel.PemeliharaanData> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_pemeliharaan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataPemeliharaanModel.PemeliharaanData item = itemList.get(position);
        String inputDate = item.getWaktuAwal();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String status = item.getKeteranganStatus().toLowerCase().contains("selesai") ? "Selesai" :
                item.getKeteranganStatus().toLowerCase().contains("proses") || item.getKeteranganStatus().toLowerCase().contains("rencana") ? "Proses" : "";
        holder.titleLokasi.setText(item.getNamaRuas());
        holder.status.setEnabled(false);
        int color;
        if (status.equalsIgnoreCase("selesai")) {
            color = context.getResources().getColor(R.color.blueLight);
            holder.status.setTextColor(Color.WHITE);
        } else if (status.equalsIgnoreCase("proses")) {
            color = context.getResources().getColor(R.color.status_onProgress);
            holder.status.setTextColor(Color.BLACK);

        } else {
            color = context.getResources().getColor(android.R.color.transparent); // or any default color
        }

        holder.status.setBackgroundColor(color);

        holder.status.setText(status);
        try {
            Date date = inputFormat.parse(inputDate);
            String formattedDate = outputFormat.format(date);
            holder.date.setText("Waktu Awal : " + formattedDate);

        } catch (ParseException e) {
            e.printStackTrace(); // Handle the ParseException here
        }
        holder.km.setText("KM : "+item.getKm());
        holder.jalur.setText(item.getJalur());
        holder.area.setText("Lajur : "+item.getLajur());
        holder.itemView.setOnClickListener(v ->{
            PopupDetailDataPem popupDetailLalin = new PopupDetailDataPem(item,"lalin");
            popupDetailLalin.show(((AppCompatActivity) context).getSupportFragmentManager(),PopupDetailLalin.TAG);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleLokasi, date, km, jalur, area;
        MaterialButton status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleLokasi = itemView.findViewById(R.id.title_lokasi);
            status = itemView.findViewById(R.id.status);
            date = itemView.findViewById(R.id.date);
            km = itemView.findViewById(R.id.km);
            jalur = itemView.findViewById(R.id.jalur);
            area = itemView.findViewById(R.id.area);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<DataPemeliharaanModel.PemeliharaanData> filterList){
        if(filterList != null){
            itemList = filterList;
        }
        notifyDataSetChanged();
    }
}
