package com.jasamarga.jid.views;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
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

public class Antrian extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antrian);

        url_antrian = getString(R.string.url_antrian_gerbang);
        title = "Antrian Gerbang";

        btnMap = findViewById(R.id.btnMap);
        button_exit = findViewById(R.id.button_exit);
        sessionmanager = new Sessionmanager(getApplicationContext());
        userSession = sessionmanager.getUserDetails();

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

        btnMap.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Maps.class));
            overridePendingTransition(0, 0);
            finish();
        });
        Appbar.appBar(this,getWindow().getDecorView());
        ServiceFunction.addLogActivity(this,title,"",title);



//        button_exit.setOnClickListener(v -> {
//            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Antrian.this);
//            alertDialogBuilder.setTitle("Peringatan Akun");
//            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
//            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
//            alertDialogBuilder.setCancelable(false);
//            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> ServiceFunction.delSession(getApplicationContext(),loadingDialog,username,sessionmanager));
//            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
//            alertDialogBuilder.show();
//        });
        menuBottomnavbar();

    }

    @Override
    public void onRefresh() {
        content_antrian_gerbang.reload();
        refreshLayout.setRefreshing(false);
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
