package com.jasamarga.jid.adapter;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.jasamarga.jid.R;
import com.jasamarga.jid.components.PopupDetailLalin;
import com.jasamarga.jid.models.ModelListGangguan;
import com.jasamarga.jid.models.model_notif.ModelEvent;
import com.jasamarga.jid.models.model_notif.ModelNotif;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class NotifAdapater extends RecyclerView.Adapter<NotifAdapater.ViewHolder> {
    private ArrayList<ModelNotif> modelEvents;
    private Context context;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format3 = new SimpleDateFormat("dd/MM/yyyy");

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat time = new SimpleDateFormat("HH:mm");

    boolean extend = false;
    Date date,notifTgl;

    @SuppressLint("NotifyDataSetChanged")
    public NotifAdapater(ArrayList<ModelNotif> listGangguan, Context context) {
        this.modelEvents = listGangguan;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notif_list, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ModelNotif item2 = modelEvents.get(position);
        ArrayList<ModelEvent> modelE = new ArrayList<>();
        Log.d(TAG, "onBindViewHolder: " + modelEvents.get(position).getNotification());
        JSONObject jsonObject1 = null;
        JSONObject js = null;
        try {
            jsonObject1 = new JSONObject(item2.getNotification());
            ModelEvent modelEvent = new ModelEvent();
            modelEvent.setDetailKetJenisEvent(jsonObject1.getString("detail_ket_jenis_event"));
            modelEvent.setTipeEvent(jsonObject1.getString("tipe_event"));
            modelEvent.setKetJenisEvent(jsonObject1.getString("ket_jenis_event"));
            modelEvent.setKetStatus(jsonObject1.getString("ket_status"));
            modelEvent.setJalur(jsonObject1.getString("jalur"));
            modelEvent.setKm(jsonObject1.getString("km"));
            modelEvent.setNamaRuas(jsonObject1.getString("nama_ruas"));
            modelEvent.setTanggal(jsonObject1.isNull("tanggal") ? " - " : jsonObject1.getString("tanggal"));
                if (jsonObject1.getString("tipe_event").contains("Gangguan")) {
                    modelEvent.setDampak(jsonObject1.isNull("dampak") ? " - " : jsonObject1.getString("dampak"));
                }
            Log.d(TAG, "onBindViewHolder: " + modelEvent.getTipeEvent());
            modelE.add(modelEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ModelEvent item = modelE.get(0);
        try {
            if(!item.getTanggal().equals(" - ")){
                date = format4.parse(item.getTanggal());
            }
            notifTgl  = format.parse(item2.getCreatedAt());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String tanggal = format2.format(date);
        String tanggal2 = format3.format(notifTgl);

        String now = format3.format(new Date());
        if (tanggal2.contains(now)) {
            holder.date.setText("Hari ini " + time.format(notifTgl));
        } else {
            holder.date.setTextSize(14);
            holder.date.setText(tanggal);
        }
        holder.title.setText(item.getTipeEvent());

        if (extend) {
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.im.setRotation(180);
        } else {
            holder.linearLayout.setVisibility(View.GONE);
            holder.im.setRotation(0);
        }

        if (item.getTipeEvent().contains("Gangguan")){
            holder.desc4.setText(item.getDampak());
            holder.desc4.setVisibility(View.VISIBLE);

        }else {
            holder.desc4.setVisibility(View.GONE);
        }
        holder.body.setText("Nama Ruas : " + item.getNamaRuas());
        holder.body2.setText("Status : " + item.getKetStatus());
        if (item.getTanggal().equals(" - ")) {
            holder.body3.setVisibility(View.GONE);
        } else {
            holder.body3.setText("Tanggal Kejadian : " + tanggal);
        }
        holder.desc1.setText("Jenis Kejadian : " + item.getKetJenisEvent());
        holder.desc2.setText("KM : " + item.getKm() + " " + item.getJalur());
        holder.desc3.setText("Detail Kejadian : " + item.getDetailKetJenisEvent());
        holder.desc4.setText("Dampak : " + item.getDampak());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                extend = !extend;
                notifyItemChanged(position);
            }
        });
        holder.im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extend = !extend;
                notifyItemChanged(position);
            }
        });

    }
    @Override
    public int getItemCount() {
        return modelEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,body,body2,body3,date,desc1,desc2,desc3,desc4;
        LinearLayout linearLayout;
        ImageView im;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            body2 = itemView.findViewById(R.id.body2);
            body3 = itemView.findViewById(R.id.body3);
            date = itemView.findViewById(R.id.date);
            linearLayout = itemView.findViewById(R.id.listDetail);
            desc1 = itemView.findViewById(R.id.desc1);
            desc2 = itemView.findViewById(R.id.desc2);
            desc3 = itemView.findViewById(R.id.desc3);
            desc4 = itemView.findViewById(R.id.desc4);
            im = itemView.findViewById(R.id.arrow);
//            status = itemView.findViewById(R.id.status);
        }
    }
}
