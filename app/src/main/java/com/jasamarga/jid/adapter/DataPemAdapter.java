package com.jasamarga.jid.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.jasamarga.jid.R;
import com.jasamarga.jid.models.DataPemeliharaanModel;

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
        String status = item.getKeteranganStatus().toLowerCase().contains("selesai") ? "Selesai" : item.getKeteranganStatus().toLowerCase().contains("proses") ? "Proses" : "";
        holder.titleLokasi.setText(item.getNamaRuas());
        holder.status.setText(status);
        holder.date.setText(item.getTanggalEntri());
        holder.km.setText(item.getKm());
        holder.jalur.setText(item.getJalur());
        holder.area.setText(item.getLajur());


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
}
