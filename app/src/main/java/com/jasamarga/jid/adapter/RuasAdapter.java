package com.jasamarga.jid.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.jasamarga.jid.R;
import com.jasamarga.jid.models.RuasModel;
import com.jasamarga.jid.views.CctvRuas;
import com.jasamarga.jid.views.CctvViewRuas;
import com.jasamarga.jid.views.MapsNew;

import java.util.ArrayList;
import java.util.Locale;

public class RuasAdapter extends RecyclerView.Adapter<RuasAdapter.ViewProcessHolder>{
    Context context;
    private ArrayList<RuasModel> item;

    public RuasAdapter(Context context, ArrayList<RuasModel> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public RuasAdapter.ViewProcessHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cctv_costum, parent, false);
        return new RuasAdapter.ViewProcessHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuasAdapter.ViewProcessHolder holder, int position) {
        final RuasModel data = item.get(position);
        holder.itemView.setTag(position);
        holder.title.setText(data.getNama_ruas());
        holder.button_maps.setVisibility(View.GONE);
        holder.button_maps.setOnClickListener(view -> {
            String nama = item.get(position).getNama_ruas();
            String id = item.get(position).getId_ruas();
            Intent intent = new Intent(context,MapsNew.class);
            intent.putExtra("judul_ruas",nama);
            intent.putExtra("id_ruas",id);
            Activity context = (Activity) this.context;
            context.overridePendingTransition(0,0);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }


    public class ViewProcessHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, title2;
        public MaterialButton button_maps;

        public ViewProcessHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.title);
            button_maps = itemView.findViewById(R.id.button_maps);
        }

        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            Intent intent;
            if(item.get(position).getNama_ruas().toLowerCase().contains("demak")){
                intent = new Intent(context, CctvViewRuas.class);
                intent.putExtra("judul_segment","Semua Segment");
                intent.putExtra("id_ruas",item.get(position).getId_ruas());
                intent.putExtra("id_segment","0");

                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(0,0);
            }
            else if(item.get(position).getNama_ruas().toLowerCase().contains("jjs")){
                intent = new Intent(context, CctvViewRuas.class);
                intent.putExtra("judul_segment","Semua Segment");
                intent.putExtra("id_ruas",item.get(position).getId_ruas());
                intent.putExtra("id_segment","0");

                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(0,0);
            }
            else if(item.get(position).getNama_ruas().toLowerCase().contains("bptj")){
                intent = new Intent(context, CctvViewRuas.class);
                intent.putExtra("judul_segment","Semua Segment");
                intent.putExtra("id_ruas",item.get(position).getId_ruas());
                intent.putExtra("id_segment","0");

                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(0,0);
            }
            else{
                String nama = item.get(position).getNama_ruas();
                String id = item.get(position).getId_ruas();
                intent = new Intent(context,CctvRuas.class);
                intent.putExtra("judul_ruas",nama);
                intent.putExtra("id_ruas",id);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(0,0);
            }

        }
    }

    public void filterList(ArrayList<RuasModel> filterList){
        if(filterList != null){
            item = filterList;
        }
        notifyDataSetChanged();
    }
}
