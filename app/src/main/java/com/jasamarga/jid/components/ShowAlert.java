package com.jasamarga.jid.components;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jasamarga.jid.R;
import com.jasamarga.jid.views.Activitiweb;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShowAlert {
    public static void alertExit(Activity activity){
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari aplikasi ?");
        alertDialogBuilder.setIcon(R.drawable.logojm);
        alertDialogBuilder.setBackground(activity.getResources().getDrawable(R.drawable.modal_alert));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> activity.finish());
        alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
        alertDialogBuilder.show();
    }
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

    public static void showDIalogGerbangTol(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng pointgerbangtol){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(pointgerbangtol);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalgerbangtol");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String id_ruas = selectedFeaturevms.getStringProperty("id_ruas");
            String nama_gerbang = selectedFeaturevms.getStringProperty("nama_gerbang");
            String nama_cabang = selectedFeaturevms.getStringProperty("nama_cabang");
            String lalin_shift_1 = selectedFeaturevms.getStringProperty("lalin_shift_1");
            String lalin_shift_2 = selectedFeaturevms.getStringProperty("lalin_shift_2");
            String lalin_shift_3 = selectedFeaturevms.getStringProperty("lalin_shift_3");
            String lalin_perjam = selectedFeaturevms.getStringProperty("lalin_perjam");
            String status = selectedFeaturevms.getStringProperty("status");
            String last_update = selectedFeaturevms.getStringProperty("last_update");

            String ket_status = "";
            if (status.equals("0")){
                ket_status = "Status Off";
            }else{
                ket_status = "Status On";
            }

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.custome_dialog_gerbangtol, null);
            alert.setView(dialoglayout);

            TextView title_kondisi_lalin = dialoglayout.findViewById(R.id.title_kondisi_lalin);
            TextView txt_status = dialoglayout.findViewById(R.id.txt_status);
            TextView txt_nm_cabang = dialoglayout.findViewById(R.id.txt_nm_cabang);
            TextView txt_nm_gerbang = dialoglayout.findViewById(R.id.txt_nm_gerbang);
            TextView txt_lalinperjam = dialoglayout.findViewById(R.id.txt_lalinperjamr);
            TextView txt_shift1 = dialoglayout.findViewById(R.id.txt_shift1);
            TextView txt_shift2 = dialoglayout.findViewById(R.id.txt_shift2);
            TextView txt_shift3 = dialoglayout.findViewById(R.id.txt_shift3);
            TextView txt_lastupdate = dialoglayout.findViewById(R.id.txt_lastupdate);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            title_kondisi_lalin.setText("Gerbang Tol");
            txt_status.setText(ket_status);
            txt_nm_cabang.setText(nama_cabang);
            txt_nm_gerbang.setText(nama_gerbang);
            txt_lalinperjam.setText(lalin_perjam);
            txt_shift1.setText(lalin_shift_1);
            txt_shift2.setText(lalin_shift_2);
            txt_shift3.setText(lalin_shift_3);
            txt_lastupdate.setText(last_update);

            final AlertDialog  alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }
    public static void showDialogGerbangSispuTol(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng pointgerbangtol){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(pointgerbangtol);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalgerbangsisputol");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String id_ruas = selectedFeaturevms.getStringProperty("id_ruas");
            String nama_gerbang = selectedFeaturevms.getStringProperty("nama_gerbang");
            String nama_cabang = selectedFeaturevms.getStringProperty("nama_ruas");
            String lalin_shift_1 = selectedFeaturevms.getStringProperty("gol1");
            String lalin_shift_2 = selectedFeaturevms.getStringProperty("gol2");
            String lalin_shift_3 = selectedFeaturevms.getStringProperty("gol3");
            String lalin_shift_4 = selectedFeaturevms.getStringProperty("gol4");
            String lalin_shift_5 = selectedFeaturevms.getStringProperty("gol5");
            String lalin_perjam = selectedFeaturevms.getStringProperty("jam");
            String status = selectedFeaturevms.getStringProperty("status");
            String last_update = selectedFeaturevms.getStringProperty("last_update");
            String ket_status = selectedFeaturevms.getStringProperty("status");
//            if (status.equals("0")){
//                ket_status = "Status Off";
//            }else{
//                ket_status = "Status On";
//            }

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.custome_dialog_gerbangsisputol, null);
            alert.setView(dialoglayout);

            TextView title_kondisi_lalin = dialoglayout.findViewById(R.id.title_kondisi_lalin);
            TextView txt_status = dialoglayout.findViewById(R.id.txt_status);
            TextView txt_nm_cabang = dialoglayout.findViewById(R.id.txt_nm_cabang);
            TextView txt_nm_gerbang = dialoglayout.findViewById(R.id.txt_nm_gerbang);
            TextView txt_lalinperjam = dialoglayout.findViewById(R.id.txt_lalinperjamr);
            TextView txt_shift1 = dialoglayout.findViewById(R.id.txt_shift1);
            TextView txt_shift2 = dialoglayout.findViewById(R.id.txt_shift2);
            TextView txt_shift3 = dialoglayout.findViewById(R.id.txt_shift3);
            TextView txt_shift4 = dialoglayout.findViewById(R.id.txt_shift4);
            TextView txt_shift5 = dialoglayout.findViewById(R.id.txt_shift5);
            TextView txt_lastupdate = dialoglayout.findViewById(R.id.txt_lastupdate);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            title_kondisi_lalin.setText("Gerbang Tol");
            txt_status.setText(ket_status);
            txt_nm_cabang.setText(nama_cabang);
            txt_nm_gerbang.setText(nama_gerbang);
            txt_lalinperjam.setText(lalin_perjam);
            txt_shift1.setText(lalin_shift_1);
            txt_shift2.setText(lalin_shift_2);
            txt_shift3.setText(lalin_shift_3);
            txt_shift4.setText(lalin_shift_4);
            txt_shift5.setText(lalin_shift_5);
            txt_lastupdate.setText(last_update);

            final AlertDialog  alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }
    public static void showRestArea(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng point){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalrestarea");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String nama_rest_area = selectedFeaturevms.getStringProperty("nama_rest_area");
            String kondisi = selectedFeaturevms.getStringProperty("kondisi");
            String status = selectedFeaturevms.getStringProperty("status_ra");
            String last_update = selectedFeaturevms.getStringProperty("last_update");
            String kend_besar_tersedia = selectedFeaturevms.getStringProperty("kend_besar_tersedia");
            String kend_kecil_tersedia = selectedFeaturevms.getStringProperty("kend_kecil_tersedia");
            String kapasitas_kend_besar = selectedFeaturevms.getStringProperty("kapasitas_kend_besar");
            String kapasitas_kend_kecil = selectedFeaturevms.getStringProperty("kapasitas_kend_kecil");
            String cctv_1 = selectedFeaturevms.getStringProperty("cctv_1");
            String cctv_2 = selectedFeaturevms.getStringProperty("cctv_2");
            String cctv_3 = selectedFeaturevms.getStringProperty("cctv_3");

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);


            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_rest_area, null);
            alert.setView(dialoglayout);

            TextView title_kondisi_lalin = dialoglayout.findViewById(R.id.title_kondisi_lalin);
            TextView txt_lastupdate = dialoglayout.findViewById(R.id.txt_lastupdate);
            TextView txt_kondisi = dialoglayout.findViewById(R.id.txt_kondisi);
            TextView txt_status = dialoglayout.findViewById(R.id.txt_status);
            TextView val_kecil = dialoglayout.findViewById(R.id.val_kecil);
            TextView val_besar = dialoglayout.findViewById(R.id.val_besar);
            TextView val_kecil_tersedia = dialoglayout.findViewById(R.id.val_kecil_tersedia);
            TextView val_besar_tersedia = dialoglayout.findViewById(R.id.val_besar_tersedia);

            ViewFlipper flip_camera = dialoglayout.findViewById(R.id.flip_camera);
            ProgressBar loadingIMG = dialoglayout.findViewById(R.id.loadingIMG);
            ImageView flip_img_1 = dialoglayout.findViewById(R.id.flip_img_1);
            ImageView flip_img_2 = dialoglayout.findViewById(R.id.flip_img_2);
            ImageView flip_img_3 = dialoglayout.findViewById(R.id.flip_img_3);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);
            Button btn_next_1 = dialoglayout.findViewById(R.id.btn_next_1);
            Button btn_next_2 = dialoglayout.findViewById(R.id.btn_next_2);
            Button btn_next_3 = dialoglayout.findViewById(R.id.btn_next_3);

            btn_next_1.setOnClickListener(v ->
                    flip_camera.setDisplayedChild(3)
            );
            btn_next_2.setOnClickListener(v ->
                    flip_camera.setDisplayedChild(1)
            );
            btn_next_3.setOnClickListener(v ->
                    flip_camera.setDisplayedChild(2)
            );

            setImgFlip(cctv_1, dialoglayout, loadingIMG, flip_img_1);
            setImgFlip(cctv_2, dialoglayout, loadingIMG, flip_img_2);
            setImgFlip(cctv_3, dialoglayout, loadingIMG, flip_img_3);

            title_kondisi_lalin.setText("Rest Area "+nama_rest_area);
            txt_kondisi.setText(kondisi);
            txt_status.setText(status);
            val_besar.setText(kend_besar_tersedia);
            val_kecil.setText(kend_kecil_tersedia);
            val_besar_tersedia.setText(kapasitas_kend_besar);
            val_kecil_tersedia.setText(kapasitas_kend_kecil);
            txt_lastupdate.setText(last_update);

            final AlertDialog  alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

    public static void setImgFlip(String url, View dialoglayout,ProgressBar loadingIMG, ImageView imageSet){
        Glide.with(dialoglayout)
                .asBitmap()
                .load(url)
                .fitCenter()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        loadingIMG.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        loadingIMG.setVisibility(View.GONE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource,
                                                @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        imageSet.setImageBitmap(resource);
                    }
                });
    }

    public static void showDialogRTMS(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng point){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalrtms");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String nama_lokasi = selectedFeaturevms.getStringProperty("nama_lokasi");
            String total_volume_jalur_a = selectedFeaturevms.getStringProperty("total_volume_jalur_a");
            String speed_jalur_a = selectedFeaturevms.getStringProperty("speed_jalur_a");
            String total_volume_jalur_b = selectedFeaturevms.getStringProperty("total_volume_jalur_b");
            String speed_jalur_b = selectedFeaturevms.getStringProperty("speed_jalur_b");
            String waktu_update = selectedFeaturevms.getStringProperty("waktu_update");
            String status = selectedFeaturevms.getStringProperty("status");

            String ket_status = "";
            String ket_vol_a = "";
            String ket_vol_b = "";
            String ket_kec_a = "";
            String ket_kec_b = "";
            if (status.equals("1")){
                ket_status = "ON";
            }else{
                ket_status = "OFF";
            }

            if (total_volume_jalur_a.equals("-1")){
                ket_vol_a = "0";
            }else{
                ket_vol_a = total_volume_jalur_a;
            }
            if (total_volume_jalur_b.equals("-1")){
                ket_vol_b = "0";
            }else{
                ket_vol_b = total_volume_jalur_b;
            }

            if (speed_jalur_a.equals("-1")){
                ket_kec_a = "0";
            }else{
                ket_kec_a = speed_jalur_a;
            }
            if (speed_jalur_b.equals("-1")){
                ket_kec_b = "0";
            }else{
                ket_kec_b = speed_jalur_b;
            }

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_rtms, null);
            alert.setView(dialoglayout);

            TextView txt_nama_lokasi = dialoglayout.findViewById(R.id.txt_nama_lokasi);
            TextView val_jalur_a = dialoglayout.findViewById(R.id.val_jalur_a);
            TextView val_jalur_b = dialoglayout.findViewById(R.id.val_jalur_b);
            TextView val_kec_jalura = dialoglayout.findViewById(R.id.val_kec_jalura);
            TextView val_kec_jalurb = dialoglayout.findViewById(R.id.val_kec_jalurb);
            TextView txt_status = dialoglayout.findViewById(R.id.txt_status);
            TextView txt_lastupdate = dialoglayout.findViewById(R.id.txt_lastupdate);

            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            txt_nama_lokasi.setText(nama_lokasi);
            val_jalur_a.setText(ket_vol_a);
            val_jalur_b.setText(ket_vol_b);
            val_kec_jalura.setText(ket_kec_a);
            val_kec_jalurb.setText(ket_kec_b);
            txt_status.setText(ket_status);
            txt_lastupdate.setText(waktu_update);

            final AlertDialog  alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

    public static void showDialogRTMSCCTV(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng point){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalrtms2");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String nama_lokasi = selectedFeaturevms.getStringProperty("nama_lokasi");
            String cabang = selectedFeaturevms.getStringProperty("cabang");
            String key_id = selectedFeaturevms.getStringProperty("key_id");
            String car = selectedFeaturevms.getStringProperty("car");
            String bus = selectedFeaturevms.getStringProperty("bus");
            String truck = selectedFeaturevms.getStringProperty("truck");
            String total_volume = selectedFeaturevms.getStringProperty("total_volume");
            String status = selectedFeaturevms.getStringProperty("status");
            String waktu_update = selectedFeaturevms.getStringProperty("waktu_update");

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_rtms_cctv, null);
            alert.setView(dialoglayout);

            TextView val_car = dialoglayout.findViewById(R.id.val_car);
            TextView val_bus = dialoglayout.findViewById(R.id.val_bus);
            TextView val_truck = dialoglayout.findViewById(R.id.val_truck);
            TextView txt_total = dialoglayout.findViewById(R.id.txt_total);
            TextView txt_lastupdate = dialoglayout.findViewById(R.id.txt_lastupdate);
            TextView txtnama_lokasi = dialoglayout.findViewById(R.id.nama_lokasi);
            ProgressBar loadingIMG = dialoglayout.findViewById(R.id.loadingIMG);
            ImageView str_cctv_rtms = dialoglayout.findViewById(R.id.str_cctv_rtms);

            LinearLayout lay_off = dialoglayout.findViewById(R.id.lay_off);
            LinearLayout lay_on = dialoglayout.findViewById(R.id.lay_on);

            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            if (status.equals("1")){
                lay_off.setVisibility(View.GONE);
                lay_on.setVisibility(View.VISIBLE);
            }else{
                lay_on.setVisibility(View.GONE);
                lay_off.setVisibility(View.VISIBLE);
            }

            txtnama_lokasi.setText(cabang+", "+nama_lokasi);
            val_car.setText(car);
            val_bus.setText(bus);
            val_truck.setText(truck);
            txt_total.setText(total_volume);
            txt_lastupdate.setText(waktu_update);

            Handler handler_cctvs = new Handler();
            handler_cctvs.postDelayed(new Runnable(){
                public void run(){
                    String urlimg = "https://jid.jasamarga.com/cctv2/"+key_id+"?tx="+Math.random();
                    setImgFlip(urlimg, dialoglayout, loadingIMG, str_cctv_rtms);
                    handler_cctvs.postDelayed(this, 1000);
                }
            }, 300);

            final AlertDialog  alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                handler_cctvs.removeCallbacksAndMessages(null);
                alertDialog.cancel();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

    public static void showDialogSpeed(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng point){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalspeed");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String nama_lokasi = selectedFeaturevms.getStringProperty("nama_lokasi");
            String cabang = selectedFeaturevms.getStringProperty("cabang");
            String posisi = selectedFeaturevms.getStringProperty("posisi");
            String kec_1 = selectedFeaturevms.getStringProperty("kec_1");
            String kec_2 = selectedFeaturevms.getStringProperty("kec_2");
            String kec_3 = selectedFeaturevms.getStringProperty("kec_3");
            String total_volume = selectedFeaturevms.getStringProperty("total_volume");
            String waktu_update = selectedFeaturevms.getStringProperty("waktu_update");

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_speed, null);
            alert.setView(dialoglayout);

            TextView val_kec_040 = dialoglayout.findViewById(R.id.val_kec_040);
            TextView val_kec_4080 = dialoglayout.findViewById(R.id.val_kec_4080);
            TextView val_kec_80 = dialoglayout.findViewById(R.id.val_kec_80);
            TextView txt_total = dialoglayout.findViewById(R.id.txt_total);
            TextView txt_lastupdate = dialoglayout.findViewById(R.id.txt_lastupdate);
            TextView txtnama_lokasi = dialoglayout.findViewById(R.id.txt_nama_lokasi);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            txtnama_lokasi.setText(cabang+", "+nama_lokasi+"\n("+posisi+")");
            val_kec_040.setText(kec_1);
            val_kec_4080.setText(kec_2);
            val_kec_80.setText(kec_3);
            txt_total.setText(total_volume);
            txt_lastupdate.setText(waktu_update);

            final AlertDialog alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

    public static void showDialogWaterLevel(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng point){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finallevel");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String nama_lokasi = selectedFeaturevms.getStringProperty("nama_lokasi");
            String nama_ruas = selectedFeaturevms.getStringProperty("nama_ruas");
            String level_sensor = selectedFeaturevms.getStringProperty("level_sensor");
            String level = selectedFeaturevms.getStringProperty("level");
            String hujan = selectedFeaturevms.getStringProperty("hujan");
            String pompa = selectedFeaturevms.getStringProperty("pompa");

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_water_level, null);
            alert.setView(dialoglayout);

            TextView txt_nm_lokasi = dialoglayout.findViewById(R.id.txt_nm_lokasi);
            TextView txt_nm_ruas = dialoglayout.findViewById(R.id.txt_nm_ruas);
            TextView txt_level_sesnsor = dialoglayout.findViewById(R.id.txt_level_sesnsor);
            TextView txt_level = dialoglayout.findViewById(R.id.txt_level);
            TextView txt_hujan = dialoglayout.findViewById(R.id.txt_hujan);
            TextView txt_pompa = dialoglayout.findViewById(R.id.txt_pompa);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            txt_nm_lokasi.setText(nama_lokasi);
            txt_nm_ruas.setText(nama_ruas);
            txt_level_sesnsor.setText(level_sensor);
            txt_level.setText(level);
            txt_hujan.setText(hujan);
            txt_pompa.setText(pompa);

            final AlertDialog alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

    public static void showDialogPompaBanjir(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng point){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalpompa");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String jenis_pompa = selectedFeaturevms.getStringProperty("jenis_pompa");
            String vol = selectedFeaturevms.getStringProperty("vol");
            String no_urut_pompa = selectedFeaturevms.getStringProperty("no_urut_pompa");
            String nama_pompa = selectedFeaturevms.getStringProperty("nama_pompa");
            String kw = selectedFeaturevms.getStringProperty("kw");
            String diameter = selectedFeaturevms.getStringProperty("diameter");
            String km = selectedFeaturevms.getStringProperty("km");
            String jalur = selectedFeaturevms.getStringProperty("jalur");
            String keterangan = selectedFeaturevms.getStringProperty("keterangan");

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_pompa_banjir, null);
            alert.setView(dialoglayout);

            TextView txt_jns_pompa = dialoglayout.findViewById(R.id.txt_jns_pompa);
            TextView txt_volume = dialoglayout.findViewById(R.id.txt_volume);
            TextView txt_no_urut = dialoglayout.findViewById(R.id.txt_no_urut);
            TextView txt_nm_pompa = dialoglayout.findViewById(R.id.txt_nm_pompa);
            TextView txt_kw = dialoglayout.findViewById(R.id.txt_kw);
            TextView txt_diameter = dialoglayout.findViewById(R.id.txt_diameter);
            TextView txt_km = dialoglayout.findViewById(R.id.txt_km);
            TextView txt_jalur = dialoglayout.findViewById(R.id.txt_jalur);
            TextView txt_ket = dialoglayout.findViewById(R.id.txt_ket);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            txt_jns_pompa.setText(jenis_pompa);
            txt_volume.setText(vol);
            txt_no_urut.setText(no_urut_pompa);
            txt_nm_pompa.setText(nama_pompa);
            txt_kw.setText(kw);
            txt_diameter.setText(diameter);
            txt_km.setText(km);
            txt_jalur.setText(jalur);
            txt_ket.setText(keterangan);

            final AlertDialog alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

    public static void showDialogWIM(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng point){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalwim");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String nama_lokasi = selectedFeaturevms.getStringProperty("nama_lokasi");
            String Respon_Induk_PJR = selectedFeaturevms.getStringProperty("Respon_Induk_PJR");
            String Back_Office_ETLE = selectedFeaturevms.getStringProperty("Back_Office_ETLE");
            String waktu_update = selectedFeaturevms.getStringProperty("waktu_update");

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_wim, null);
            alert.setView(dialoglayout);

            TextView txt_nama_lokasi = dialoglayout.findViewById(R.id.nama_lokasi);
            TextView txt_respjr = dialoglayout.findViewById(R.id.txt_respjr);
            TextView txt_etle = dialoglayout.findViewById(R.id.txt_etle);
            TextView txt_lastupdate = dialoglayout.findViewById(R.id.txt_lastupdate);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            txt_nama_lokasi.setText(nama_lokasi);
            txt_respjr.setText(Respon_Induk_PJR);
            txt_etle.setText(Back_Office_ETLE);
            txt_lastupdate.setText(waktu_update);

            final AlertDialog alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

    public static void showDialogBike(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng point){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalbike");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String nama_lokasi = selectedFeaturevms.getStringProperty("nama_lokasi");
            String waktu_update_koneksi = selectedFeaturevms.getStringProperty("waktu_update_koneksi");
            String waktu_update_status_perangkat = selectedFeaturevms.getStringProperty("waktu_update_status_perangkat");
            String waktu_input_terakhir = selectedFeaturevms.getStringProperty("waktu_input_terakhir");

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_bike, null);
            alert.setView(dialoglayout);

            TextView txt_nm_lokasi = dialoglayout.findViewById(R.id.txt_nm_lokasi);
            TextView txt_update_koneksi = dialoglayout.findViewById(R.id.txt_update_koneksi);
            TextView txt_update_perangkat = dialoglayout.findViewById(R.id.txt_update_perangkat);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            txt_nm_lokasi.setText(nama_lokasi);
            txt_update_koneksi.setText(waktu_update_koneksi);
            txt_update_perangkat.setText(waktu_update_status_perangkat);

            final AlertDialog alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

    public static void showDialogkendaraanOpera(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng point){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalgpskendaraan");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String ruas = selectedFeaturevms.getStringProperty("ruas");
            String odo_sehari = selectedFeaturevms.getStringProperty("odo_sehari");
            String vehicle_name = selectedFeaturevms.getStringProperty("vehicle_name");
            String police_number = selectedFeaturevms.getStringProperty("police_number");
            String waktu_update = selectedFeaturevms.getStringProperty("waktu_update");
            String heading = selectedFeaturevms.getStringProperty("heading");
            String speed = selectedFeaturevms.getStringProperty("speed");
            String poi = selectedFeaturevms.getStringProperty("poi");
            String status = selectedFeaturevms.getStringProperty("status");

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_kendaraan_operasional, null);
            alert.setView(dialoglayout);

            ImageView imgPoi = dialoglayout.findViewById(R.id.imgPoi);
            TextView val_status = dialoglayout.findViewById(R.id.val_status);
            TextView val_speed = dialoglayout.findViewById(R.id.val_speed);
            TextView txt_vol_ruas = dialoglayout.findViewById(R.id.txt_vol_ruas);
            TextView val_odo = dialoglayout.findViewById(R.id.val_odo);
            TextView val_vehicle_nm = dialoglayout.findViewById(R.id.val_vehicle_nm);
            TextView val_polisi_num = dialoglayout.findViewById(R.id.val_polisi_num);
            TextView txt_lastupdate = dialoglayout.findViewById(R.id.txt_lastupdate);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);

            if (poi.equals("patroli_30")){
                imgPoi.setImageResource(R.drawable.patroli_64);
            }else if (poi.equals("ambulance_30")){
                imgPoi.setImageResource(R.drawable.ambulance_64);
            }else if (poi.equals("derek_30")){
                imgPoi.setImageResource(R.drawable.derek_64);
            }else if (poi.equals("kamtib_30")){
                imgPoi.setImageResource(R.drawable.kamtib_64);
            }else if (poi.equals("support_30")){
                imgPoi.setImageResource(R.drawable.support_64);
            }

            val_status.setText(status);
            val_speed.setText("/ "+speed+" Km/h");
            txt_vol_ruas.setText(ruas);
            val_odo.setText(odo_sehari);
            val_vehicle_nm.setText(vehicle_name);
            val_polisi_num.setText(police_number);
            txt_lastupdate.setText(waktu_update);

            final AlertDialog alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

    public static void showDialogRadar(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng point, Context context){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalradar");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String nama_lokasi = selectedFeaturevms.getStringProperty("nama_lokasi");
            String vcr_jalur_a = selectedFeaturevms.getStringProperty("vcr_jalur_a");
            String vcr_jalur_b = selectedFeaturevms.getStringProperty("vcr_jalur_b");
            String last_update = selectedFeaturevms.getStringProperty("last_update");
            String idx = selectedFeaturevms.getStringProperty("idx");
            String id_ruas = selectedFeaturevms.getStringProperty("url_radar");
            Log.d(TAG, "showDialogRadar: " + selectedFeaturevms.toJson());

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_radar, null);
            alert.setView(dialoglayout);

            TextView val_nmlokasi = dialoglayout.findViewById(R.id.val_nmlokasi);
            TextView val_vcr_jalaur_a = dialoglayout.findViewById(R.id.val_vcr_jalaur_a);
            TextView val_vcr_jalaur_b = dialoglayout.findViewById(R.id.val_vcr_jalaur_b);
            TextView txt_lastupdate = dialoglayout.findViewById(R.id.txt_lastupdate);
            Button btn_close = dialoglayout.findViewById(R.id.btn_close);
            Button btn_getdata = dialoglayout.findViewById(R.id.btn_getdata);

            String vcr_a = "0";
            String vcr_b = "0";

            if (vcr_jalur_a == null){
                vcr_a = "0";
            }else{
                vcr_a = vcr_jalur_a;
            }
            if (vcr_jalur_b == null){
                vcr_b = "0";
            }else{
                vcr_b = vcr_jalur_b;
            }

            val_nmlokasi.setText(nama_lokasi);
            val_vcr_jalaur_a.setText(vcr_a);
            val_vcr_jalaur_b.setText(vcr_b);
            txt_lastupdate.setText(last_update);

            final AlertDialog alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });

            btn_getdata.setOnClickListener(v -> {
                Intent i = new Intent(v.getContext(), Activitiweb.class);
                i.putExtra("hosturl", id_ruas);
                i.putExtra("judul_app", "Dashboard Radar");
                v.getContext().startActivity(i);
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

    public static void showDialogMidas(Activity activity,AlertDialog alertDialogLineToll,MapboxMap mapboxMap,LatLng point, Context context){
        PointF screenPointvms = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> featuresgerbangtol = mapboxMap.queryRenderedFeatures(screenPointvms, "finalmidas");
        if (!featuresgerbangtol.isEmpty()) {
            Feature selectedFeaturevms = featuresgerbangtol.get(0);
            String idx = selectedFeaturevms.getStringProperty("idx");

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_midas, null);
            alert.setView(dialoglayout);

            Button btn_close = dialoglayout.findViewById(R.id.btn_close);
            Button btn_data = dialoglayout.findViewById(R.id.btn_data);
            Button btn_cctv = dialoglayout.findViewById(R.id.btn_cctv);
            
            TextView txtNmSegment, txtNmKm, txtJalurLajur, txtTipeGangguan, txtWaktuKejadian, txtWaktuSelesai;
            TextView headNmSegment, headNmKm, headJalur, headTipeGangguan, headWaktuKejadian, headWaktuSelesai;

            txtNmSegment = dialoglayout.findViewById(R.id.txt_nm_segment);
            headNmSegment = dialoglayout.findViewById(R.id.head_nm_segment);
            txtNmKm = dialoglayout.findViewById(R.id.txt_nmKm);
            headNmKm = dialoglayout.findViewById(R.id.head_nm_km);

            txtJalurLajur = dialoglayout.findViewById(R.id.txt_jalur_lajur);
            headJalur = dialoglayout.findViewById(R.id.head_jalur);

            txtTipeGangguan = dialoglayout.findViewById(R.id.txt_tipegangguan);
            headTipeGangguan = dialoglayout.findViewById(R.id.head_tipegangguan);
            txtWaktuKejadian = dialoglayout.findViewById(R.id.txt_waktu_kejadian);
            headWaktuKejadian = dialoglayout.findViewById(R.id.head_waktu_kejadian);
            txtWaktuSelesai = dialoglayout.findViewById(R.id.txt_waktu_selesai);
            headWaktuSelesai = dialoglayout.findViewById(R.id.head_waktu_selesai);
            
            txtNmSegment.setText(selectedFeaturevms.getStringProperty("nama_segment"));
            txtNmKm.setText(selectedFeaturevms.getStringProperty("ruas_tol"));
            txtJalurLajur.setText(selectedFeaturevms.getStringProperty("nama_jalur"));
            txtTipeGangguan.setText(selectedFeaturevms.getStringProperty("panjang_segment"));
            txtWaktuKejadian.setText(selectedFeaturevms.getStringProperty("waktu_tempuh"));
            txtWaktuSelesai.setText(selectedFeaturevms.getStringProperty("update_time"));

            final AlertDialog alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn_close.setOnClickListener(v -> {
                alertDialog.cancel();
            });
            btn_data.setVisibility(View.GONE);
            btn_data.setEnabled(false);
            btn_data.setOnClickListener(v -> {
                Intent i = new Intent(v.getContext(), Activitiweb.class);
                i.putExtra("hosturl", "https://jid.jasamarga.com/midasns.php?idx="+idx);
                i.putExtra("judul_app", "Data MIDAS");
                v.getContext().startActivity(i);
            });

            btn_cctv.setOnClickListener(v -> {
                Intent i = new Intent(v.getContext(), Activitiweb.class);
                i.putExtra("hosturl", "https://jid.jasamarga.com/graph/realtime_lalin/matrixcctv/"+idx);
                i.putExtra("judul_app", "CCTV MIDAS");
                v.getContext().startActivity(i);
            });

            alertDialog.show();
            if (alertDialogLineToll != null){
                alertDialogLineToll.cancel();
            }
        }
    }

}
