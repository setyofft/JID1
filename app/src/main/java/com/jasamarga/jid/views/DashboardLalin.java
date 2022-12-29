package com.jasamarga.jid.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.jasamarga.jid.components.Appbar;
import com.jasamarga.jid.components.ShowAlert;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.TabAdapter;
import com.jasamarga.jid.fragment.FragmentKosong;
import com.jasamarga.jid.fragment.dashlalin.FragmentMenu;
import com.jasamarga.jid.service.ServiceFunction;

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
        Appbar.appBarNoName(this,getWindow().getDecorView());
        ServiceFunction.addLogActivity(this,"Dashboard Lalu Lintas","","Dashboard Lalu Lintas");

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
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_dashlalin) + ServiceFunction.getMathRandomWebview(),"Dashboard"),"Dashboard");
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_real_lalin)+ ServiceFunction.getMathRandomWebview(),"Realtime Traffic"),"Realtime Traffic");
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_antrian_gerbang)+ ServiceFunction.getMathRandomWebview(),"Antrian Gerbang"),"Antrian Gerbang");
        if(ServiceFunction.getUserRole(getApplicationContext(),"dash").contains("dsb13")){
            tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_lalin_perjam)+ ServiceFunction.getMathRandomWebview(),"Lalin Perjam"),"Lalin Perjam");
        }else {
            tabAdapter.AddFragment(new FragmentKosong(getString(R.string.not_scope)+ ServiceFunction.getMathRandomWebview()),"Lalin Perjam");
        }
        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_data_gangguan)+ ServiceFunction.getMathRandomWebview(),"Gangguan Lalin"),"Gangguan Lalin");
//        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_kinlalin)),"Kinerja Lalin");
//        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_trafik)),"Traffic");
//        tabAdapter.AddFragment(new FragmentMenu(getResources().getString(R.string.url_trafik_detail)),"Traffic Detail");


        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
