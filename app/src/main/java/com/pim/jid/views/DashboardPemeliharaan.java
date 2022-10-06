package com.pim.jid.views;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.pim.jid.LoadingDialog;
import com.pim.jid.R;
import com.pim.jid.Sessionmanager;
import com.pim.jid.adapter.TabAdapter;
import com.pim.jid.fragment.FragmentDashTransaksi;
import com.pim.jid.fragment.FragmentKosong;
import com.pim.jid.fragment.dashlalin.FragmentMenu;
import com.pim.jid.service.ServiceFunction;

import java.util.HashMap;

public class DashboardPemeliharaan extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_transaksi);

        initVar();
    }

    private void initVar(){
        textView = findViewById(R.id.title_app);
        imageView = findViewById(R.id.back);
        button_exit = findViewById(R.id.button_exit);

        sessionmanager = new Sessionmanager(getApplicationContext());
        userSession = sessionmanager.getUserDetails();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.showLoadingDialog("Loading...");

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        setTabAdapter();

        dekVar();
        clickOn();
    }

    private void dekVar(){
        username = userSession.get(Sessionmanager.kunci_id);
        loadingDialog.hideLoadingDialog();
        textView.setText("Dashboard Pemeliharaan");
    }
    private void clickOn(){
        imageView.setOnClickListener(v->{
            finish();
        });
        button_exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle("Peringatan Akun");
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
        tabAdapter.AddFragment(new FragmentMenu(getString(R.string.url_dash_pemeliharaan)),"Dashboard");
        tabAdapter.AddFragment(new FragmentMenu(getString(R.string.url_data_pemeliharaan)),"Data Pemeliharaan");
        tabAdapter.AddFragment(new FragmentMenu(getString(R.string.url_water_level)),"Water Level Sensor");


        viewPager.setAdapter(tabAdapter);
        tabLayout.stopNestedScroll();
        tabLayout.setupWithViewPager(viewPager);
    }
}
