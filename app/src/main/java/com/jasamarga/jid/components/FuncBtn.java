package com.jasamarga.jid.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.jasamarga.jid.R;

import java.util.Objects;

public class FuncBtn {
    @SuppressLint("ResourceType")
    public static void showBottomSheet(BottomSheetDialog sheetDialog, Activity activity,BottomSheetBehavior sheetBehavior){
        View view = activity.getLayoutInflater().inflate(R.layout.layer_dialog, null);

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        sheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetTheme);
        Chip switch_jalan_toll = view.findViewById(R.id.switch_jalan_toll);
        Chip switch_kondisi_traffic = view.findViewById(R.id.switch_kondisi_traffic);
        Chip switch_cctv = view.findViewById(R.id.switch_cctv);
        Chip switch_vms = view.findViewById(R.id.switch_vms);
        Chip switch_pemeliharaan = view.findViewById(R.id.switch_pemeliharaan);
        Chip switch_gangguan_lalin = view.findViewById(R.id.switch_gangguan_lalin);
        Chip switch_rekayasalalin = view.findViewById(R.id.switch_rekayasalalin);
        Chip switch_batas_km = view.findViewById(R.id.switch_batas_km);
        Chip switch_jalan_penghubung = view.findViewById(R.id.switch_jalan_penghubung);
        Chip switch_gerbang_tol = view.findViewById(R.id.switch_gerbang_tol);
        Chip switch_rest_Area = view.findViewById(R.id.switch_rest_Area);
        Chip switch_roughnes_index = view.findViewById(R.id.switch_roughnes_index);
        Chip switch_rtms = view.findViewById(R.id.switch_rtms);
        Chip switch_rtms2 = view.findViewById(R.id.switch_rtms2);
        Chip switch_radar = view.findViewById(R.id.switch_radar);
        Chip switch_speed = view.findViewById(R.id.switch_speed);
        Chip switch_water_level = view.findViewById(R.id.switch_water_level);
        Chip switch_pompa_banjir = view.findViewById(R.id.switch_pompa_banjir);
        Chip switch_wim_bridge = view.findViewById(R.id.switch_wim_bridge);
        Chip switch_gps_kend_opra = view.findViewById(R.id.switch_gps_kend_opra);
        Chip switch_sepeda_montor = view.findViewById(R.id.switch_sepeda_montor);

        Button set_layer = view.findViewById(R.id.set_layer);

        sheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        sheetDialog.show();

        BottomSheetDialog finalSheetDialog = sheetDialog;
        sheetDialog.setOnDismissListener(dialog -> finalSheetDialog.dismiss());
    }
    public static void btnLalin(MaterialButton btn_tab_rekayasa, MaterialButton btn_tab_pemeliharaan, MaterialButton btn_tab_gangguan, Context context, String click){
        int colorClick = R.color.white;
        int color2Click = Color.TRANSPARENT;
        if(Objects.equals(click, "gangguan")){
            colorClick = R.color.blue;
            color2Click = R.color.grey;

            Drawable btngangguan = btn_tab_gangguan.getBackground();
            btngangguan = DrawableCompat.wrap(btngangguan);
            DrawableCompat.setTint(btngangguan, context.getResources().getColor(color2Click));

            btn_tab_gangguan.setTextColor(context.getResources().getColor(colorClick));
            btn_tab_gangguan.setBackground(btngangguan);
            btn_tab_gangguan.setIconTint(ColorStateList.valueOf(context.getResources().getColor(colorClick)));
        }else if(click.equals("rekayasa")){
            colorClick = R.color.blue;
            color2Click = R.color.grey;

            Drawable btnrekaya = btn_tab_rekayasa.getBackground();
            btnrekaya = DrawableCompat.wrap(btnrekaya);
            DrawableCompat.setTint(btnrekaya,context.getResources().getColor(color2Click));

            btn_tab_rekayasa.setTextColor(context.getResources().getColor(colorClick));
            btn_tab_rekayasa.setBackground(btnrekaya);
            btn_tab_rekayasa.setIconTint(ColorStateList.valueOf(context.getResources().getColor(colorClick)));

        }else if (click.equals("pemeliharaan")){
            colorClick = R.color.blue;
            color2Click = R.color.grey;

            Drawable btnpemeli = btn_tab_pemeliharaan.getBackground();
            btnpemeli = DrawableCompat.wrap(btnpemeli);
            DrawableCompat.setTint(btnpemeli, context.getResources().getColor(color2Click));

            btn_tab_pemeliharaan.setTextColor(context.getResources().getColor(colorClick));
            btn_tab_pemeliharaan.setBackground(btnpemeli);
            btn_tab_pemeliharaan.setIconTint(ColorStateList.valueOf(context.getResources().getColor(colorClick)));

        }else {
            colorClick = R.color.white;
            color2Click = R.color.biru2;
        }

    }
}
