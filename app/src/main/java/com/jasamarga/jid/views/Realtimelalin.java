package com.jasamarga.jid.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.jasamarga.jid.R;

public class Realtimelalin extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private WebView content_realtimelalin;
    WebSettings contentsetting;

    String url_realtime = "https://jid.jasamargalive.com/graph/realtime_lalin_mobile";
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realtimelalin_layout);

        loading = (ProgressBar) findViewById(R.id.loading);
        content_realtimelalin = findViewById(R.id.content_realtimelalin);
        if (Terkoneksi()){
            content_realtimelalin.setWebViewClient(new myWebClient());
        }else {
            PesanNosignal();
        }
        contentsetting = content_realtimelalin.getSettings();
        content_realtimelalin.loadUrl(url_realtime);

        contentsetting.setJavaScriptEnabled(true);
        contentsetting.setJavaScriptCanOpenWindowsAutomatically(true);
        contentsetting.setEnableSmoothTransition(true);
        content_realtimelalin.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        content_realtimelalin.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        content_realtimelalin.getSettings().setAppCacheEnabled(true);
        contentsetting.setDomStorageEnabled(true);
        contentsetting.setSaveFormData(true);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void onRefresh() {
        content_realtimelalin.reload();
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
        content_realtimelalin.setVisibility(WebView.GONE);
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
