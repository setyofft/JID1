package com.pim.jid.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pim.jid.R;
import com.pim.jid.service.ServiceFunction;
import com.pim.jid.service.WebClient;

public class Activitiweb extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout refreshLayout;
    private ConstraintLayout constraintLayout;
    private WebView content_antrian_gerbang;
    private ImageView back;
    private TextView judul;
    WebSettings contentsetting;

    String url_antrian,title;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.antriangerbang_layout);

        Intent intent = getIntent();
        url_antrian = intent.getStringExtra("hosturl");
        title = intent.getStringExtra("judul_app");
        constraintLayout = findViewById(R.id.layout);
        back = findViewById(R.id.back);
        back.setColorFilter(Color.BLACK);
        back.setOnClickListener(v->{
            finish();
        });
        judul = findViewById(R.id.title_app);
        judul.setText(title);
        judul.setTextColor(Color.BLACK);
        constraintLayout.setBackgroundColor(Color.WHITE);
        loading = (ProgressBar) findViewById(R.id.loading);
        content_antrian_gerbang = findViewById(R.id.content_antrian_gerbang);
        if (ServiceFunction.Terkoneksi(this)){
            content_antrian_gerbang.setWebViewClient(new WebClient(loading));
        }else {
            ServiceFunction.pesanNosignal(content_antrian_gerbang,this);
        }
        contentsetting = content_antrian_gerbang.getSettings();
        content_antrian_gerbang.loadUrl(url_antrian);

        contentsetting.setJavaScriptEnabled(true);
        contentsetting.setJavaScriptCanOpenWindowsAutomatically(true);
        contentsetting.setEnableSmoothTransition(true);
        content_antrian_gerbang.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        content_antrian_gerbang.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        content_antrian_gerbang.getSettings().setAppCacheEnabled(true);
        contentsetting.setDomStorageEnabled(true);
        contentsetting.setSaveFormData(true);
        contentsetting.setLoadWithOverviewMode(true);
        contentsetting.setUseWideViewPort(true);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void onRefresh() {
        content_antrian_gerbang.reload();
        refreshLayout.setRefreshing(false);
    }

}
