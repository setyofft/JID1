package com.jasamarga.jid.fragment.dashlalin;

import static androidx.fragment.app.DialogFragment.STYLE_NORMAL;
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

import com.google.android.material.button.MaterialButton;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.DataPemAdapter;
import com.jasamarga.jid.components.FilterBottomFragment;
import com.jasamarga.jid.models.DataPemeliharaanModel;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentGangguanLalin extends Fragment {
    Sessionmanager sessionmanager;
    String token,scope;
    private LoadingDialog loadingDialog;
    DataPemAdapter dataPemAdapter;
    ArrayList<DataPemeliharaanModel> dataPemeliharaanModels;
    RecyclerView listData;
    MaterialButton filterChartKegiatan;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_gangguan_lalin, container, false);
        sessionmanager = new Sessionmanager(requireContext());
        HashMap<String, String> userDetails = sessionmanager.getUserDetails();
        token = userDetails.get(Sessionmanager.nameToken);
        scope = userDetails.get(Sessionmanager.set_scope);
        filterChartKegiatan = view.findViewById(R.id.filterChartKegiatan);
        loadingDialog = new LoadingDialog(requireActivity());
        listData = view.findViewById(R.id.data_list);
        FilterBottomFragment bottomSheetFragment = new FilterBottomFragment("datapem");
        filterChartKegiatan.setOnClickListener(view1 -> {
            bottomSheetFragment.setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);
            bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
        });
        getData();
        return view;
    }
    public void getData(){
        loadingDialog.showLoadingDialog("Loading Data Pemeliharaan. . .");
        Log.d(TAG, "getDataPemeliharaan: " + token + scope);
        ReqInterface newService = ApiClientNew.getServiceNew();
        Call<DataPemeliharaanModel> call = newService.getPemeliharaan("2",null,null,null,null,"2023-01","2023-12","bulan",token);
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
                    Log.d(TAG, "Error onResponse: " + response);
                }
                loadingDialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<DataPemeliharaanModel> call, Throwable t) {
                Toast.makeText(getContext(),"Maaf ada kesalahan network" +t.getMessage() +" ",Toast.LENGTH_SHORT).show();
                loadingDialog.hideLoadingDialog();
                Log.d("Error Data Pemeliharaan", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}
