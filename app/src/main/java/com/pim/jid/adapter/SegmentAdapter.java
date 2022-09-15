package com.pim.jid.adapter;

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
import com.pim.jid.R;
import com.pim.jid.models.RuasModel;
import com.pim.jid.models.SegmentModel;
import com.pim.jid.views.CctvRuas;
import com.pim.jid.views.CctvViewRuas;

import java.util.ArrayList;

public class SegmentAdapter extends RecyclerView.Adapter<SegmentAdapter.ViewProcessHolder>{
    Context context;
    private ArrayList<SegmentModel> item;

    public SegmentAdapter(Context context, ArrayList<SegmentModel> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public SegmentAdapter.ViewProcessHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_button_segment, parent, false);
        return new SegmentAdapter.ViewProcessHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SegmentAdapter.ViewProcessHolder holder, int position) {
        final SegmentModel data = item.get(position);
        holder.itemView.setTag(position);
        holder.button.setText(data.getNamaSegment());
    }

    @Override
    public int getItemCount() {
        return item.size();
    }


    public class ViewProcessHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MaterialButton button;

        public ViewProcessHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            button = itemView.findViewById(R.id.button_all_segment);
        }

        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            Intent intent = new Intent(context, CctvViewRuas.class);
            intent.putExtra("judul_segment",item.get(position).getNamaSegment());
            intent.putExtra("id_segment",item.get(position).getIdSegment());
            intent.putExtra("id_ruas",item.get(0).getIdRUas());
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(0,0);
        }
    }
}
