package com.jasamarga.jid.components;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.jasamarga.jid.R;
import com.jasamarga.jid.adapter.CctvViewPagerAdapter;
import com.jasamarga.jid.models.CctvSegmentModel;

import java.util.ArrayList;

public class PopupCctv extends DialogFragment {
    private MaterialButton materialButton;
    private ViewPager2 viewPager2;
    private ArrayList<CctvSegmentModel> models;
    Handler handler;
    String status;
    int km;


    public PopupCctv(ArrayList<CctvSegmentModel> model,String status,Handler handler,int km){
        this.models = model;
        this.status = status;
        this.handler = handler;
        this.km = km;
    }

    public static PopupCctv display(FragmentManager fragmentManager,ArrayList<CctvSegmentModel> model,String status,Handler handler,int km) {
        PopupCctv exampleDialog = new PopupCctv(model,status,handler,km);
        exampleDialog.show(fragmentManager, "popup");
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.popup_cctv, container, false);
        initVar(view);
        return view;
    }

    private void initVar(View v){
        materialButton = v.findViewById(R.id.btn_close);
        viewPager2 = v.findViewById(R.id.viewPager);
        setClick();
        setData();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setData() {
        CctvViewPagerAdapter cctvViewPagerAdapter = new CctvViewPagerAdapter(models, viewPager2,status, getContext(), handler);
        viewPager2.setAdapter(cctvViewPagerAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setCurrentItem(km);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(0));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);

            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
    }

    private void setClick(){
        materialButton.setOnClickListener(v->{
            handler.removeCallbacksAndMessages(null);
            dismiss();
        });
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

}
