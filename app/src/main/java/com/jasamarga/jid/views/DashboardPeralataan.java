package com.jasamarga.jid.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.jasamarga.jid.components.Appbar;
import com.jasamarga.jid.fragment.dashperalataan.FDashboardPeralatan;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.TabAdapter;
import com.jasamarga.jid.fragment.FragmentKosong;
import com.jasamarga.jid.fragment.dashlalin.FragmentMenu;
import com.jasamarga.jid.service.ServiceFunction;

import java.util.HashMap;

public class DashboardPeralataan extends AppCompatActivity {

    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageView;
    private TextView textView;
    HashMap<String, String> userSession = null;
    Sessionmanager sessionmanager;
    private CardView button_exit;
    private LoadingDialog loadingDialog;
    String username;
    int selected;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_transaksi);

        Intent intent = getIntent();
        selected = intent.getIntExtra("selected",0);
        initVar();
    }

    private void initVar(){
        textView = findViewById(R.id.title_app);
        imageView = findViewById(R.id.back);
        button_exit = findViewById(R.id.button_exit);

        sessionmanager = new Sessionmanager(this);
        userSession = sessionmanager.getUserDetails();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.showLoadingDialog("Loading...");

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        setTabAdapter();

        dekVar();
        clickOn();
        Appbar.appBarNoName(this,getWindow().getDecorView());

    }

    private void dekVar(){
        username = userSession.get(Sessionmanager.kunci_id);
        loadingDialog.hideLoadingDialog();
        textView.setText("Dashboard Peralataan");
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void clickOn(){
        imageView.setOnClickListener(v->{
            finish();
        });
        button_exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle("Logout");
            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> ServiceFunction.delSession(this,loadingDialog,username,sessionmanager));
            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();
        });

    }
    private void setTabAdapter(){
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.AddFragment(FragmentMenu.newInstance(getResources().getString(R.string.url_dash_peralataan) + ServiceFunction.getMathRandomWebview(),"Monitoring Alat"),"Dashboard");
        if(ServiceFunction.getUserRole(this,"dash").contains("dsb6")){
            tabAdapter.AddFragment(FragmentMenu.newInstance(getResources().getString(R.string.monitoring_alat)+ ServiceFunction.getMathRandomWebview(),"Monitoring Alat"),"Monitoring Alat");
        }else {
            tabAdapter.AddFragment(new FragmentKosong("Maaf Anda tidak punya akses"),"Monitoring Alat");
        }
        tabAdapter.AddFragment(FragmentMenu.newInstance(getResources().getString(R.string.realtimecctv)+ ServiceFunction.getMathRandomWebview(),"Realtime CCTV"),"Realtime CCTV");
        tabAdapter.AddFragment(FragmentMenu.newInstance(getResources().getString(R.string.realtime_vms)+ ServiceFunction.getMathRandomWebview(),"Realtime DMS"),"Realtime DMS");

        viewPager.setOffscreenPageLimit(0);

        viewPager.setAdapter(tabAdapter);
        tabLayout.stopNestedScroll();
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(selected); // Set the default tab to the first one
    }
}
