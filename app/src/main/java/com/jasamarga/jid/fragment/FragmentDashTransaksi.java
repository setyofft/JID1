package com.jasamarga.jid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.jasamarga.jid.R;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.service.WebClient;

public class FragmentDashTransaksi extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout refreshLayout;
    private WebView content_antrian_gerbang;
    WebSettings contentsetting;

    String url_antrian;
    private ProgressBar loading;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentDashTransaksi() {
        // Required empty public constructor
    }

    public static FragmentDashTransaksi newInstance(String param1, String param2) {
        FragmentDashTransaksi fragment = new FragmentDashTransaksi();
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
        View v =inflater.inflate(R.layout.fragment_transaksi2, container, false);
        loading = (ProgressBar) v.findViewById(R.id.loading);
        url_antrian = getResources().getString(R.string.url_dash_transaksi);
        content_antrian_gerbang = v.findViewById(R.id.content_antrian_gerbang);
        if (ServiceFunction.Terkoneksi(getActivity())){
            content_antrian_gerbang.setWebViewClient(new WebClient(loading));
        }else {
            ServiceFunction.pesanNosignal(content_antrian_gerbang,getActivity());
        }
        contentsetting = content_antrian_gerbang.getSettings();
        content_antrian_gerbang.loadUrl(url_antrian);

        contentsetting.setJavaScriptEnabled(true);
        contentsetting.setJavaScriptCanOpenWindowsAutomatically(true);
        contentsetting.setEnableSmoothTransition(true);
        content_antrian_gerbang.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        content_antrian_gerbang.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        content_antrian_gerbang.getSettings().setAppCacheEnabled(true);
        contentsetting.setDomStorageEnabled(true);
        contentsetting.setSaveFormData(true);

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(this::onRefresh);
        return v;
    }

    @Override
    public void onRefresh() {

        content_antrian_gerbang.reload();
        refreshLayout.setRefreshing(false);
    }
}