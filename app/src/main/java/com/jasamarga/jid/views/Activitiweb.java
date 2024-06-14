package com.jasamarga.jid.views;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.components.Appbar;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.service.WebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activitiweb extends AppCompatActivity{

    private ConstraintLayout constraintLayout;
    private WebView content_antrian_gerbang;
    private ImageView back;
    private TextView judul,badge;
    private CardView button_bell,button_exit;
    WebSettings contentsetting;
    String url_antrian,title;
    private ProgressBar loading;
    Sessionmanager sessionmanager;
    int delayMillis = 5000;

    @SuppressLint("SetJavaScriptEnabled")
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
        sessionmanager = new Sessionmanager(this);
        // Enable JavaScript in WebView
        setUpWebview();
        Appbar.appBarNoName(this,getWindow().getDecorView());
        ServiceFunction.addLogActivity(this,title,"",title);
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebview(){
        WebSettings contentsetting = content_antrian_gerbang.getSettings();
        contentsetting.setJavaScriptEnabled(true);
        loading.setVisibility(View.VISIBLE);
        // Retrieve token from SessionManager
        HashMap<String, String> user = sessionmanager.getUserDetails();
        String token = Objects.requireNonNull(user.get(Sessionmanager.nameToken)).replace("Bearer ","");
        content_antrian_gerbang.setWebChromeClient(new WebChromeClient());
        content_antrian_gerbang.evaluateJavascript("javascript:localStorage.setItem('token', '" + token + "');", null);
        content_antrian_gerbang.getSettings().setDomStorageEnabled(true);
        contentsetting.setBuiltInZoomControls(true);
        String jscoba = "setTimeout(function() {" +
                "var sidebar = document.getElementById('sidebar');" +
                "if (sidebar) {" +
                "console.log('Sidebar found, hiding it now.');" +
                "sidebar.style.display = 'none';" +
                "} else {" +
                "console.log('Sidebar not found.');" +
                "}" +
                "}, 2000);";

        String jsLogout = "setTimeout(function() {" +
                "    var appBanners = document.getElementsByClassName('hidden sm:flex sm:gap-2');" +
                "    for (var i = 0; i < appBanners.length; i++) {" +
                "        appBanners[i].style.display = 'none';" +
                "    };" +
                "}, 2000);"; // Penundaan 5 detik
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                content_antrian_gerbang.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            }
        }, delayMillis);
        String css1 = ".grid-rows-* { grid-template-rows: 0px }";
        String js1 = "var style = document.createElement('style'); style.innerHTML = '" + css1 + "'; document.head.appendChild(style);";

        String css = "#mobile-expand-button { display: none }";
        String js = "var style = document.createElement('style'); style.innerHTML = '" + css + "'; document.head.appendChild(style);";
        // Set WebViewClient to handle page loading within WebView
        content_antrian_gerbang.setVisibility(View.GONE);
        content_antrian_gerbang.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                content_antrian_gerbang.loadUrl("javascript:localStorage.setItem('token', '" + token + "');");
//                content_antrian_gerbang.evaluateJavascript(jscoba,null);
                content_antrian_gerbang.evaluateJavascript(js1,null);
                content_antrian_gerbang.evaluateJavascript(js,null);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                content_antrian_gerbang.evaluateJavascript(jscoba,null);
                content_antrian_gerbang.evaluateJavascript(jsLogout,null);
                // Log the action for debugging purposes
                content_antrian_gerbang.loadUrl("javascript:console.log('Token set in localStorage: ' + localStorage.getItem('token'));");

            }
        });

        // Load the URL
        if (ServiceFunction.Terkoneksi(this)) {
            content_antrian_gerbang.loadUrl(url_antrian);
        } else {
            ServiceFunction.pesanNosignal(content_antrian_gerbang, this);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (content_antrian_gerbang != null) {
//            content_antrian_gerbang.onPause();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (content_antrian_gerbang != null) {
            content_antrian_gerbang.onPause();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (content_antrian_gerbang != null) {
            content_antrian_gerbang.reload();
        }else {
            setUpWebview();
        }
    }
}
