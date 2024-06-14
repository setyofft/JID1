package com.jasamarga.jid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.jasamarga.jid.R;
import com.jasamarga.jid.models.ModelRegion;
import com.jasamarga.jid.models.ModelRuas;

import java.util.List;

public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ViewHolder> {

    private List<ModelRuas.DataRuas> ruasList;
    private List<ModelRegion.DataRegion> region;
    private Context context;
    private String chipIs;

    public ChipAdapter(List<ModelRuas.DataRuas> ruasList, List<ModelRegion.DataRegion> region, Context context, String chipIs) {
        this.ruasList = ruasList;
        this.region = region;
        this.context = context;
        this.chipIs = chipIs;
    }

    @NonNull
    @Override
    public ChipAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chip_layer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipAdapter.ViewHolder holder, int position) {
        if (chipIs.equals("ruas")){
            ModelRuas.DataRuas dataRuas = ruasList.get(position);
            holder.chip.setText(dataRuas.getNamaRuasSingkatan());

        }else {
            ModelRegion.DataRegion dataRegion = region.get(position);
            if (dataRegion.getId_region() == 1){
                holder.chip.setChecked(true);
            }
            holder.chip.setText(dataRegion.getNama_region());
        }
    }

    @Override
    public int getItemCount() {
        if (chipIs.equals("ruas")){
            if (ruasList != null&& ruasList.size() != 0){
                return Math.min(ruasList.size(), 5);
            }else {
                return 0;
            }
        }else {
            if (region != null && region.size() != 0){
                return region.size();
            }else {
                return 0;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Chip chip;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip);
        }
    }
}
