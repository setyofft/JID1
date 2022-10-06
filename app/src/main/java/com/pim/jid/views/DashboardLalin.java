package com.pim.jid.views;

import android.os.Bundle;
import android.widget.ImageView;

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
import com.pim.jid.fragment.dashlalin.FragmentMenu;
import com.pim.jid.service.ServiceFunction;

import java.util.HashMap;

public class DashboardLalin extends AppCompatActivity {

    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageView;
    HashMap<String, String> userSession = null;
    Sessionmanager sessionmanager;
    private CardView button_exit;
    private LoadingDialog loadingDialog;
    String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_lalin);

        initVar();
    }

    private void initVar(){
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
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_dashlalin)),"Dashboard");
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_real_lalin)),"Realtime Traffic");
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_antrian_gerbang)),"Antrian Gerbang");
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_lalin_perjam)),"Lalin Perjam");
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_data_gangguan)),"Gangguan Lalin");
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_kinlalin)),"Kinerja Lalin");
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_trafik)),"Traffic");
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_trafik_detail)),"Traffic Detail");


        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
