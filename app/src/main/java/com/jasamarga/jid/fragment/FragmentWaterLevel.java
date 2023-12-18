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
import com.jasamarga.jid.adapter.AdapterWaterLevel;
import com.jasamarga.jid.adapter.DataPemAdapter;
import com.jasamarga.jid.models.DataPemeliharaanModel;
import com.jasamarga.jid.models.DataWaterLevel;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.LoadingDialog;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentWaterLevel extends Fragment {
    Sessionmanager sessionmanager;
    String token,scope;
    LoadingDialog loadingDialog;
    RecyclerView listData;
    AdapterWaterLevel adapterWaterLevel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_water_level, container, false);
        sessionmanager = new Sessionmanager(requireContext());
        HashMap<String, String> userDetails = sessionmanager.getUserDetails();
        listData = view.findViewById(R.id.list_data);
        token = userDetails.get(Sessionmanager.nameToken);
        scope = userDetails.get(Sessionmanager.set_scope);
        loadingDialog = new LoadingDialog(requireActivity());
        getData();
        return view;
    }
    public void getData(){
        loadingDialog.showLoadingDialog("Loading Data Pemeliharaan. . .");
        Log.d(TAG, "getDataPemeliharaan: " + token + scope);
        ReqInterface newService = ApiClientNew.getServiceNew();
        Call<DataWaterLevel> call = newService.getWaterLevet(token);
        String fullUrl = call.request().url().toString();
        Log.d(TAG, "getDataPemeliharaan URL: " + fullUrl);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        listData.setLayoutManager(linearLayoutManager);
        call.enqueue(new Callback<DataWaterLevel>() {
            @Override
            public void onResponse(Call<DataWaterLevel> call, Response<DataWaterLevel> response) {
                if (response.body() != null){
                    DataWaterLevel data = response.body();
                    boolean result = data.isStatus();

                    if (result){
                        adapterWaterLevel = new AdapterWaterLevel(data.getDataList(),requireContext());
                        listData.setAdapter(adapterWaterLevel);
                    }else {
                        Toast.makeText(requireContext(),response.message(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(),"Maaf ada kesalahan data " +response.message(),Toast.LENGTH_SHORT).show();

                }
                loadingDialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<DataWaterLevel> call, Throwable t) {
                Toast.makeText(getContext(),"Maaf ada kesalahan data " +t.getMessage(),Toast.LENGTH_SHORT).show();
                loadingDialog.hideLoadingDialog();
                Log.d("Error Data Pemeliharaan", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}
