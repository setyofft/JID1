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
import com.google.gson.Gson;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.DataGangguanAdapter;
import com.jasamarga.jid.adapter.DataPemAdapter;
import com.jasamarga.jid.components.FilterBottomFragment;
import com.jasamarga.jid.models.DataPemeliharaanModel;
import com.jasamarga.jid.models.ModelGangguanLalin;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDataGangguan extends Fragment {
    Sessionmanager sessionmanager;
    String token,scope;
    private LoadingDialog loadingDialog;
    DataGangguanAdapter dataPemAdapter;
    ArrayList<ModelGangguanLalin.GangguanData> dataPemeliharaanModels;
    RecyclerView listData;
    MaterialButton filterChartKegiatan;
    AutoCompleteTextView search;
    private ArrayList<ModelGangguanLalin.GangguanData> filteredData;

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
                Log.d(TAG, "onTextChanged: " + charSequence.toString() + dataPemeliharaanModels.size());
                if(!filteredData.isEmpty()){
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
        Gson gson = new Gson();
        Log.d(TAG, "search: " + text + gson.toJson(filteredData));
        ArrayList<ModelGangguanLalin.GangguanData> filteredList = new ArrayList<>();
        for (ModelGangguanLalin.GangguanData item : filteredData) {
            if (item.getNamaRuas().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        dataPemAdapter.filterList(filteredList);
    }
    public void getData(){
        loadingDialog.showLoadingDialog("Loading Data Gangguan lalin. . .");
        Log.d(TAG, "getDataPemeliharaan: " + token + scope);
        ReqInterface newService = ApiClientNew.getServiceNew();
        Call<ModelGangguanLalin> call = newService.getGangguanLalin(scope,null,null,null,null,"2023-01","2023-12","bulan",token);
        String fullUrl = call.request().url().toString();
        Log.d(TAG, "getDataPemeliharaan URL: " + fullUrl);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        listData.setLayoutManager(linearLayoutManager);
        call.enqueue(new Callback<ModelGangguanLalin>() {
            @Override
            public void onResponse(Call<ModelGangguanLalin> call, Response<ModelGangguanLalin> response) {
                if (response.body() != null){
                    ModelGangguanLalin data = response.body();
                    String message = data.getMessage();
                    boolean result = data.isStatus();
                    Gson gson = new Gson();
                    Log.d(TAG, "onResponseData: " + gson.toJson(data));
                    if (result){
                        dataPemAdapter = new DataGangguanAdapter(new ArrayList<>(), requireContext());

                         filteredData = new ArrayList<>();

                        for (ModelGangguanLalin.GangguanData item : data.getData()) {
                            if (!item.getKetStatus().toLowerCase().contains("selesai")) {
                                filteredData.add(item);
                            }
                        }
                        dataPemAdapter.setData(filteredData);
                        listData.setAdapter(dataPemAdapter);
                    }else {
                        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.d(TAG, "Error onResponse: " + response);
                }
                loadingDialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(Call<ModelGangguanLalin> call, Throwable t) {
                Toast.makeText(getContext(),"Maaf ada kesalahan network" +t.getMessage() +" ",Toast.LENGTH_SHORT).show();
                loadingDialog.hideLoadingDialog();
                Log.d("Error Data Pemeliharaan", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}
