package com.jasamarga.jid.fragment.dashlalin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jasamarga.jid.R;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.service.WebClient;

public class FragmentMenu extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

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
    private String mURL;
    private String title;

    public FragmentMenu(String mURL,String title) {
        this.mURL = mURL;
        this.title = title;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mURL = getArguments().getString(ARG_PARAM1);
            title = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_menu_dashlalin, container, false);
        loading = (ProgressBar) v.findViewById(R.id.loading);
        url_antrian = mURL;
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
        content_antrian_gerbang.getSettings().setBuiltInZoomControls(true);
        content_antrian_gerbang.getSettings().setSupportZoom(true);
        contentsetting.setDomStorageEnabled(true);
        contentsetting.setSaveFormData(true);
        content_antrian_gerbang.clearFormData();
        content_antrian_gerbang.clearCache(true);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(this);
        ServiceFunction.addLogActivity(getActivity(),title,"",title);

        return v;
    }
    @Override
    public void onRefresh() {

        content_antrian_gerbang.reload();
        refreshLayout.setRefreshing(false);
    }
}