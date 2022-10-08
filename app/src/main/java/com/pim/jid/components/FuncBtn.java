package com.pim.jid.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.button.MaterialButton;
import com.pim.jid.R;

import java.util.Objects;

public class FuncBtn {
    @SuppressLint("ResourceType")
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
