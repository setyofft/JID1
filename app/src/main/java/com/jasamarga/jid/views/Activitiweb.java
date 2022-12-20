package com.jasamarga.jid.views;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jasamarga.jid.R;
import com.jasamarga.jid.components.Appbar;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.service.WebClient;

public class Activitiweb extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout refreshLayout;
    private ConstraintLayout constraintLayout;
    private WebView content_antrian_gerbang;
    private ImageView back;
    private TextView judul,badge;
    private CardView button_bell,button_exit;
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
        badge = findViewById(R.id.cart_badge);
        button_bell = findViewById(R.id.button_bell);
        button_exit = findViewById(R.id.button_exit);
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
        Log.d(TAG, "onCreate Zoom: " +         content_antrian_gerbang.getSettings().getDisplayZoomControls());
        content_antrian_gerbang.getSettings().setBuiltInZoomControls(true);
        contentsetting.setDomStorageEnabled(true);
        contentsetting.setSaveFormData(true);
        contentsetting.setLoadWithOverviewMode(true);
        contentsetting.setUseWideViewPort(true);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        refreshLayout.setOnRefreshListener(this);
        Appbar.appBarNoName(this,getWindow().getDecorView());
        ServiceFunction.addLogActivity(this,title,"",title);


    }

    @Override
    public void onRefresh() {
        content_antrian_gerbang.reload();
        refreshLayout.setRefreshing(false);
    }

}
