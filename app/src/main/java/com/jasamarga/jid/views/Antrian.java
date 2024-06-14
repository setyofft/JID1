package com.jasamarga.jid.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jasamarga.jid.Dashboard;
import com.jasamarga.jid.components.Appbar;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.service.WebClient;

import java.util.HashMap;
import java.util.Objects;

public class  Antrian extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    Sessionmanager sessionmanager;
    HashMap<String, String> userSession = null;

    private CardView button_exit;
    private SwipeRefreshLayout refreshLayout;
    private ConstraintLayout constraintLayout;
    private WebView content_antrian_gerbang;
    private ImageView back;
    private TextView judul;
    private MaterialButton btnMap;
    private TextView nameInitial, nameuser;

    WebSettings contentsetting;

    String username,scope;
    LoadingDialog loadingDialog;
    String url_antrian,title;
    private ProgressBar loading;
    private TextView badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antrian);

        url_antrian = getString(R.string.url_antrian_gerbang);
        title = "Antrian Gerbang";

        btnMap = findViewById(R.id.btnMap);
        button_exit = findViewById(R.id.button_exit);
        badge = findViewById(R.id.cart_badge);
        sessionmanager = new Sessionmanager(getApplicationContext());
        userSession = sessionmanager.getUserDetails();
        refreshLayout = findViewById(R.id.swiperefresh);
        loadingDialog = new LoadingDialog(Antrian.this);
        scope = userSession.get(Sessionmanager.set_scope);
        nameInitial = findViewById(R.id.nameInitial);
        nameuser = findViewById(R.id.nameuser);
        username = userSession.get(Sessionmanager.kunci_id);

        nameuser.setText(username);
        nameInitial.setText(username.substring(0,1).toUpperCase());
        loading = (ProgressBar) findViewById(R.id.loading);
        content_antrian_gerbang = findViewById(R.id.content_antrian_gerbang);
        if (ServiceFunction.Terkoneksi(this)){
            content_antrian_gerbang.setWebViewClient(new WebClient(loading));
        }else {
            ServiceFunction.pesanNosignal(content_antrian_gerbang,this);
        }
        contentsetting = content_antrian_gerbang.getSettings();
        content_antrian_gerbang.getSettings().setDomStorageEnabled(true);
        HashMap<String, String> user = sessionmanager.getUserDetails();
        String token = Objects.requireNonNull(user.get(Sessionmanager.nameToken)).replace("Bearer ","");
        refreshLayout.setEnabled(false);

        String css1 = ".grid-rows-* { grid-template-rows: 0px }";
        String js1 = "var style = document.createElement('style'); style.innerHTML = '" + css1 + "'; document.head.appendChild(style);";

        String css = "#mobile-expand-button { display: none }";
        String js = "var style = document.createElement('style'); style.innerHTML = '" + css + "'; document.head.appendChild(style);";
        content_antrian_gerbang.setVisibility(View.GONE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                content_antrian_gerbang.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            }
        }, 5000);
        String jscoba = "setTimeout(function() {" +
                "var sidebar = document.getElementById('sidebar');" +
                "if (sidebar) {" +
                "console.log('Sidebar found, hiding it now.');" +
                "sidebar.style.display = 'none';" +
                "} else {" +
                "console.log('Sidebar not found.');" +
                "}" +
                "}, 3000);";

        String jsLogout = "setTimeout(function() {" +
                "    var appBanners = document.getElementsByClassName('hidden sm:flex sm:gap-2');" +
                "    for (var i = 0; i < appBanners.length; i++) {" +
                "        appBanners[i].style.display = 'none';" +
                "    };" +
                "}, 2000);"; // Penundaan 5 detik
        // Set WebViewClient to handle page loading within WebView
        content_antrian_gerbang.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                content_antrian_gerbang.evaluateJavascript(js1,null);
                content_antrian_gerbang.evaluateJavascript(js,null);
                content_antrian_gerbang.loadUrl("javascript:localStorage.setItem('token', '" + token + "');");

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Log the action for debugging purposes
                content_antrian_gerbang.evaluateJavascript(jscoba,null);
                content_antrian_gerbang.evaluateJavascript(jsLogout,null);
                content_antrian_gerbang.loadUrl("javascript:console.log('Token set in localStorage: ' + localStorage.getItem('token'));");

            }

        });
        content_antrian_gerbang.loadUrl(url_antrian);

        contentsetting.setJavaScriptEnabled(true);
        contentsetting.setJavaScriptCanOpenWindowsAutomatically(true);
        contentsetting.setEnableSmoothTransition(true);
        content_antrian_gerbang.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        content_antrian_gerbang.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        contentsetting.setDomStorageEnabled(true);
        contentsetting.setSaveFormData(true);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(this);

        btnMap.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Maps.class));
            overridePendingTransition(0, 0);
            finish();
        });
        Appbar.appBar(this,getWindow().getDecorView());
        ServiceFunction.addLogActivity(this,title,"",title);
        menuBottomnavbar();

    }

    @Override
    public void onRefresh() {
        content_antrian_gerbang.reload();
        refreshLayout.setRefreshing(false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Appbar.appBar(this,getWindow().getDecorView());

        if (content_antrian_gerbang != null) {
            content_antrian_gerbang.reload();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (content_antrian_gerbang != null) {
            content_antrian_gerbang.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (content_antrian_gerbang != null) {
            content_antrian_gerbang.onPause();
        }
    }
    private void menuBottomnavbar(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.antrian_gerbang);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.cctv:
                    startActivity(new Intent(getApplicationContext(), Cctv.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.antrian_gerbang:
                    return true;
                case R.id.realtime_lalin:
                    startActivity(new Intent(getApplicationContext(), RealtimeTraffic.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
            }
            return false;
        });
    }


}
