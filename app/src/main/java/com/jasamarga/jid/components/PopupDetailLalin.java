package com.jasamarga.jid.components;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.jasamarga.jid.R;
import com.jasamarga.jid.models.ModelListGangguan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PopupDetailLalin extends DialogFragment {
    private ModelListGangguan item;
    String title;
    TextView nama_ruas,km,waktu_awal,waktu_akhir,jalur,range_km,status,jenis,ket,arah_jalur,titles;
    Button button;
    RelativeLayout head_akhir,head_range;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format2 = new SimpleDateFormat("dd - MM - yyyy HH:mm");

    Date date,date2;

    public static String TAG = "DetailReport";

    public PopupDetailLalin(ModelListGangguan model,String title){
        this.item = model;
        this.title = title;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_report_lalin, container, false);
        initVar(view);
        return view;
    }

    private void initVar(View v){
        nama_ruas = v.findViewById(R.id.txt_nama_ruas);
        button = v.findViewById(R.id.btn_close);
        km = v.findViewById(R.id.txt_km);
        waktu_awal = v.findViewById(R.id.txt_waktu_awal);
        waktu_akhir = v.findViewById(R.id.txt_waktu_akhir);
        jalur = v.findViewById(R.id.txt_jalur);
        range_km = v.findViewById(R.id.txt_range_km_pekerjaan);
        status = v.findViewById(R.id.txt_status);
        jenis = v.findViewById(R.id.txt_ket_jenis_kegiatan);
        ket = v.findViewById(R.id.txt_keterangan);
        arah_jalur = v.findViewById(R.id.txt_arah_jalur);
        titles = v.findViewById(R.id.title_lalin);
        head_akhir = v.findViewById(R.id.layout_waktu_akhir);
        head_range = v.findViewById(R.id.layout_range);
        action();

    }
    private String logicTitle(){
        switch (title) {
            case "gangguan":
                return "Gangguan Lalu Lintas";
            case "rekayasa":
                return "Rekayasa Lalu Lintas";
            case "pemeliharaan":
                return "Pemeliharaan Lalu Lintas";
        }
        return "Kosong";
    }
    private void action(){
            try {
                date = format.parse(item.getWaktu_awal());
                if(!title.equals("gangguan")){
                    date2 = format.parse(item.getWaktu_akhir());
                }else {
                    date2 = new Date();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(!title.equals("gangguan")) {
                head_range.setVisibility(View.VISIBLE);
                head_akhir.setVisibility(View.VISIBLE);
                range_km.setText(item.getRange_pekerjaan());
                if (date2 != null){
                    waktu_akhir.setText(format2.format(date2));
                }else {
                    waktu_akhir.setText("-");
                }

            }else {
                head_range.setVisibility(View.GONE);
                head_akhir.setVisibility(View.GONE);
            }
            nama_ruas.setText(item.getNama_ruas2());
            km.setText(item.getKm());
            try {
                if (item.getWaktu_awal()!=null || !item.getWaktu_awal().equals("null")){
                    date = format.parse(item.getWaktu_awal());
                }
                if (!title.equals(("gangguan"))){
                    if (item.getWaktu_akhir() != null || !item.getWaktu_akhir().equals("null")){
                        date2 = format.parse(item.getWaktu_akhir());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null){
                waktu_awal.setText(format2.format(date));
            }else {
                waktu_awal.setText("-");
            }
            jalur.setText(item.getJalur());
            status.setText(item.getStatus());
            jenis.setText(item.getTipe());
            ket.setText(item.getKet());
            arah_jalur.setText(item.getArah_lajur());
            titles.setText(logicTitle());

        button.setOnClickListener(view -> {
            dismiss();
        });
    }

}
