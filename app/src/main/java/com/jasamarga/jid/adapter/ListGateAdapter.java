package com.jasamarga.jid.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.Section;
import com.jasamarga.jid.R;
import com.jasamarga.jid.models.DataGate;

import java.util.List;

public class ListGateAdapter extends RecyclerView.Adapter<ListGateAdapter.ViewHolder>{
    List<DataGate.GateData> data;
    Context context;

    public ListGateAdapter(List<DataGate.GateData> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ListGateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_antiran_g, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListGateAdapter.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();  // Dapatkan context dari itemView

        DataGate.GateData gateData = data.get(position);
        holder.lokasi.setText(gateData.getNamaGerbang());
        int rawSpeed = gateData.getMeterAntrian() / 10;
//        holder.speed.setSpeedAt(3000);
        holder.speed.speedTo(50f,1000);
        holder.meter.setText(gateData.getMeterAntrian() + " " + "M");
//        float maxSpeed = 3000f; // Nilai maksimum yang diinginkan
        holder.speed.setMaxSpeed(150);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lokasi,legend,meter;
        View circleLegend;
        SpeedView speed;
        public ViewHolder(View view) {
            super(view);
            lokasi = view.findViewById(R.id.lokasi);
            legend = view.findViewById(R.id.Tlegend_selesai);
//            circleLegend = view.findViewById(R.id.legend);
            speed = view.findViewById(R.id.speedView);
            meter = view.findViewById(R.id.meter);
        }
    }
}
