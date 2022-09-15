package com.pim.jid.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pim.jid.R;

public class Activitiweb extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout refreshLayout;
    private WebView content_antrian_gerbang;
    WebSettings contentsetting;

    String url_antrian;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.antriangerbang_layout);

        Intent intent = getIntent();
        url_antrian = intent.getStringExtra("hosturl");

        loading = (ProgressBar) findViewById(R.id.loading);
        content_antrian_gerbang = findViewById(R.id.content_antrian_gerbang);
        if (Terkoneksi()){
            content_antrian_gerbang.setWebViewClient(new Activitiweb.myWebClient());
        }else {
            PesanNosignal();
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

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void onRefresh() {
        content_antrian_gerbang.reload();
        refreshLayout.setRefreshing(false);
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            loading.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            loading.setVisibility(View.GONE);
        }
    }

    private boolean Terkoneksi(){
        boolean connectStatus = true;
        ConnectivityManager ConnectionManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true ) {
            connectStatus = true;
        }
        else {
            connectStatus = false;
        }
        return connectStatus;
    }

    private void PesanNosignal(){
        content_antrian_gerbang.setVisibility(WebView.GONE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Silahakan Cek Internet Anda !");
        alertDialogBuilder
                .setMessage("Klik 'YA' Untuk Coba Lagi !")
                .setIcon(R.drawable.logojm)
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        finish();
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
