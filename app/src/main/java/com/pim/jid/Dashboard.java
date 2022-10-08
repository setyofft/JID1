package com.pim.jid;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.pim.jid.adapter.TabAdapter;
import com.pim.jid.adapter.TableView;
import com.pim.jid.fragment.FragmentLalin;
import com.pim.jid.fragment.FragmentPemeliharaan;
import com.pim.jid.fragment.FragmentPeralataan;
import com.pim.jid.fragment.FragmentTransaksi;
import com.pim.jid.models.ModelListGangguan;
import com.pim.jid.router.ApiClient;
import com.pim.jid.router.ReqInterface;
import com.pim.jid.service.ServiceFunction;
import com.pim.jid.views.Antrian;
import com.pim.jid.views.Cctv;
import com.pim.jid.views.Maps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {

    Sessionmanager sessionmanager;
    HashMap<String, String> userSession = null;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ViewFlipper vf;
    private ColorStateList def;
    private TextView item1,item2,item3,item4,select;
    private ArrayList<ModelListGangguan> modelListGangguans;
    private TextView nameInitial, nameuser, ket_not_found;
    private CardView button_exit;
    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoadingDialog loadingDialog;
    private TableLayout table_gangguan;
    private FloatingActionButton floatingButton;
    private MaterialButton btn_tab_pemeliharaan, btn_tab_gangguan, btn_tab_rekayasa, btnMap;
    private TableRow row_notfound;
    private RecyclerView list_gangguan;

    String username, scope, tipe_lalin;
    JSONArray arrPemeli, arrGanggu, arrRekaya;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_home);

        sessionmanager = new Sessionmanager(getApplicationContext());
        userSession = sessionmanager.getUserDetails();
//        clickTab();
        menuBottomnavbar();
        initVar();
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void initVar(){
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        list_gangguan = findViewById(R.id.list_ruas);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);
        floatingButton = findViewById(R.id.fab);
        nameInitial = findViewById(R.id.nameInitial);
        nameuser = findViewById(R.id.nameuser);
        button_exit = findViewById(R.id.button_exit);
//        table_gangguan = findViewById(R.id.table_gangguan);
        btn_tab_pemeliharaan = findViewById(R.id.btn_tab_pemeliharaan);
        btn_tab_gangguan = findViewById(R.id.btn_tab_gangguan);
        btn_tab_rekayasa = findViewById(R.id.btn_tab_rekayasa);
        btnMap = findViewById(R.id.btnMap);
//        ket_not_found = findViewById(R.id.ket_not_found);
        setTabAdapter();
        tipe_lalin = "gangguan";
        deklarasiVar();
        clickOn();
    }


    private void setTabAdapter(){

        tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.AddFragment(new FragmentLalin(),"Lalu Lintas");
        tabAdapter.AddFragment(new FragmentTransaksi(),"Transaksi");
        tabAdapter.AddFragment(new FragmentPemeliharaan(),"Pemeliharaan");
        tabAdapter.AddFragment(new FragmentPeralataan(),"Peralataan");
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void clickOn() {
        floatingButton.setOnClickListener(v ->{
            PopupMenu popup = new PopupMenu(Dashboard.this, floatingButton,Gravity.END,0,R.style.MyPopupMenu);
            popup.getMenuInflater().inflate(R.menu.fab_menu, popup.getMenu());
            //Inflating the Popup using xml file

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(Dashboard.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            popup.setForceShowIcon(true);
            popup.show();
        });

        button_exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Dashboard.this);
            alertDialogBuilder.setTitle("Peringatan Akun");
            alertDialogBuilder.setMessage("Apakah anda yakin ingin keluar dari akun anda ?");
            alertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.modal_alert));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yakin", (dialog, which) -> ServiceFunction.delSession(this,loadingDialog,username,sessionmanager));
            alertDialogBuilder.setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();
        });

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
        modelListGangguans = new ArrayList<>();
        username = userSession.get(Sessionmanager.kunci_id);
        nameuser.setText(username);
        nameInitial.setText(username.substring(0,1).toUpperCase());
        scope = userSession.get(Sessionmanager.set_scope);
        if (ServiceFunction.Terkoneksi(this)){
            getKejadian_Lalin();
        }else {
            ServiceFunction.pesanNosignalDefault( this);
        }
    }

    private void getKejadian_Lalin() {

        mShimmerViewContainer.startShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.VISIBLE);

//        loadingDialog = new LoadingDialog(Dashboard.this);
//        loadingDialog.showLoadingDialog("Loading...");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id_ruas", scope);

        ReqInterface serviceAPI = ApiClient.getClient();
        Call<JsonObject> call = serviceAPI.excutekejadianlalin(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                        Log.d("STATUS", response.toString());
                    JSONObject dataRes = new JSONObject(response.body().toString());
                    if (dataRes.getString("status").equals("1")){
                        JSONObject dataresult = new JSONObject(dataRes.getString("results"));
                        arrPemeli = new JSONArray(dataresult.getString("pemeliharaan"));
                        arrGanggu = new JSONArray(dataresult.getString("gangguan_lalin"));
                        arrRekaya = new JSONArray(dataresult.getString("rekayasa_lalin"));

                        fetchData();
                    }else{
                        Log.d("STATUS", response.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error Data", call.toString());
                loadingDialog.hideLoadingDialog();
            }
        });
    }

    private void fetchData() {
        mShimmerViewContainer.startShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        try {
            JSONArray fecth;
            if (tipe_lalin.equals("gangguan")){
                fecth = arrGanggu;
            }else if(tipe_lalin.equals("rekayasa")){
                fecth = arrRekaya;
            }else{
                fecth = arrPemeli;
            }
            if (fecth.length() > 0){
                for (int i = 0; i < fecth.length(); i++) {
                    JSONObject getdata = fecth.getJSONObject(i);
                    ModelListGangguan s = new ModelListGangguan();
                    s.setDampak(getdata.getString("dampak"));
                    s.setArah_lajur(getdata.getString("arah_jalur"));
                    s.setNama_ruas(getdata.getString("nama_ruas"));
                    s.setKm(getdata.getString("km"));
                    modelListGangguans.add(s);
                }
            }else{
                Toast.makeText(getApplicationContext(),"Tidak Ada Gangguan Lalu Lintas",Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
            TableView tableView = new TableView(modelListGangguans);
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
                    Toast.makeText(getApplicationContext(), "Sedang tahap pembuatan !", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

}
