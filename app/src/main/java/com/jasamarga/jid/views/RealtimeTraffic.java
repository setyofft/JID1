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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jasamarga.jid.Dashboard;
import com.jasamarga.jid.components.Appbar;
import com.jasamarga.jid.components.ShowAlert;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.service.WebClient;

import java.util.HashMap;
import java.util.Objects;

public class RealtimeTraffic extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    Sessionmanager sessionmanager;
    HashMap<String, String> userSession = null;

    private CardView button_exit;
    private SwipeRefreshLayout refreshLayout;
    private ConstraintLayout constraintLayout;
    private WebView content_realtime_trafic;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_traffic);

        url_antrian = getString(R.string.url_real_lalin);
        title = "Realtime Traffic";

        btnMap = findViewById(R.id.btnMap);
        button_exit = findViewById(R.id.button_exit);
        badge = findViewById(R.id.cart_badge);
        sessionmanager = new Sessionmanager(getApplicationContext());
        userSession = sessionmanager.getUserDetails();

        loadingDialog = new LoadingDialog(RealtimeTraffic.this);
        scope = userSession.get(Sessionmanager.set_scope);
        nameInitial = findViewById(R.id.nameInitial);
        nameuser = findViewById(R.id.nameuser);
        username = userSession.get(Sessionmanager.kunci_id);
        refreshLayout = findViewById(R.id.swiperefresh);

        nameuser.setText(username);
        nameInitial.setText(username.substring(0,1).toUpperCase());
        loading = (ProgressBar) findViewById(R.id.loading);
        content_realtime_trafic = findViewById(R.id.content_realtime_trafic);
        if (ServiceFunction.Terkoneksi(this)){
            content_realtime_trafic.setWebViewClient(new WebClient(loading));
        }else {
            ServiceFunction.pesanNosignal(content_realtime_trafic,this);
        }
        contentsetting = content_realtime_trafic.getSettings();
        HashMap<String, String> user = sessionmanager.getUserDetails();
        String token = Objects.requireNonNull(user.get(Sessionmanager.nameToken)).replace("Bearer ","");
        content_realtime_trafic.getSettings().setDomStorageEnabled(true);
        content_realtime_trafic.setVisibility(View.GONE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                content_realtime_trafic.setVisibility(View.VISIBLE);
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
                "}, 2000);";

        String jsLogout = "setTimeout(function() {" +
                "    var appBanners = document.getElementsByClassName('hidden sm:flex sm:gap-2');" +
                "    for (var i = 0; i < appBanners.length; i++) {" +
                "        appBanners[i].style.display = 'none';" +
                "    };" +
                "}, 2000);"; // Penundaan 5 detik

        String css1 = ".grid-rows-* { grid-template-rows: 0px }";
        String js1 = "var style = document.createElement('style'); style.innerHTML = '" + css1 + "'; document.head.appendChild(style);";

        String css = "#mobile-expand-button { display: none }";
        String js = "var style = document.createElement('style'); style.innerHTML = '" + css + "'; document.head.appendChild(style);";

        // Set WebViewClient to handle page loading within WebView
        content_realtime_trafic.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                content_realtime_trafic.loadUrl("javascript:localStorage.setItem('token', '" + token + "');");
                content_realtime_trafic.evaluateJavascript(js1,null);
                content_realtime_trafic.evaluateJavascript(js,null);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                content_realtime_trafic.evaluateJavascript(jscoba,null);
                content_realtime_trafic.evaluateJavascript(jsLogout,null);
                // Log the action for debugging purposes
                content_realtime_trafic.loadUrl("javascript:console.log('Token set in localStorage: ' + localStorage.getItem('token'));");


            }
        });
        content_realtime_trafic.loadUrl(url_antrian);

        contentsetting.setJavaScriptEnabled(true);
        contentsetting.setJavaScriptCanOpenWindowsAutomatically(true);
        contentsetting.setEnableSmoothTransition(true);
        content_realtime_trafic.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        content_realtime_trafic.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        contentsetting.setDomStorageEnabled(true);
        contentsetting.setSaveFormData(true);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnabled(false);

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
    public void onBackPressed() {
        super.onBackPressed();
        ShowAlert.alertExit(this);

    }

    @Override
    public void onRefresh() {
        content_realtime_trafic.reload();
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Appbar.appBar(this,getWindow().getDecorView());

        if (content_realtime_trafic != null) {
            content_realtime_trafic.reload();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (content_realtime_trafic != null) {
            content_realtime_trafic.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (content_realtime_trafic != null) {
            content_realtime_trafic.onPause();
        }
    }

    private void menuBottomnavbar(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.realtime_lalin);

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
                    startActivity(new Intent(getApplicationContext(), Antrian.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.realtime_lalin:
                    return true;
            }
            return false;
        });
    }

}
