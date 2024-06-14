package com.jasamarga.jid.components;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.adapter.ChipAdapter;
import com.jasamarga.jid.models.ModelRegion;
import com.jasamarga.jid.models.ModelRuas;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterBottomFragment extends BottomSheetDialogFragment {

    private Chip switchHarian;
    private Chip switchBulan;
    private TextInputLayout mulaiDariEditText;
    private TextInputLayout sampaiDariEditText;
    private RecyclerView regionalRecyclerView;
    private RecyclerView ruasRecyclerView,sumberData;
    private MaterialButton terapkanButton;
    private ReqInterface reqInterface;
    private Sessionmanager sessionmanager;
    private LinearLayout layout_laporan,layout_bulan,layout_region,layout_ruas,layout_sumber;
    private HashMap<String,String> userDetails;
    private List<ModelRuas.DataRuas> dataRuas;
    private List<ModelRegion.DataRegion> modelRegions;
    private String activityMana;

    public FilterBottomFragment(String activityMana) {
        this.activityMana = activityMana;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_menu, container, false);
        // Inisialisasi view
        switchHarian = rootView.findViewById(R.id.switch_harian);
        switchBulan = rootView.findViewById(R.id.switch_bulan);
        mulaiDariEditText = rootView.findViewById(R.id.textInput_mulai_dri);
        sampaiDariEditText = rootView.findViewById(R.id.textInput_smp_dri);
        regionalRecyclerView = rootView.findViewById(R.id.regional_chip);
        ruasRecyclerView = rootView.findViewById(R.id.ruas_chip);
        terapkanButton = rootView.findViewById(R.id.terapkan);
        layout_laporan = rootView.findViewById(R.id.layout_laporan);
        layout_bulan = rootView.findViewById(R.id.layout_bulan);
        layout_region = rootView.findViewById(R.id.layout_region);
        layout_ruas = rootView.findViewById(R.id.layout_ruas);
        layout_sumber = rootView.findViewById(R.id.layout_sumber);
        if (activityMana.equals("datapem")) {
            layout_sumber.setVisibility(View.GONE);
            layout_bulan.setVisibility(View.VISIBLE);
            layout_laporan.setVisibility(View.VISIBLE);
        }else {
            layout_ruas.setVisibility(View.GONE);
            layout_bulan.setVisibility(View.GONE);
            layout_laporan.setVisibility(View.GONE);
        }
//        layout_sumber.setVisibility(View.GONE);
        dataRuas = new ArrayList<>();
        modelRegions = new ArrayList<>();
        reqInterface = ApiClientNew.getServiceNew();
        sessionmanager = new Sessionmanager(requireContext());
        userDetails = sessionmanager.getUserDetails();
        getRegion();
        getRuas();
        return rootView;
    }
    private void getRegion(){
        reqInterface.getRegionFilter(userDetails.get(Sessionmanager.nameToken)).enqueue(new Callback<ModelRegion>() {
            @Override
            public void onResponse(Call<ModelRegion> call, Response<ModelRegion> response) {
                if (response.body() != null){
                    ModelRegion modelRegion = response.body();
                    ChipAdapter chipAdapter = new ChipAdapter(dataRuas,modelRegion.getData(),requireContext(),"region");
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(),2);
                    regionalRecyclerView.setAdapter(chipAdapter);
                    regionalRecyclerView.setLayoutManager(gridLayoutManager);
                }else {
                    Toast.makeText(requireContext(),"Maaf ada error data " + response.message(),Toast.LENGTH_SHORT).show();;
                    Log.d("Region", "onFailure: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<ModelRegion> call, Throwable t) {
                Toast.makeText(requireContext(),"Maaf ada error network " + t.getMessage(),Toast.LENGTH_SHORT).show();;
                Log.d("Region", "onFailure: " + t.getMessage());
            }
        });
    }
    private void getRuas(){
        reqInterface.getRuasFilter(userDetails.get(Sessionmanager.nameToken)).enqueue(new Callback<ModelRuas>() {
            @Override
            public void onResponse(Call<ModelRuas> call, Response<ModelRuas> response) {
                if (response.body() != null){
                    ModelRuas modelRuas = response.body();
                    ChipAdapter chipAdapter = new ChipAdapter(modelRuas.getData(),modelRegions,requireContext(),"ruas");
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(),3);
                    ruasRecyclerView.setLayoutManager(gridLayoutManager);
                    ruasRecyclerView.setAdapter(chipAdapter);
                }else {
                    Toast.makeText(requireContext(),"Maaf ada error data " + response.message(),Toast.LENGTH_SHORT).show();;
                    Log.d("Ruas", "onFailure: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<ModelRuas> call, Throwable t) {
                Toast.makeText(requireContext(),"Maaf ada error network " + t.getMessage(),Toast.LENGTH_SHORT).show();;
                Log.d("Ruas", "onFailure: " + t.getMessage());
            }
        });
    }
}
