package com.jasamarga.jid.components;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.jasamarga.jid.R;
import com.jasamarga.jid.models.DataPemeliharaanModel;
import com.jasamarga.jid.models.ModelGangguanLalin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PopupDetailDataGangguan extends DialogFragment {
    private ModelGangguanLalin.GangguanData item;
    String title;
    TextView nama_ruas,km,waktu_awal,waktu_akhir,jalur,range_km,status,jenis,ket,arah_jalur,titles;
    Button button;
    RelativeLayout head_akhir,head_range;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    Date date,date2;

    public static String TAG = "DetailReport";
    private TextView head_jalur;

    public PopupDetailDataGangguan(ModelGangguanLalin.GangguanData model, String title){
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
        head_jalur = v.findViewById(R.id.head_jalur);
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
            if (item.getWaktuKejadian() != null){
                date = format.parse(item.getWaktuKejadian());
            }
            if (item.getWaktuSelesai() != null){
                date2 = format.parse(item.getWaktuSelesai());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        nama_ruas.setText(item.getNamaRuas());
        km.setText(item.getKm());
        waktu_awal.setText(format2.format(date));
        waktu_akhir.setText(date2 != null ? format2.format(date2) : "-");
        jalur.setText(item.getLajur());
        head_jalur.setText("Lajur");
        status.setText(item.getKetStatus());
        jenis.setText(String.valueOf(item.getTipeGangguan()));
        ket.setText(item.getDetailKejadian());
        arah_jalur.setText(item.getJalur());
        titles.setText("Gangguan lalin");
        button.setOnClickListener(view -> {
            dismiss();
        });
    }
}
