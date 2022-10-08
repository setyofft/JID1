package com.pim.jid.adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pim.jid.R;
import com.pim.jid.models.ModelListGangguan;

import org.json.JSONObject;

import java.util.ArrayList;


public class TableView extends RecyclerView.Adapter<TableView.ViewHolder> {
    private ArrayList<ModelListGangguan> listGangguan;

    @SuppressLint("NotifyDataSetChanged")
    public TableView(ArrayList<ModelListGangguan> listGangguan) {
        this.listGangguan = listGangguan;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position ==0){
            holder.arah_lajur.setTypeface(holder.arah_lajur.getTypeface(), Typeface.BOLD);
            holder.km.setTypeface(holder.km.getTypeface(), Typeface.BOLD);
            holder.nama_ruas.setTypeface(holder.nama_ruas.getTypeface(), Typeface.BOLD);
            holder.dampak.setTypeface(holder.dampak.getTypeface(), Typeface.BOLD);

            holder.arah_lajur.setText("Arah");
            holder.km.setText("KM");
            holder.nama_ruas.setText("Ruas");
            holder.dampak.setText("Dampak");
        }
//        Log.d(TAG, "onBindViewHolder: " + listGangguan.get(position -1).getNama_ruas());
        else{
            ModelListGangguan list = listGangguan.get(position- 1);
            holder.linearLayout.setBackgroundColor(Color.WHITE);
            holder.dampak.setTextColor(Color.BLACK);
            holder.km.setTextColor(Color.BLACK);
            holder.nama_ruas.setTextColor(Color.BLACK);
            holder.arah_lajur.setTextColor(Color.BLACK);

            holder.nama_ruas.setTextSize(11);
            holder.arah_lajur.setTextSize(11);
            holder.km.setTextSize(11);
            holder.dampak.setTextSize(11);
//            holder.km.setWidth(9);
//            holder.nama_ruas.setWidth(9);
//            holder.dampak.setWidth(9);

            holder.arah_lajur.setText(list.getArah_lajur());
            holder.km.setText(list.getKm());
            holder.nama_ruas.setText(list.getNama_ruas());
            holder.dampak.setText(list.getDampak());
        }
    }

    @Override
    public int getItemCount() {
        return listGangguan.size() +1;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView km,dampak,nama_ruas,arah_lajur;
        LinearLayout linearLayout;
        ViewHolder(View itemView) {
            super(itemView);
            km = itemView.findViewById(R.id.km);
            dampak = itemView.findViewById(R.id.dampak);
            nama_ruas = itemView.findViewById(R.id.ruas);
            arah_lajur = itemView.findViewById(R.id.arah);
            linearLayout = itemView.findViewById(R.id.list_back);
        }
    }
}
