package com.jasamarga.jid.fragment.dashlalin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.models.ModelGangguan;
import com.jasamarga.jid.models.ModelRekayasa;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ReqInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FDashboardLalin extends Fragment {
    private TextView date_r_lalin,dateG_lalin,angka_oneway,angka_contraflow,angka_pengalihan,angkaKerjaan,angkaKecelakaan,angka_g_lalin,angkaGenangan,angkaPArus,angkaLain;
    private Sessionmanager sessionmanager;
    private RadioGroup radioGroupTahun;
    private RadioButton rbtn_tahun,rbtn_hari;
    private ReqInterface api;
    HashMap<String,String> dataUser ;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ModelRekayasa.InnerData dataTRekayasa;
    private ModelRekayasa.InnerDataDaily dataDailyRekayasa;
    private ModelGangguan.DataKerjaan dataTGangguan;
    private ModelGangguan.DataKerjaanDaily dataGanggaunDaily;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_f_dashboard_lalin, container, false);
        angka_oneway = view.findViewById(R.id.angka_oneway);
        angka_contraflow = view.findViewById(R.id.angka_contraflow);
        angka_pengalihan = view.findViewById(R.id.angka_pengalihan);
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        angkaKerjaan = view.findViewById(R.id.angkaKerjaan);
        angkaKecelakaan = view.findViewById(R.id.angkaKecelakaan);
        angka_g_lalin = view.findViewById(R.id.angka_g_lalin);
        angkaGenangan = view.findViewById(R.id.angka_genangan);
        angkaPArus = view.findViewById(R.id.angkaPArus);
        angkaLain = view.findViewById(R.id.angkaLain);
        radioGroupTahun = view.findViewById(R.id.radioGroupTahun);
        rbtn_hari = view.findViewById(R.id.rbtn_hari);
        rbtn_tahun = view.findViewById(R.id.rbtn_tahun);
        date_r_lalin = view.findViewById(R.id.date_r_lalin);
        dateG_lalin = view.findViewById(R.id.dateG_lalin);
        sessionmanager = new Sessionmanager(requireActivity());
        api = ApiClient.getServiceNew(requireActivity());
        dataUser = sessionmanager.getUserDetails();
        Date date = new Date();
        String tanggalSkrg = "Data dari "+outputFormat.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR,1);
        Date tahunAwal = calendar.getTime();
        String tahunOutputAwal = "Data dari " + outputFormat.format(tahunAwal) +" sampai " + outputFormat.format(date);
        if (rbtn_hari.isChecked()){
            date_r_lalin.setText(tanggalSkrg);
            dateG_lalin.setText(tanggalSkrg);
        }
        radioGroupTahun.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rbtn_hari:
                        date_r_lalin.setText(tanggalSkrg);
                        dateG_lalin.setText(tanggalSkrg);
                        angka_oneway.setText(dataDailyRekayasa.getOneWay());
                        angka_contraflow.setText(dataDailyRekayasa.getContraFlow());
                        angka_pengalihan.setText(dataDailyRekayasa.getPengalihan());

                        angkaKerjaan.setText(dataGanggaunDaily.getKerjaanJalan());
                        angkaKecelakaan.setText(dataGanggaunDaily.getKecelakaan());
                        angka_g_lalin.setText(dataGanggaunDaily.getGangguanLalin());
                        angkaGenangan.setText(dataGanggaunDaily.getGenangan());
                        angka_pengalihan.setText(dataGanggaunDaily.getPenyekatan());
                        angkaLain.setText(dataGanggaunDaily.getLainLain());
                        break;
                    case R.id.rbtn_tahun:
                        date_r_lalin.setText(tahunOutputAwal);
                        dateG_lalin.setText(tahunOutputAwal);
                        angka_oneway.setText(dataTRekayasa.getOneWay());
                        angka_contraflow.setText(dataTRekayasa.getContraFlow());
                        angka_pengalihan.setText(dataTRekayasa.getPengalihan());
                        angkaKerjaan.setText(dataTGangguan.getKerjaanJalan());
                        angkaKecelakaan.setText(dataTGangguan.getKecelakaan());
                        angka_g_lalin.setText(dataTGangguan.getGangguanLalin());
                        angkaGenangan.setText(dataTGangguan.getGenangan());
                        angka_pengalihan.setText(dataTGangguan.getPenyekatan());
                        angkaLain.setText(dataTGangguan.getLainLain());
                        break;
                }
            }
        });
        getDataCountRekayasa();
        getDataCountGangguan();
        return view;
    }
    private void getDataCountRekayasa(){
        swipeRefreshLayout.setRefreshing(true);
        api.getDataCountRekayasa(dataUser.get(Sessionmanager.nameToken)).enqueue(new Callback<ModelRekayasa>() {
            @Override
            public void onResponse(Call<ModelRekayasa> call, Response<ModelRekayasa> response) {
                if (response.body() != null){
                    ModelRekayasa model = response.body();
                    ModelRekayasa.Data data = model.getData();
                    ModelRekayasa.InnerDataDaily innerDataDaily = data.getDataDaily();
                    dataTRekayasa = data.getData();
                    dataDailyRekayasa = data.getDataDaily();
                    Gson gson = new Gson();
                    Log.d(TAG, "ONEWWAYY: " + gson.toJson(data));
                    if (rbtn_hari.isChecked()){
                        angka_oneway.setText(innerDataDaily.getOneWay());
                        angka_contraflow.setText(innerDataDaily.getContraFlow());
                        angka_pengalihan.setText(innerDataDaily.getPengalihan());
                    }
                }else {
                    Log.d("DashLalin",response.message());
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ModelRekayasa> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);

                Log.d("Error Data Rekayasa", Objects.requireNonNull(t.getMessage()));
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataCountGangguan();
                getDataCountRekayasa();
            }
        });
    }
    private void getDataCountGangguan(){
        swipeRefreshLayout.setRefreshing(true);
        api.getDataCountGangguan(dataUser.get(Sessionmanager.nameToken)).enqueue(new Callback<ModelGangguan>() {
            @Override
            public void onResponse(Call<ModelGangguan> call, Response<ModelGangguan> response) {
                if (response.body() != null){
                    ModelGangguan model = response.body();
                    ModelGangguan.Data data = model.getData();
                    ModelGangguan.DataKerjaanDaily kerjaanDaily = data.getDataDaily();
                    dataTGangguan = data.getData();
                    Gson gson = new Gson();
                    Log.d(TAG, "onResponse: " + gson.toJson(model));
                    dataGanggaunDaily = data.getDataDaily();
                    if (rbtn_hari.isChecked()){
                        angkaKerjaan.setText(kerjaanDaily.getKerjaanJalan());
                        angkaKecelakaan.setText(kerjaanDaily.getKecelakaan());
                        angka_g_lalin.setText(kerjaanDaily.getGangguanLalin());
                        angkaGenangan.setText(kerjaanDaily.getGenangan());
                        angkaPArus.setText(kerjaanDaily.getPenyekatan());
                        angkaLain.setText(kerjaanDaily.getLainLain());
                    }
                }else {
                    Log.d("DashLalin",response.message());
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ModelGangguan> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);

                Log.d("Error Data Rekayasa", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}