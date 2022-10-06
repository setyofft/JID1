package com.pim.jid.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pim.jid.R;
import com.pim.jid.views.Activitiweb;
import com.pim.jid.views.DashboardLalin;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLalin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLalin extends Fragment {


    private LinearLayout antrianGerbangbtn, lalinPerjambtn,dashboardLalin,realtimeTraffic,ganglalin,kinlalin,traffic;
    int views =R.layout.fragment_kosong ;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentLalin() {
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
    public static FragmentLalin newInstance(String param1, String param2) {
        FragmentLalin fragment = new FragmentLalin();
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
        View view = inflater.inflate(R.layout.fragment_lalin, container, false);
        inittVar(view);
        return view;
    }

    private void inittVar(View view){
        antrianGerbangbtn = view.findViewById(R.id.antrianGerbangbtn);
        lalinPerjambtn = view.findViewById(R.id.lalinPerjambtn);
        realtimeTraffic = view.findViewById(R.id.realtime);
        dashboardLalin = view.findViewById(R.id.dashboard);

        clickOn();
    }

    private void clickOn(){
        dashboardLalin.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), DashboardLalin.class);
            startActivity(intent);

        });
        antrianGerbangbtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Activitiweb.class);
            intent.putExtra("hosturl", getResources().getString(R.string.url_antrian_gerbang));
            intent.putExtra("judul_app","Antrian Gerbang");
            startActivity(intent);
        });

        lalinPerjambtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Activitiweb.class);
            intent.putExtra("hosturl", getResources().getString(R.string.url_lalin_perjam));
            intent.putExtra("judul_app","Lalin Perjam");

            startActivity(intent);
        });
        realtimeTraffic.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), Activitiweb.class);
            intent.putExtra("hosturl", getResources().getString(R.string.url_real_lalin));
            intent.putExtra("judul_app","Realtime Traffic");
            startActivity(intent);
        });
    }
}