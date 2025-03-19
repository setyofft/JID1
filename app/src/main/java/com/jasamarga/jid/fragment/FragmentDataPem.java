package com.jasamarga.jid.fragment;

import static androidx.fragment.app.DialogFragment.STYLE_NORMAL;
import static com.jasamarga.jid.components.PopupDetailLalin.TAG;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
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
import com.jasamarga.jid.models.RuasModel;
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
    ArrayList<DataPemeliharaanModel.PemeliharaanData> dataPemeliharaanModels;
    RecyclerView listData;
    MaterialButton filterChartKegiatan;
    AutoCompleteTextView search;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_data_pemilharaan, container, false);
        sessionmanager = new Sessionmanager(requireActivity());
        HashMap<String, String> userDetails = sessionmanager.getUserDetails();
        token = userDetails.get(Sessionmanager.nameToken);
        scope = userDetails.get(Sessionmanager.set_scope);
        filterChartKegiatan = view.findViewById(R.id.filterChartKegiatan);
        search = view.findViewById(R.id.search);
        loadingDialog = new LoadingDialog(requireActivity());
        dataPemeliharaanModels = new ArrayList<>();
        listData = view.findViewById(R.id.data_list);
        FilterBottomFragment bottomSheetFragment = new FilterBottomFragment("datapem");
        filterChartKegiatan.setOnClickListener(view1 -> {
            bottomSheetFragment.setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);
            bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
        });
        filterChartKegiatan.setVisibility(View.GONE);
        getData();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!dataPemeliharaanModels.isEmpty()){
                    search(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }
    private void search(String text){
        Log.d(TAG, "search: CARI NAMA" +"AWWA"+ text);

        ArrayList<DataPemeliharaanModel.PemeliharaanData> filteredList = new ArrayList<>();
        for (DataPemeliharaanModel.PemeliharaanData item : dataPemeliharaanModels) {
            if (item.getNamaRuas().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        dataPemAdapter.filterList(filteredList);
    }
    public void getData(){
        loadingDialog.showLoadingDialog("Loading Data Pemeliharaan. . .");
        Log.d(TAG, "getDataPemeliharaan: " + token + scope);
        ReqInterface newService = ApiClientNew.getServiceNew();
        Call<DataPemeliharaanModel> call = newService.getPemeliharaan(scope,null,null,null,null,"2024-07","2024-07","hari",token);
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
                        dataPemeliharaanModels.addAll(data.getData());
                        listData.setAdapter(dataPemAdapter);
                        for (DataPemeliharaanModel.PemeliharaanData item : data.getData()){
                            Log.d(TAG, "onResponsePemeliharaanData: " + item.getKeteranganStatus());
                        }
                    }else {
                        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show();
                    }
                }else {
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
