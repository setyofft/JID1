package com.jasamarga.jid.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.jasamarga.jid.fragment.FragmentDataGangguan;
import com.jasamarga.jid.fragment.dashlalin.FAntrianGerbang;
import com.jasamarga.jid.fragment.dashlalin.FDashboardLalin;
import com.jasamarga.jid.fragment.dashlalin.FRealtimeTraffic;
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
    int selected;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_lalin);
        Intent intent = getIntent();
        selected = intent.getIntExtra("selected",0);
        initVar();
    }

    private void initVar(){
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
    }
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
        tabAdapter.AddFragment(new FDashboardLalin(), "Summary"); // Add null as a placeholder for the fragment
        tabAdapter.AddFragment(FragmentMenu.newInstance(getResources().getString(R.string.url_trafik) + ServiceFunction.getMathRandomWebview(), "Realtime Traffic"), "Realtime Traffic");
//        tabAdapter.AddFragment(new FAntrianGerbang(), "Antrian Gerbang");
//        tabAdapter.AddFragment(FragmentMenu.newInstance(getResources().getString(R.string.url_antrian_gerbang) + ServiceFunction.getMathRandomWebview(), "Antrian Gerbang"), "Antrian Gerbang");

        if (ServiceFunction.getUserRole(this, "dash").contains("dsb13")) {
            tabAdapter.AddFragment(FragmentMenu.newInstance(getResources().getString(R.string.url_lalin_perjam) + ServiceFunction.getMathRandomWebview(), "Lalin Perjam"), "Lalin Perjam");
        } else {
            tabAdapter.AddFragment(new FragmentKosong(getString(R.string.not_scope) + ServiceFunction.getMathRandomWebview()), "Lalin Perjam");
        }
        tabAdapter.AddFragment(new FragmentDataGangguan(),"Gangguan Lalin");
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(selected); // Set the default tab to the first one

        // Set up the tab selection listener to load fragments dynamically
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                loadFragment(tab.getPosition());
                Log.d("TABLAY", "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Implement if needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Implement if needed
            }
        });
    }

    private void loadFragment(int position) {
        if (tabAdapter.getFragment(position) == null) {
            // Load the fragment dynamically if it's not already loaded
            switch (position) {
                case 0:
                    tabAdapter.UpdateFragment(new FDashboardLalin(), position);
                    break;
                case 1:
                    tabAdapter.UpdateFragment(new FRealtimeTraffic(), position);
                    break;
                case 2:
                    tabAdapter.UpdateFragment(new FAntrianGerbang(), position);
                    break;
                case 3:
                    if (ServiceFunction.getUserRole(this, "dash").contains("dsb13")) {
                        tabAdapter.UpdateFragment(FragmentMenu.newInstance(getResources().getString(R.string.url_lalin_perjam) + ServiceFunction.getMathRandomWebview(), "Lalin Perjam"), position);
                    } else {
                        tabAdapter.UpdateFragment(new FragmentKosong(getString(R.string.not_scope) + ServiceFunction.getMathRandomWebview()), position);
                    }
                    break;
                case 4:
                    tabAdapter.UpdateFragment(FragmentMenu.newInstance(getResources().getString(R.string.url_data_gangguan) + ServiceFunction.getMathRandomWebview(), "Gangguan Lalin"), position);
                    break;
                // Add cases for other tabs if needed
            }
        }
        viewPager.setCurrentItem(position, false); // Set the current item without animation
    }
}
