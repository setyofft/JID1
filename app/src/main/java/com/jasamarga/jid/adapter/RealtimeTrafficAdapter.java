package com.jasamarga.jid.adapter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.jasamarga.jid.R;
import com.jasamarga.jid.models.RTJalurAB;
import com.jasamarga.jid.models.RealtimeTrModel;

import java.util.ArrayList;
import java.util.List;

public class RealtimeTrafficAdapter extends RecyclerView.Adapter<RealtimeTrafficAdapter.ViewHolder> {

    private List<RealtimeTrModel.DataItem> dataList;
    private Context context;

    public RealtimeTrafficAdapter(List<RealtimeTrModel.DataItem> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_realtime_traffic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RealtimeTrModel.DataItem data = dataList.get(position);
        ArrayList<BarEntry> jalurEntry = new ArrayList<>();
        jalurEntry.add(new BarEntry(1,Float.parseFloat(String.valueOf(data.getKecGoogle()))));

        BarDataSet selesaiDataSet = new BarDataSet(jalurEntry, "Selesai");
        selesaiDataSet.setColor(holder.itemView.getContext().getResources().getColor(R.color.legend_selesai));
        BarData barData2 = new BarData(selesaiDataSet);
        barData2.setBarWidth(0.3f);

        holder.barChart.setData(barData2);
//        RTJalurAB.DataJalurA jalurA = data.getDataA();
//        RTJalurAB.DataJalurB jalurB = data.getDataB();
        // Set data to views in your card layout
//        Gson gs = new Gson();
//        Log.d("Adapter", "onBindViewHolder: " + gs.toJson(jalurB));
        holder.lokasiTextView.setText(data.getNamaSegment());
//        holder.switchLokasi.setChecked(data.isSwitchChecked());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lokasiTextView,date;
        SwitchCompat switchLokasi;
        BarChart barChart;
        // Add other views from your layout
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lokasiTextView = itemView.findViewById(R.id.lokasi);
            switchLokasi = itemView.findViewById(R.id.switchLokasi);
            switchLokasi.setVisibility(View.GONE);
            barChart = itemView.findViewById(R.id.chart_bar);
            date = itemView.findViewById(R.id.date);
            // Initialize other views
        }
    }
}
