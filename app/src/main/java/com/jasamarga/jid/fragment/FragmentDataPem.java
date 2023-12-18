package com.jasamarga.jid.fragment;

import static com.jasamarga.jid.components.PopupDetailLalin.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.DataPemAdapter;
import com.jasamarga.jid.models.DataPemeliharaanModel;
import com.jasamarga.jid.models.PemeliharaanData;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDataPem extends Fragment {
    Sessionmanager sessionmanager;
    String token,scope;
    private LoadingDialog loadingDialog;
    DataPemAdapter dataPemAdapter;
    ArrayList<DataPemeliharaanModel> dataPemeliharaanModels;
    RecyclerView listData;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_data_pemilharaan, container, false);
        sessionmanager = new Sessionmanager(requireContext());
        HashMap<String, String> userDetails = sessionmanager.getUserDetails();
        token = userDetails.get(Sessionmanager.nameToken);
        scope = userDetails.get(Sessionmanager.set_scope);
        loadingDialog = new LoadingDialog(requireActivity());
        listData = view.findViewById(R.id.data_list);
        getData();
        return view;
    }
    public void getData(){
        loadingDialog.showLoadingDialog("Loading Data Pemeliharaan. . .");
        Log.d(TAG, "getDataPemeliharaan: " + token + scope);
        ReqInterface newService = ApiClientNew.getServiceNew();
        Call<DataPemeliharaanModel> call = newService.getPemeliharaan("5",null,null,null,null,"2022-01","2022-08",token);
        String fullUrl = call.request().url().toString();
        Log.d(TAG, "getDataPemeliharaan URL: " + fullUrl);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        listData.setLayoutManager(linearLayoutManager);
        call.enqueue(new Callback<DataPemeliharaanModel>() {
            @Override
            public void onResponse(Call<DataPemeliharaanModel> call, Response<DataPemeliharaanModel> response) {
                if (response.body() != null){
                    DataPemeliharaanModel data = response.body();
                    String message = data.getMessage();
                    boolean result = data.isStatus();
                    if (result){
                        dataPemAdapter = new DataPemAdapter(data.getData(),requireContext());
                        listData.setAdapter(dataPemAdapter);
                        for (DataPemeliharaanModel.PemeliharaanData item : data.getData()){
                            Log.d(TAG, "onResponsePemeliharaanData: " + item.getNamaRuas());
                        }
                    }else {
                        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(),"Maaf ada kesalahan data " +response.message(),Toast.LENGTH_SHORT).show();

                }
                loadingDialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<DataPemeliharaanModel> call, Throwable t) {
                Toast.makeText(getContext(),"Maaf ada kesalahan data " +t.getMessage(),Toast.LENGTH_SHORT).show();
                loadingDialog.hideLoadingDialog();
                Log.d("Error Data Pemeliharaan", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}
