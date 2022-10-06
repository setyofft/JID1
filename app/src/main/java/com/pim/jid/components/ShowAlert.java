package com.pim.jid.components;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.pim.jid.R;

import java.util.List;

public class ShowAlert {

    public static void showDialogRealtime(Activity activity, MapboxMap mapboxMap, LatLng pointlalin, androidx.appcompat.app.AlertDialog alertDialogLineToll){
        PointF screenPointcctv = mapboxMap.getProjection().toScreenLocation(pointlalin);
        List<Feature> featurescctv = mapboxMap.queryRenderedFeatures(screenPointcctv, "finallalin");
        if (!featurescctv.isEmpty()) {
            Feature selectedFeaturecctv = featurescctv.get(0);
            String kec_google = selectedFeaturecctv.getStringProperty("kec_google");
            String nmsegment = selectedFeaturecctv.getStringProperty("nama_segment");
            String nmsubsegment = selectedFeaturecctv.getStringProperty("nama_sub_segment");
            String ruas_tol = selectedFeaturecctv.getStringProperty("ruas_tol");
            String nama_jalur = selectedFeaturecctv.getStringProperty("nama_jalur");
            String kondisi = selectedFeaturecctv.getStringProperty("kondisi");
            String himbauan = selectedFeaturecctv.getStringProperty("himbauan");
            String panjang_segment = selectedFeaturecctv.getStringProperty("panjang_segment");
            String waktu_tempuh = selectedFeaturecctv.getStringProperty("waktu_tempuh");
            String update_time = selectedFeaturecctv.getStringProperty("update_time");

            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.custom_dialog_lalin, null);
            alert.setView(dialoglayout);

            TextView txt_nm_segment = dialoglayout.findViewById(R.id.txt_nm_segment);
            TextView txt_sub_Segment = dialoglayout.findViewById(R.id.txt_sub_Segment);
            TextView txt_ruas_tol = dialoglayout.findViewById(R.id.txt_ruas_tol);
            TextView txt_nm_lajur = dialoglayout.findViewById(R.id.txt_nm_lajur);
            TextView txt_kondisi = dialoglayout.findViewById(R.id.txt_kondisi);
            TextView txt_himbauan = dialoglayout.findViewById(R.id.txt_himbauan);
            TextView txt_kec_google = dialoglayout.findViewById(R.id.txt_kec_google);
            TextView txt_panjang_segment = dialoglayout.findViewById(R.id.txt_panjang_segment);
            TextView txt_waktu_tempuh = dialoglayout.findViewById(R.id.txt_waktu_tempuh);
            TextView txt_update = dialoglayout.findViewById(R.id.txt_update);

            txt_nm_segment.setText(nmsegment);
            txt_sub_Segment.setText(nmsubsegment);
            txt_ruas_tol.setText(ruas_tol);
            txt_nm_lajur.setText(nama_jalur);
            txt_kondisi.setText(kondisi);
            txt_himbauan.setText(himbauan);
            txt_kec_google.setText(kec_google);
            txt_panjang_segment.setText(panjang_segment);
            txt_waktu_tempuh.setText(waktu_tempuh);
            txt_update.setText(update_time);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            alertDialogLineToll = alert.create();
            alertDialogLineToll.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            androidx.appcompat.app.AlertDialog finalAlertDialogLineToll = alertDialogLineToll;
            btn_close.setOnClickListener(v -> {
//                    serviceRealtime.removeCallbacksHandleLalinservice();
                finalAlertDialogLineToll.cancel();
            });
            alertDialogLineToll.show();
        }
    }

    public static void showDialogPemeliharaan(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng pointvms){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(pointvms);
        List<Feature> featuresvsms = mapboxMap.queryRenderedFeatures(screenPointvms, "finalpemeliharaan");
        if (!featuresvsms.isEmpty()) {
            Feature selectedFeaturevms = featuresvsms.get(0);
            String nama_ruas = selectedFeaturevms.getStringProperty("nama_ruas");
            String km = selectedFeaturevms.getStringProperty("km");
            String jalur = selectedFeaturevms.getStringProperty("jalur");
            String lajur = selectedFeaturevms.getStringProperty("lajur");
            String range_km_pekerjaan = selectedFeaturevms.getStringProperty("range_km_pekerjaan");
            String waktu_awal = selectedFeaturevms.getStringProperty("waktu_awal");
            String waktu_akhir = selectedFeaturevms.getStringProperty("waktu_akhir");
            String ket_jenis_kegiatan = selectedFeaturevms.getStringProperty("ket_jenis_kegiatan");
            String keterangan_detail = selectedFeaturevms.getStringProperty("keterangan_detail");
            String ket_status = selectedFeaturevms.getStringProperty("ket_status");


            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.custom_dialog_pemeliharaan, null);
            alert.setView(dialoglayout);

            TextView title_kondisi_lalin = dialoglayout.findViewById(R.id.title_kondisi_lalin);
            TextView txt_nm_ruas = dialoglayout.findViewById(R.id.txt_nm_ruas);
            TextView txt_nmKm = dialoglayout.findViewById(R.id.txt_nmKm);
            TextView txt_jalur_lajur = dialoglayout.findViewById(R.id.txt_jalur_lajur);
            TextView txt_range_km = dialoglayout.findViewById(R.id.txt_eange_km);
            TextView txt_waktu_awal = dialoglayout.findViewById(R.id.txt_waktu_awal);
            TextView txt_waktu_akhir = dialoglayout.findViewById(R.id.txt_waktu_akhir);
            TextView txt_status = dialoglayout.findViewById(R.id.txt_status);
            TextView txt_kegiatan = dialoglayout.findViewById(R.id.txt_jns_kegiatan);
            TextView txt_keternagan = dialoglayout.findViewById(R.id.txt_keterangan);

            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            title_kondisi_lalin.setText("Pemeliharaan");
            txt_nm_ruas.setText(nama_ruas);
            txt_nmKm.setText(km);
            txt_jalur_lajur.setText(jalur+" / "+lajur);
            txt_range_km.setText(range_km_pekerjaan);
            txt_waktu_awal.setText(waktu_awal);
            txt_waktu_akhir.setText(waktu_akhir);
            txt_status.setText(ket_status);
            txt_kegiatan.setText(ket_jenis_kegiatan);
            txt_keternagan.setText(keterangan_detail);

            final AlertDialog  alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
//                                    serviceKondisiLalin.removeCallbacksHandlePemeliharaan();
            });
            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }


    public static void showDialogGangguan(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng pointvms){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(pointvms);
        List<Feature> featuresvsms = mapboxMap.queryRenderedFeatures(screenPointvms, "finalgangguan");
        if (!featuresvsms.isEmpty()) {
            Feature selectedFeaturevms = featuresvsms.get(0);
            String nama_ruas = selectedFeaturevms.getStringProperty("nama_ruas");
            String km = selectedFeaturevms.getStringProperty("km");
            String jalur = selectedFeaturevms.getStringProperty("jalur");
            String lajur = selectedFeaturevms.getStringProperty("lajur");
            String ket_tipe_gangguan = selectedFeaturevms.getStringProperty("ket_tipe_gangguan");
            String waktu_kejadian = selectedFeaturevms.getStringProperty("waktu_kejadian");
            String detail_kejadian = selectedFeaturevms.getStringProperty("detail_kejadian");
            String ket_status = selectedFeaturevms.getStringProperty("ket_status");
            String dampak = selectedFeaturevms.getStringProperty("dampak");
            String waktu_selsai = selectedFeaturevms.getStringProperty("waktu_selsai");

            if (waktu_selsai == null){
                waktu_selsai = "-";
            }else {
                waktu_selsai = waktu_selsai;
            }

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.custome_dialog_gangguan, null);
            alert.setView(dialoglayout);

            TextView title_kondisi_lalin = dialoglayout.findViewById(R.id.title_kondisi_lalin);
            TextView txt_status = dialoglayout.findViewById(R.id.txt_status);
            TextView txt_nm_ruas = dialoglayout.findViewById(R.id.txt_nm_ruas);
            TextView txt_nmKm = dialoglayout.findViewById(R.id.txt_nmKm);
            TextView txt_jalur_lajur = dialoglayout.findViewById(R.id.txt_jalur_lajur);
            TextView txt_tipegangguan = dialoglayout.findViewById(R.id.txt_tipegangguan);
            TextView txt_waktu_kejadian = dialoglayout.findViewById(R.id.txt_waktu_kejadian);
            TextView txt_dampak = dialoglayout.findViewById(R.id.txt_dampak);
            TextView txt_waktu_selesai = dialoglayout.findViewById(R.id.txt_waktu_selesai);
            TextView txt_keterangan = dialoglayout.findViewById(R.id.txt_keterangan);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            title_kondisi_lalin.setText("Gangguan Lalin");
            txt_status.setText(ket_status);
            txt_nm_ruas.setText(nama_ruas);
            txt_nmKm.setText(km);
            txt_jalur_lajur.setText(jalur+" / "+lajur);
            txt_tipegangguan.setText(ket_tipe_gangguan);
            txt_waktu_kejadian.setText(waktu_kejadian);
            txt_dampak.setText(dampak);
            txt_waktu_selesai.setText(waktu_selsai);
            txt_keterangan.setText(detail_kejadian);

            final AlertDialog  alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
//                                        serviceKondisiLalin.removeCallbacksHandleGangguanLalin();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

}
