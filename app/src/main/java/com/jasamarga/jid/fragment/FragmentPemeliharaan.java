package com.jasamarga.jid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.jasamarga.jid.R;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.views.Activitiweb;
import com.jasamarga.jid.views.ActivityFragment;
import com.jasamarga.jid.views.DashboardPemeliharaan;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPemeliharaan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPemeliharaan extends Fragment {


    private LinearLayout data,level,dashboardLalin,realtimeTraffic;
    int views =R.layout.fragment_kosong ;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentPemeliharaan() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentLalin.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPemeliharaan newInstance(String param1, String param2) {
        FragmentPemeliharaan fragment = new FragmentPemeliharaan();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pemeliharaan, container, false);
        inittVar(view);
        return view;
    }

    private void inittVar(View view){
//        antrianGerbangbtn = view.findViewById(R.id.antrianGerbangbtn);
        data = view.findViewById(R.id.data);
        level = view.findViewById(R.id.level);
        dashboardLalin = view.findViewById(R.id.dashboard);
        clickOn();
    }

    private void clickOn(){
        dashboardLalin.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), DashboardPemeliharaan.class);
            startActivity(intent);

        });
        level.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), ActivityFragment.class);
            intent.putExtra("hosturl", getResources().getString(R.string.url_water_level)+ ServiceFunction.getMathRandomWebview());
            intent.putExtra("judul_app","Water Level Sensor");
            startActivity(intent);

        });
        data.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), ActivityFragment.class);
//            intent.putExtra("hosturl", getResources().getString(R.string.url_data_pemeliharaan)+ ServiceFunction.getMathRandomWebview());
            intent.putExtra("judul_app","Data Pemeliharaan");
            startActivity(intent);

        });
    }
}