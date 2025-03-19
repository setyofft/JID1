package com.jasamarga.jid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.jasamarga.jid.R;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.views.Activitiweb;
import com.jasamarga.jid.views.DashboardPeralataan;

import java.util.Objects;

public class FragmentPeralataan extends Fragment {


    private LinearLayout realtimecctv, realtimevms,dashboardLalin,monitoringAlat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_peralataan, container, false);
        inittVar(view);
        return view;
    }

    private void inittVar(View view){
//        antrianGerbangbtn = view.findViewById(R.id.antrianGerbangbtn);
//        lalinPerjambtn = view.findViewById(R.id.lalinPerjambtn);
        monitoringAlat = view.findViewById(R.id.monit_alat);
        dashboardLalin = view.findViewById(R.id.dashboard);
        realtimecctv = view.findViewById(R.id.realtimecctv);
        realtimevms = view.findViewById(R.id.realtimevms);
        clickOn();
    }

    private void clickOn(){
        dashboardLalin.setOnClickListener(v->{
            if(ServiceFunction.getUserRole(requireActivity(),"dash").contains("dsb1")){
                Intent intent = new Intent(getActivity(), DashboardPeralataan.class);
                intent.putExtra("selected",0);
                startActivity(intent);
            }else {
                Toast.makeText(getContext(),requireActivity().getResources().getString(R.string.not_scope),Toast.LENGTH_LONG).show();
            }

        });
        monitoringAlat.setOnClickListener(v -> {
            if(ServiceFunction.getUserRole(requireActivity(),"dash").contains("dsb6")){
//                Intent intent = new Intent(getActivity(), Activitiweb.class);
//                intent.putExtra("hosturl", getResources().getString(R.string.monitoring_alat)+ ServiceFunction.getMathRandomWebview());
//                intent.putExtra("judul_app","Monitoring Alat");
//                startActivity(intent);
                Intent intent = new Intent(getActivity(), DashboardPeralataan.class);
                intent.putExtra("selected",1);
                startActivity(intent);
            }else {
                Toast.makeText(getContext(), requireActivity().getResources().getString(R.string.not_scope),Toast.LENGTH_LONG).show();
            }
        });
        realtimecctv.setOnClickListener(v -> {

//                Intent intent = new Intent(getActivity(), Activitiweb.class);
//                intent.putExtra("hosturl", getResources().getString(R.string.realtimecctv)+ ServiceFunction.getMathRandomWebview());
//                intent.putExtra("judul_app","Realtime CCTV");
//                startActivity(intent);
            Intent intent = new Intent(getActivity(), DashboardPeralataan.class);
            intent.putExtra("selected",2);
            startActivity(intent);

        });
        realtimevms.setOnClickListener(v -> {

//                Intent intent = new Intent(getActivity(), Activitiweb.class);
//                intent.putExtra("hosturl", getResources().getString(R.string.realtime_vms)+ ServiceFunction.getMathRandomWebview());
//                intent.putExtra("judul_app","Realtime DMS");
//                startActivity(intent);
            Intent intent = new Intent(getActivity(), DashboardPeralataan.class);
            intent.putExtra("selected",3);
            startActivity(intent);
        });
//        lalinPerjambtn.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), Activitiweb.class);
//            intent.putExtra("hosturl", getResources().getString(R.string.url_lalin_perjam));
//            startActivity(intent);
//        });
//        realtimeTraffic.setOnClickListener(v->{
//            Intent intent = new Intent(getActivity(), Activitiweb.class);
//            intent.putExtra("hosturl", getResources().getString(R.string.url_real_lalin));
//            startActivity(intent);
//        });
    }
}