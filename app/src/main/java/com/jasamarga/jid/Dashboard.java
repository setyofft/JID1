package com.jasamarga.jid;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.anastr.speedviewlib.Speedometer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jasamarga.jid.adapter.TabAdapter;
import com.jasamarga.jid.adapter.TableView;
import com.jasamarga.jid.components.Appbar;
import com.jasamarga.jid.components.ShowAlert;
import com.jasamarga.jid.fragment.FragmentLalin;
import com.jasamarga.jid.fragment.FragmentPemeliharaan;
import com.jasamarga.jid.fragment.FragmentPeralataan;
import com.jasamarga.jid.models.ModelEventGangguan;
import com.jasamarga.jid.models.ModelEventLalin;
import com.jasamarga.jid.models.ModelListGangguan;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.FirebaseService;
import com.jasamarga.jid.service.LoadingDialog;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.views.Antrian;
import com.jasamarga.jid.views.Cctv;
import com.jasamarga.jid.views.Maps;
import com.jasamarga.jid.views.Notification;
import com.jasamarga.jid.views.RealtimeTraffic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {

    Sessionmanager sessionmanager;

    HashMap<String, String> userSession = null;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ViewFlipper vf;
    private ColorStateList def;
    private TextView item1,item2,item3,item4,select,data_ksong,badge;
    private ArrayList<ModelListGangguan> modelListGangguans;
    private TextView nameInitial, nameuser, ket_not_found,leg1,leg2,leg3,c;
    private CardView button_exit,button_notif;
    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoadingDialog loadingDialog;
    private TableLayout table_gangguan;
//    private FloatingActionButton floatingButton;
    private MaterialButton btn_tab_pemeliharaan, btn_tab_gangguan, btn_tab_rekayasa, btnMap;
    private TableRow row_notfound;
    private RecyclerView list_gangguan;
    String username, scope, tipe_lalin,legend,onpro,done,nopro,error;
    JSONArray arrPemeli, arrGanggu, arrRekaya;
    private ReqInterface serviceAPI;
    private String token;
    private List<ModelEventLalin.DataEvent> dataEventRek,dataEventPem;
    private List<ModelEventGangguan.GangguanData> dataEventGangguan;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String tanggalSkrg,bulanOutputAwal;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_home);
        ServiceFunction.initCheckItem(this,"");
        sessionmanager = new Sessionmanager(this);
        userSession = sessionmanager.getUserDetails();
        Log.d("TOKEN", "onCreate: " + userSession.get(Sessionmanager.nameToken));

//        clickTab();
        menuBottomnavbar();
        initVar();

        updateFileToll();
        updateFileLalin();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void initVar(){
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        list_gangguan = findViewById(R.id.list_ruas);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);
        data_ksong = findViewById(R.id.data_kosong);
        leg1 = findViewById(R.id.legend1);
        leg2 = findViewById(R.id.legend2);
        button_notif = findViewById(R.id.button_bell);
//        floatingButton = findViewById(R.id.fab);
        nameInitial = findViewById(R.id.nameInitial);
        nameuser = findViewById(R.id.nameuser);
        button_exit = findViewById(R.id.button_exit);
        btn_tab_pemeliharaan = findViewById(R.id.btn_tab_pemeliharaan);
        btn_tab_gangguan = findViewById(R.id.btn_tab_gangguan);
        btn_tab_rekayasa = findViewById(R.id.btn_tab_rekayasa);
        btnMap = findViewById(R.id.btnMap);
        badge = findViewById(R.id.cart_badge);
        Appbar.appBar(this,getWindow().getDecorView());
        setTabAdapter();
        tipe_lalin = "gangguan";
        deklarasiVar();
        clickOn();
    }


    private void setTabAdapter(){

        tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.AddFragment(new FragmentLalin(),"Lalu Lintas");
//        tabAdapter.AddFragment(new FragmentTransaksi(),"Transaksi");
        tabAdapter.AddFragment(new FragmentPemeliharaan(),"Pemeliharaan");
        tabAdapter.AddFragment(new FragmentPeralataan(),"Peralataan");
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void clickOn() {
//        floatingButton.setOnClickListener(v ->{
//            PopupMenu popup = new PopupMenu(Dashboard.this, floatingButton,Gravity.END,0,R.style.MyPopupMenu);
//            popup.getMenuInflater().inflate(R.menu.fab_menu, popup.getMenu());
//            //Inflating the Popup using xml file
//            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                public boolean onMenuItemClick(MenuItem item) {
//                    Toast.makeText(Dashboard.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//            });
//            popup.setForceShowIcon(true);
//            popup.show();
//        });
//        button_notif.setOnClickListener(view -> {
//            Intent intent = new Intent(this, Notification.class);
//            startActivity(intent);
//            overridePendingTransition(0,0);
//        });
//        button_exit.setOnClickListener(v -> {
//            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Dashboard.this);
//            alertDialogBuilder.setTitle("Logout");
//            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
//            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
//            alertDialogBuilder.setCancelable(false);
//            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> ServiceFunction.delSession(this,loadingDialog,username,sessionmanager));
//            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
//            alertDialogBuilder.show();
//        });

        btn_tab_rekayasa.setOnClickListener(v -> {
            tipe_lalin = "rekayasa";
            Drawable btnrekaya = btn_tab_rekayasa.getBackground();
            btnrekaya = DrawableCompat.wrap(btnrekaya);
            DrawableCompat.setTint(btnrekaya, getResources().getColor(R.color.gray));
            btn_tab_rekayasa.setTextColor(getResources().getColor(R.color.blue));
            btn_tab_rekayasa.setBackground(btnrekaya);
            btn_tab_rekayasa.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

            Drawable btnpemeli = btn_tab_pemeliharaan.getBackground();
            btnpemeli = DrawableCompat.wrap(btnpemeli);
            DrawableCompat.setTint(btnpemeli, Color.TRANSPARENT);
            btn_tab_pemeliharaan.setTextColor(getResources().getColor(R.color.white));
            btn_tab_pemeliharaan.setBackground(btnpemeli);
            btn_tab_pemeliharaan.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));

            Drawable btngangguan = btn_tab_gangguan.getBackground();
            btngangguan = DrawableCompat.wrap(btngangguan);
            DrawableCompat.setTint(btngangguan, Color.TRANSPARENT);
            btn_tab_gangguan.setTextColor(getResources().getColor(R.color.white));
            btn_tab_gangguan.setBackground(btngangguan);
            btn_tab_gangguan.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));

            if(!modelListGangguans.isEmpty()){
                modelListGangguans.clear();
            }

            fetchData();
        });

        btn_tab_gangguan.setOnClickListener(v -> {
            tipe_lalin = "gangguan";
            Drawable btnrekaya = btn_tab_rekayasa.getBackground();
            btnrekaya = DrawableCompat.wrap(btnrekaya);
            DrawableCompat.setTint(btnrekaya, Color.TRANSPARENT);
            btn_tab_rekayasa.setTextColor(getResources().getColor(R.color.white));
            btn_tab_rekayasa.setBackground(btnrekaya);
            btn_tab_rekayasa.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));

            Drawable btnpemeli = btn_tab_pemeliharaan.getBackground();
            btnpemeli = DrawableCompat.wrap(btnpemeli);
            DrawableCompat.setTint(btnpemeli, Color.TRANSPARENT);
            btn_tab_pemeliharaan.setTextColor(getResources().getColor(R.color.white));
            btn_tab_pemeliharaan.setBackground(btnpemeli);
            btn_tab_pemeliharaan.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));

            Drawable btngangguan = btn_tab_gangguan.getBackground();
            btngangguan = DrawableCompat.wrap(btngangguan);
            DrawableCompat.setTint(btngangguan, getResources().getColor(R.color.gray));
            btn_tab_gangguan.setTextColor(getResources().getColor(R.color.blue));
            btn_tab_gangguan.setBackground(btngangguan);
            btn_tab_gangguan.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

            if(!modelListGangguans.isEmpty()){
                modelListGangguans.clear();
            }
            fetchData();
        });

        btn_tab_pemeliharaan.setOnClickListener(v -> {
            tipe_lalin = "pemeliharaan";
            Drawable btnrekaya = btn_tab_rekayasa.getBackground();
            btnrekaya = DrawableCompat.wrap(btnrekaya);
            DrawableCompat.setTint(btnrekaya, Color.TRANSPARENT);
            btn_tab_rekayasa.setTextColor(getResources().getColor(R.color.white));
            btn_tab_rekayasa.setBackground(btnrekaya);
            btn_tab_rekayasa.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));

            Drawable btnpemeli = btn_tab_pemeliharaan.getBackground();
            btnpemeli = DrawableCompat.wrap(btnpemeli);
            DrawableCompat.setTint(btnpemeli, getResources().getColor(R.color.gray));
            btn_tab_pemeliharaan.setTextColor(getResources().getColor(R.color.blue));
            btn_tab_pemeliharaan.setBackground(btnpemeli);
            btn_tab_pemeliharaan.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.blue)));

            Drawable btngangguan = btn_tab_gangguan.getBackground();
            btngangguan = DrawableCompat.wrap(btngangguan);
            DrawableCompat.setTint(btngangguan, Color.TRANSPARENT);
            btn_tab_gangguan.setTextColor(getResources().getColor(R.color.white));
            btn_tab_gangguan.setBackground(btngangguan);
            btn_tab_gangguan.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            if(!modelListGangguans.isEmpty()){
                modelListGangguans.clear();
            }
            fetchData();
        });

        btnMap.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Maps.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void deklarasiVar(){
//        badge.setVisibility(View.GONE);
//        if(!Objects.equals(ServiceFunction.getBadge(this), "0")){
//            badge.setVisibility(View.VISIBLE);
//            badge.setText(ServiceFunction.getBadge(this));
//        }
        modelListGangguans = new ArrayList<>();
        username = userSession.get(Sessionmanager.kunci_id);
        nameuser.setText(username);
        nameInitial.setText(username.substring(0,1).toUpperCase());
        scope = userSession.get(Sessionmanager.set_scope);
        token = userSession.get(Sessionmanager.nameToken);
        dataEventGangguan = new ArrayList<>();
        dataEventRek = new ArrayList<>();
        dataEventPem = new ArrayList<>();

        if (ServiceFunction.Terkoneksi(this)){
            getAllEvent();
        }else {
            ServiceFunction.pesanNosignalDefault( this);
        }

        Date date = new Date();
        tanggalSkrg = outputFormat.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        Date tahunAwal = calendar.getTime();

        bulanOutputAwal = outputFormat.format(tahunAwal);
    }
    private void getGangguanEvent(){

        ReqInterface serviceAPIP = ApiClientNew.getServiceNew();
        serviceAPIP.getEventGangguan(bulanOutputAwal,tanggalSkrg,"hari",token).enqueue(new Callback<ModelEventGangguan>() {
            @Override
            public void onResponse(Call<ModelEventGangguan> call, Response<ModelEventGangguan> response) {
                if (response.body() != null){
                    Gson gson = new Gson();
                    arrGanggu = new JSONArray(response.body().getData());
                    Log.d(TAG, "onResponseGan: " + gson.toJson(response.body().getData()));
                    dataEventGangguan.addAll(response.body().getData());
                    fetchData();
                }else if (response.code() == 401) {
                    // Handle 401 unauthorized error
                    Log.d(TAG, "Unauthorized: " + response.message());
//                        if (response.message().contains("Unauthorized")){
                    sessionmanager.logout();
                    sessionmanager.checkLogin();
                    finish();
//                        }
                    // You can also show a Toast or perform other actions as needed
                }else {
                    Toast.makeText(getApplicationContext(),"Maaf ada gangguan dari API",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "error Response: " + response.message() + "Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ModelEventGangguan> call, Throwable t) {
                Log.d("Error DataGan", Objects.requireNonNull(t.getMessage()));
                error = "Error saat get Gangguan lalin" + t.getMessage();

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        });
    }

    private void getPemeliharaanEvent(){
        ReqInterface serviceAPIP = ApiClientNew.getServiceNew();
        serviceAPIP.getEventPemeliharaan(bulanOutputAwal,tanggalSkrg,"hari",token).enqueue(new Callback<ModelEventLalin>() {
            @Override
            public void onResponse(Call<ModelEventLalin> call, Response<ModelEventLalin> response) {
                if (response.body() != null){
                    Gson gson = new Gson();
                    arrPemeli = new JSONArray();
                    dataEventPem.addAll(response.body().getData());

                    Log.d(TAG, "onResponseGan: " + gson.toJson(arrPemeli));

                }else if (response.code() == 401) {
                    // Handle 401 unauthorized error
                    Log.d(TAG, "Unauthorized: " + response.message());
//                        if (response.message().contains("Unauthorized")){
                    sessionmanager.logout();
                    sessionmanager.checkLogin();
                    finish();
//                        }
                    // You can also show a Toast or perform other actions as needed
                }else {
                    Toast.makeText(getApplicationContext(),"Maaf ada gangguan dari API",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "error Response: " + response.message() + "Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ModelEventLalin> call, Throwable t) {
                Log.d("Error DataPEM", t.getMessage());

                error = "Error saat get Gangguan lalin" + t.getMessage();

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        });
    }

    private void getRekayasaEvent(){
        ReqInterface serviceAPIR = ApiClientNew.getServiceNew();
        serviceAPIR.getEventRekayasa(bulanOutputAwal,tanggalSkrg,"hari",token).enqueue(new Callback<ModelEventLalin>() {
            @Override
            public void onResponse(Call<ModelEventLalin> call, Response<ModelEventLalin> response) {
                if (response.body() != null){
                    Gson gson = new Gson();
                    arrRekaya = new JSONArray(response.body().getData());
                    dataEventRek.addAll(response.body().getData());
                    Log.d(TAG, "onResponseGan: " + arrRekaya);

                }else if (response.code() == 401) {
                    // Handle 401 unauthorized error
                    Log.d(TAG, "Unauthorized: " + response.message());
//                        if (response.message().contains("Unauthorized")){
                    sessionmanager.logout();
                    sessionmanager.checkLogin();
                    finish();
//                        }
                    // You can also show a Toast or perform other actions as needed
                }else {
                    Toast.makeText(getApplicationContext(),"Maaf ada gangguan dari API",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "error Response: " + response.message() + "Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ModelEventLalin> call, Throwable t) {
                Log.d("Error DataRek", Objects.requireNonNull(t.getMessage()));
                error = "Error saat get Gangguan lalin" + t.getMessage();

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        });
    }
    private void getAllEvent(){
        mShimmerViewContainer.startShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.VISIBLE);

//        getGangguanEvent();
//        getPemeliharaanEvent();
//        getRekayasaEvent();
        getKejadian_Lalin();
//        fetchData();
    }

    private void getKejadian_Lalin() {
        mShimmerViewContainer.startShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.VISIBLE);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id_ruas", scope);

        ReqInterface serviceAPI = ApiClient.getClient(this);
        Call<JsonObject> call = serviceAPI.excutekejadianlalin(jsonObject,token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.body() != null){
                        Log.d("STATUS CEK", String.valueOf(response.body()));
                        JSONObject dataRes = new JSONObject(response.body().toString());
                        arrPemeli = new JSONArray(dataRes.getString("pemeliharaan"));
                        arrGanggu = new JSONArray(dataRes.getString("gangguan_lalin"));
                        arrRekaya = new JSONArray(dataRes.getString("rekayasa_lalin"));
                        Log.d(TAG, "DataEVENTonResponse: " +dataRes);
                        fetchData();
                    }else if (response.code() == 401) {
                        // Handle 401 unauthorized error
                        Log.d(TAG, "Unauthorized: " + response.message());
//                        if (response.message().contains("Unauthorized")){
                        finish();
//                        }
                        // You can also show a Toast or perform other actions as needed
                    }else{
                        error = response.message();
                        Log.d("STATUS ERR CEK", response.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    error = "Error saat get Gangguan lalin" + e.getMessage();
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
                error = "Error saat get Gangguan lalin" + t.getMessage();
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void fetchData() {
//        try {
//            String status;
//            String waktu = "waktu",waktu_awal = "waktu_awal";
//            String detail = "keterangan_detail";
//            if (tipe_lalin.equals("gangguan")){
//                nopro = "Belum Ditangani";
//                onpro = "Dalam Penanganan";
//                done = "Selesai";
//                status = "ket_tipe_gangguan";
//                waktu_awal = "waktu_kejadian";
//                detail = "detail_kejadian";
//                if (dataEventGangguan != null){
//                        for (ModelEventGangguan.GangguanData i : dataEventGangguan){
//                            ModelListGangguan dataList = new ModelListGangguan();
//
//                            dataList.setArah_lajur(i.getLajur());
//                            dataList.setJalur(i.getJalur());
//                            dataList.setKet(i.getKetStatus());
//                            dataList.setDampak(i.getDampak());
//                            dataList.setKm(i.getKm());
//                            dataList.setNama_ruas(i.getNamaRuas());
//                            dataList.setNama_ruas2(i.getNamaRuas());
//                            dataList.setWaktu_awal(i.getWaktuKejadian());
//                            dataList.setWaktu_akhir(i.getWaktuSelesai());
//                            dataList.setWaktu(i.getTglEntri());
//                            dataList.setNama_ruas(i.getNamaRuas());
//                            dataList.setStatus(i.getKetStatus());
//                            dataList.setKet(i.getKetTipeGangguan());
//                            dataList.setTipe(i.getKetStatus());
//                            modelListGangguans.add(dataList);
//                        }
//
//                }else {
//                    data_ksong.setVisibility(View.VISIBLE);
//                    data_ksong.setText("Tidak Ada gangguan lalin");
//                    Toast.makeText(getApplicationContext(),"Tidak Ada Gangguan Lalu Lintas",Toast.LENGTH_SHORT).show();
//                }
//            }else if(tipe_lalin.equals("rekayasa")){
//                onpro = "Dalam Proses";
//                nopro = "Dalam Rencana";
//                done = "Selesai";
//                status = "ket_jenis_kegiatan";
//
//                if (dataEventRek != null) {
//                    for (ModelEventLalin.DataEvent i : dataEventRek) {
//                        ModelListGangguan dataList = new ModelListGangguan();
//
//                        dataList.setArah_lajur(i.getLajur());
//                        dataList.setJalur(i.getJalur());
//                        dataList.setKet(i.getKetStatus());
//                        dataList.setDampak(i.getDampak());
//                        dataList.setKm(i.getKm());
//                        dataList.setWaktu(i.getTglEntri());
//                        dataList.setWaktu_awal(i.getWaktuAwal());
//                        dataList.setWaktu_akhir(i.getWaktuAkhir());
//                        dataList.setNama_ruas(i.getNamaRuas());
//                        dataList.setNama_ruas2(i.getNamaRuas());
//                        dataList.setKet(i.getKeteranganDetail());
//                        dataList.setStatus(i.getKetStatus());
//                        dataList.setTipe(i.getKetStatus());
//                        modelListGangguans.add(dataList);
//                    }
//                }else {
//                    data_ksong.setVisibility(View.VISIBLE);
//                    data_ksong.setText("Tidak ada rekayasa lalin");
//                    Toast.makeText(getApplicationContext(),"Tidak Ada Rekayasa Lalu Lintas",Toast.LENGTH_SHORT).show();
//                }
//
//            }else{
//                onpro = "Dalam Proses";
//                nopro = "Dalam Rencana";
//                done = "Selesai";
//                status = "ket_jenis_kegiatan";
//
//                if (dataEventPem != null){
//                    if (dataEventPem.size() > 0){
//                        for (ModelEventLalin.DataEvent i : dataEventPem){
//                            ModelListGangguan dataList = new ModelListGangguan();
//                            dataList.setArah_lajur(i.getLajur());
//                            dataList.setJalur(i.getJalur());
//                            dataList.setKet(i.getKeteranganDetail());
//                            dataList.setDampak(i.getDampak());
//                            dataList.setKm(i.getKm());
//                            dataList.setWaktu(i.getTglEntri());
//                            dataList.setWaktu_akhir(i.getWaktuAkhir());
//                            dataList.setWaktu_awal(i.getWaktuAwal());
//                            dataList.setNama_ruas(i.getNamaRuas());
//                            dataList.setStatus(i.getKetStatus());
//                            dataList.setKet(i.getKeteranganDetail());
//                            dataList.setTipe(i.getKetStatus());
//
//                            modelListGangguans.add(dataList);
//                        }
//                    }
//                }else {
//                    data_ksong.setVisibility(View.VISIBLE);
//                    data_ksong.setText("Tidak ada pemeliharaan lalin");
//                    Toast.makeText(getApplicationContext(),"Tidak Ada Rekayasa Lalu Lintas",Toast.LENGTH_SHORT).show();
//                }
//            }
            try {
                JSONArray fecth;
                String status,detail = "null";
                String waktu = "waktu",waktu_awal = "waktu_awal";
                if (tipe_lalin.equals("gangguan")){
                    fecth = arrGanggu;
                    nopro = "Belum Ditangani";
                    onpro = "Dalam Penanganan";
                    done = "Selesai";
                    status = "ket_tipe_gangguan";
                    waktu_awal = "waktu_kejadian";
                    detail = "detail_kejadian";
                }else if(tipe_lalin.equals("rekayasa")){
                    fecth = arrRekaya;
                    onpro = "Dalam Proses";
                    nopro = "Dalam Rencana";
                    done = "Selesai";
                    status = "ket_jenis_kegiatan";
                    detail = "keterangan_detail";
                }else{
                    onpro = "Dalam Proses";
                    nopro = "Dalam Rencana";
                    done = "Selesai";
                    status = "ket_jenis_kegiatan";
                    fecth = arrPemeli;
                    detail = "keterangan_detail";
                }
                Log.d(TAG, "fetchData: " + fecth);
                if (fecth != null){
                    for (int i = 0; i < fecth.length(); i++) {
                        JSONObject getdata = fecth.getJSONObject(i);
                        ModelListGangguan s = new ModelListGangguan();
                        s.setArah_lajur(getdata.getString("arah_jalur"));
                        s.setJalur(getdata.getString("jalur"));
                        s.setNama_ruas(getdata.getString("nama_ruas"));
                        s.setKm(getdata.getString("km"));
                        s.setWaktu(getdata.getString(waktu));
                        s.setWaktu_awal(getdata.getString(waktu_awal));
                        if (!tipe_lalin.equals("gangguan")){
                            s.setWaktu_akhir(getdata.getString("waktu_akhir"));
                            s.setRange_pekerjaan(getdata.getString("range_km_pekerjaan"));
                        }
                        s.setTgl_entri(getdata.getString("tgl_entri"));
                        s.setTipe(getdata.getString(status));
                        s.setStatus(getdata.getString("ket_status"));
                        s.setNama_ruas2(getdata.isNull("nama_ruas_2") ? " - " : getdata.getString("nama_ruas_2"));
                        s.setKet(getdata.getString(detail));
                        s.setDampak(getdata.getString("dampak"));
                        modelListGangguans.add(s);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Tidak Ada Gangguan Lalu Lintas",Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            TableView tableView = new TableView(modelListGangguans,this,tipe_lalin);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            list_gangguan.setLayoutManager(linearLayoutManager);
            list_gangguan.setAdapter(tableView);

            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);

    }
    private void menuBottomnavbar(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
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
                    startActivity(new Intent(getApplicationContext(), RealtimeTraffic.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
            }
            return false;
        });
    }
    private void updateFileLalin(){
        serviceAPI = ApiClient.getNoClient(this);
        Call<JsonObject> call = serviceAPI.excutelinetoll();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if(response.body() != null) {

                        JSONObject dataRes = new JSONObject(response.body().toString());
                        Log.d(TAG, "onResponseJSONFILE: " + dataRes);
                        String path = getApplicationContext().getExternalFilesDir(null) + "/datajid/";
                        File checkFile = new File(path);
                        if (!checkFile.exists()) {
                            checkFile.mkdir();
                        }
                        FileWriter file = new FileWriter(checkFile.getAbsolutePath() + "/lalin.json");
                        file.write(dataRes.toString());
                        file.flush();
                        file.close();
                    }else{
                        Log.d("sadde", "Kosong bro ah" + response.toString());
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
            }
        });

    }

    private void updateFileToll(){
        serviceAPI = ApiClient.getNoClient(this);
        Call<JsonObject> call = serviceAPI.excutelinetoll();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if(response.body() != null) {
                        JSONObject dataRes = new JSONObject(response.body().toString());
                        Log.d(TAG, "onResponseJSONFILE: " + dataRes);
                        String path = getApplicationContext().getExternalFilesDir(null) + "/datajid/";
                        File checkFile = new File(path);
                        if (!checkFile.exists()) {
                            checkFile.mkdir();
                        }
                        FileWriter file = new FileWriter(checkFile.getAbsolutePath() + "/tol.json");
                        file.write(dataRes.toString());
                        file.flush();
                        file.close();

                    }else{
                        Log.d("sadde", "Kosong bro ah" + response.toString());
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
            }
        });

    }
    @Override
    public void onBackPressed() {
        ShowAlert.alertExit(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Appbar.appBar(this,getWindow().getDecorView());
        mShimmerViewContainer.startShimmerAnimation();

    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

}
