package com.jasamarga.jid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.jasamarga.jid.R;
import com.jasamarga.jid.components.PopupDetailDataGangguan;
import com.jasamarga.jid.components.PopupDetailDataPem;
import com.jasamarga.jid.components.PopupDetailLalin;
import com.jasamarga.jid.models.DataPemeliharaanModel;
import com.jasamarga.jid.models.ModelGangguanLalin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataGangguanAdapter extends RecyclerView.Adapter<DataGangguanAdapter.ViewHolder> {

    private List<ModelGangguanLalin.GangguanData> itemList;
    private Context context;

    public DataGangguanAdapter(List<ModelGangguanLalin.GangguanData> itemList, Context context) {
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
        ModelGangguanLalin.GangguanData item = itemList.get(position);
        String inputDate = item.getWaktuKejadian();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//        String status = item.getKetStatus().toLowerCase().contains("selesai") ? "Selesai" : item.getKetStatus().toLowerCase().contains("proses") ? "Proses" : "";
        holder.titleLokasi.setText(item.getNamaRuas());
        holder.status.setBackgroundColor(item.getKetStatus().equalsIgnoreCase("selesai") ? context.getResources().getColor(R.color.status_done) : context.getResources().getColor(R.color.status_onProgress));
        holder.status.setText(item.getKetStatus());
        try {
            Date date = inputFormat.parse(inputDate);
            String formattedDate = outputFormat.format(date);
            holder.date.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the ParseException here
        }
        holder.km.setText(item.getKm());
        holder.jalur.setText(item.getJalur());
        holder.area.setText(item.getLajur());
        holder.itemView.setOnClickListener(v ->{
            PopupDetailDataGangguan popupDetailLalin = new PopupDetailDataGangguan(item,"lalin");
            popupDetailLalin.show(((AppCompatActivity) context).getSupportFragmentManager(),PopupDetailLalin.TAG);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleLokasi, status, date, km, jalur, area;

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
    public void filterList(ArrayList<ModelGangguanLalin.GangguanData> filterList){
        if(filterList != null){
            itemList = filterList;
        }
        notifyDataSetChanged();
    }
}