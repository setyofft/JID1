package com.jasamarga.jid.fragment.dashlalin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.ListGateAdapter;
import com.jasamarga.jid.models.DataGate;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FAntrianGerbang extends Fragment {
    private RecyclerView speedMenuju,speedDari,speedJapek;
    private ListGateAdapter listGateAdapter,menujuAdapter,japekAdapter;
    private List<DataGate.GateData> gateDataList;
    private List<DataGate.GateData> gateMenuju;
    private List<DataGate.GateData> gateMBZ;
    private ReqInterface req;
    private Sessionmanager sessionmanager;
    private HashMap<String, String> userSession;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_f_antrian_gerbang, container, false);
        req = ApiClientNew.getServiceNew();
        gateDataList = new ArrayList<>();
        gateMBZ = new ArrayList<>();
        gateMenuju = new ArrayList<>();

        speedDari = view.findViewById(R.id.speedDari);
        speedMenuju = view.findViewById(R.id.speedMenuju);
        speedJapek = view.findViewById(R.id.speedJapek);

        sessionmanager = new Sessionmanager(requireActivity());
        userSession = sessionmanager.getUserDetails();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(requireContext());
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(requireContext());

        speedDari.setLayoutManager(linearLayoutManager);
        speedMenuju.setLayoutManager(linearLayoutManager2);
        speedJapek.setLayoutManager(linearLayoutManager3);

        speedMenuju.setNestedScrollingEnabled(false);
        speedDari.setNestedScrollingEnabled(false);
        speedJapek.setNestedScrollingEnabled(false);

//        speedDari.setAdapter(listGateAdapter);
//        speedMenuju.setAdapter(listGateAdapter);
        getDataDari();
        getDataMenuju();
        return view;
    }
    private void getDataDari(){
        req.getAntrianDari(userSession.get(Sessionmanager.nameToken)).enqueue(new Callback<DataGate>() {
            @Override
            public void onResponse(Call<DataGate> call, Response<DataGate> response) {
                Gson gson = new Gson();
                if (response.body() != null) {
                    for (DataGate.GateData item : response.body().getData()){
                        Log.d("DataJSON", "onResponse: " + gson.toJson(response.body().getData()));
                        if (item.getNamaGerbang().toLowerCase().contains("antrian")){
                            gateMBZ.add(item);
                            Log.d("MBZ", "onResponse: " + gson.toJson(item));
                        }else {
                            Log.d("Other MBZ", "onResponse: " + gson.toJson(item));
                            gateDataList.add(item);
                        }
                    }
                    japekAdapter = new ListGateAdapter(gateMBZ,requireContext());
                    listGateAdapter = new ListGateAdapter(gateDataList, requireContext());
                    speedDari.setAdapter(listGateAdapter);
                    speedJapek.setAdapter(japekAdapter);
                }else {
                    Log.d("Antrian dari", gson.toJson(response.body()));
                }
            }

            @Override
            public void onFailure(Call<DataGate> call, Throwable t) {

                Log.d("Antrian dari", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
    private void getDataMenuju(){
        req.getAntrianGMenuju(userSession.get(Sessionmanager.nameToken)).enqueue(new Callback<DataGate>() {
            @Override
            public void onResponse(Call<DataGate> call, Response<DataGate> response) {
                Gson gson = new Gson();
                if (response.body() != null) {
                    menujuAdapter = new ListGateAdapter(response.body().getData(), requireContext());
                    speedMenuju.setAdapter(menujuAdapter);
                }else {
                    Log.d("Antrian dari", gson.toJson(response.body()));
                }
            }

            @Override
            public void onFailure(Call<DataGate> call, Throwable t) {

                Log.d("Antrian menuju", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}