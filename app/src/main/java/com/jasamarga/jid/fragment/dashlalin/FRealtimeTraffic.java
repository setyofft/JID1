package com.jasamarga.jid.fragment.dashlalin;

import static androidx.fragment.app.DialogFragment.STYLE_NORMAL;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.RealtimeTrafficAdapter;
import com.jasamarga.jid.components.FilterBottomFragment;
import com.jasamarga.jid.models.RTJalurAB;
import com.jasamarga.jid.models.RealtimeTrModel;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FRealtimeTraffic extends Fragment {
    private RecyclerView listRealtimeTraffic;
    private ReqInterface reqInterface;
    private Sessionmanager sessionmanager;
    private HashMap<String,String> userDetails;
    private MaterialButton filter;
    private List<RealtimeTrModel.DataItem> dataRT,dataRTB;
    private RealtimeTrafficAdapter realtimeTrafficAdapter;
    List<RTJalurAB> listGabung;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_f_realtime_traffic, container, false);
        filter = view.findViewById(R.id.filterChartKegiatan);
        listRealtimeTraffic = view.findViewById(R.id.listRT);
        reqInterface = ApiClientNew.getServiceNew();
        sessionmanager = new Sessionmanager(requireActivity());
        userDetails = sessionmanager.getUserDetails();
        dataRT = new ArrayList<>();
        dataRTB = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        listRealtimeTraffic.setLayoutManager(linearLayoutManager);
        listGabung = new ArrayList<>();
        FilterBottomFragment bottomSheetFragment = new FilterBottomFragment("realtime");
        filter.setOnClickListener(view1 -> {
            bottomSheetFragment.setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);
            bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
        });


        getData();
        return view;
    }
    private void getData(){
        reqInterface.getDataRT("1","A",userDetails.get(Sessionmanager.nameToken)).enqueue(new Callback<RealtimeTrModel>() {
            @Override
            public void onResponse(Call<RealtimeTrModel> call, Response<RealtimeTrModel> response) {
                if(response.body() != null){
                    RealtimeTrModel dataBody = response.body();
                    dataRT = response.body().getData();
                    realtimeTrafficAdapter = new RealtimeTrafficAdapter(dataRT, requireContext());
                    listRealtimeTraffic.setAdapter(realtimeTrafficAdapter);
//                    getDataB();
                }else {
                    Gson gson = new Gson();
                    Toast.makeText(getContext(),"Maaf ada kesalahan data" +response.message() +" ",Toast.LENGTH_SHORT).show();
                    Log.d("Error Data Pemeliharaan", gson.toJson(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<RealtimeTrModel> call, Throwable t) {
                Toast.makeText(getContext(),"Maaf ada kesalahan network" +t.getMessage() +" ",Toast.LENGTH_SHORT).show();
//                loadingDialog.hideLoadingDialog();
                Log.d("Error Data Pemeliharaan", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
    private void getDataB(){
        reqInterface.getDataRT("1","B",userDetails.get(Sessionmanager.nameToken)).enqueue(new Callback<RealtimeTrModel>() {
            @Override
            public void onResponse(Call<RealtimeTrModel> call, Response<RealtimeTrModel> response) {
                if(response.body() != null){
                    RealtimeTrModel dataBody = response.body();
                    dataRTB = response.body().getData();
//                    for (RealtimeTrModel.DataItem item : dataRTB) {
//                        RTJalurAB.DataJalurB rtB = new RTJalurAB.DataJalurB(item);
//                        for (RTJalurAB rt : listGabung) {
//                            if (rt.getDataA() != null) {
//                                Gson gson = new Gson();
//                                RTJalurAB rtJalurAB = new RTJalurAB();
//                                rtJalurAB.setDataA(rt.getDataA());
//                                rtJalurAB.setDataB(rtB);
////                                rt.setDataB(new RTJalurAB.DataJalur(item));
//
//                                listGabung.add(rtJalurAB);
//                                Log.d("DataB", "MasukDataB: " + gson.toJson(listGabung));
//                                break;
//                            }
//                            break;
//                        }
//                    }

                    // Update the adapter after merging the data
                    realtimeTrafficAdapter = new RealtimeTrafficAdapter(dataRTB, requireContext());
                    listRealtimeTraffic.setAdapter(realtimeTrafficAdapter);
                    cekDataList();

                }else {
                    Gson gson = new Gson();
                    Toast.makeText(getContext(),"Maaf ada kesalahan data" +response.message() +" ",Toast.LENGTH_SHORT).show();
                    Log.d("Error Data Pemeliharaan", gson.toJson(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<RealtimeTrModel> call, Throwable t) {
                Toast.makeText(getContext(),"Maaf ada kesalahan network" +t.getMessage() +" ",Toast.LENGTH_SHORT).show();
//                loadingDialog.hideLoadingDialog();
                Log.d("Error Data Pemeliharaan", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
    private void cekDataList(){
        for (RTJalurAB item : listGabung){
            Log.d("ASWWWW", "cekDataListA: " + item.getDataA().getNamaJalur());
            if (item.getDataB() != null){
                Log.d("ASWWWW", "cekDataListB: " + item.getDataB().getNamaJalur());
            }else {
                Log.d("ASWWWW", "cekDataListB: ITEM B KOSONG");

            }
        }
    }
}