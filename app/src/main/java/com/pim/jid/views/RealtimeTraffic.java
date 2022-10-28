package com.pim.jid.views;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pim.jid.Dashboard;
import com.pim.jid.LoadingDialog;
import com.pim.jid.R;
import com.pim.jid.Sessionmanager;
import com.pim.jid.service.ServiceFunction;
import com.pim.jid.service.WebClient;

import java.util.HashMap;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_traffic);

        url_antrian = getString(R.string.url_real_lalin);
        title = "Realtime Traffic";

        btnMap = findViewById(R.id.btnMap);
        button_exit = findViewById(R.id.button_exit);
        sessionmanager = new Sessionmanager(getApplicationContext());
        userSession = sessionmanager.getUserDetails();

        loadingDialog = new LoadingDialog(RealtimeTraffic.this);
        scope = userSession.get(Sessionmanager.set_scope);
        nameInitial = findViewById(R.id.nameInitial);
        nameuser = findViewById(R.id.nameuser);
        username = userSession.get(Sessionmanager.kunci_id);

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
        content_realtime_trafic.loadUrl(url_antrian);

        contentsetting.setJavaScriptEnabled(true);
        contentsetting.setJavaScriptCanOpenWindowsAutomatically(true);
        contentsetting.setEnableSmoothTransition(true);
        content_realtime_trafic.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        content_realtime_trafic.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        content_realtime_trafic.getSettings().setAppCacheEnabled(true);
        contentsetting.setDomStorageEnabled(true);
        contentsetting.setSaveFormData(true);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(this);

        btnMap.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Maps.class));
            overridePendingTransition(0, 0);
            finish();
        });

        button_exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(RealtimeTraffic.this);
            alertDialogBuilder.setTitle("Peringatan Akun");
            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> ServiceFunction.delSession(getApplicationContext(),loadingDialog,username,sessionmanager));
            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();
        });
        menuBottomnavbar();

    }

    @Override
    public void onRefresh() {
        content_realtime_trafic.reload();
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
