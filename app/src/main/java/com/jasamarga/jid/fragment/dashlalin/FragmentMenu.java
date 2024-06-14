package com.jasamarga.jid.fragment.dashlalin;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jasamarga.jid.R;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.components.Appbar;
import com.jasamarga.jid.service.ServiceFunction;
import com.jasamarga.jid.service.WebClient;
import com.jasamarga.jid.views.Activitiweb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentMenu extends Fragment{

    private SwipeRefreshLayout refreshLayout;
    private WebView content_antrian_gerbang;
    WebSettings contentsetting;

    String url_antrian;
    private ProgressBar loading;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_URL = "url";
    private static final String ARG_title = "title";

    // TODO: Rename and change types of parameters
    private String mURL;
    private String title;
    Sessionmanager sessionmanager;

    public FragmentMenu() {

    }
    public static FragmentMenu newInstance(String url, String title) {
        FragmentMenu fragment = new FragmentMenu();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        args.putString(ARG_title, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mURL = getArguments().getString(ARG_URL);
            title = getArguments().getString(ARG_title);
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
        sessionmanager = new Sessionmanager(requireContext());
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        contentsetting = content_antrian_gerbang.getSettings();
        contentsetting.setJavaScriptEnabled(true);

        // Set WebViewClient agar tautan terbuka di dalam WebView
        HashMap<String, String> user = sessionmanager.getUserDetails();
        String token = Objects.requireNonNull(user.get(Sessionmanager.nameToken)).replace("Bearer ","");
        content_antrian_gerbang.setWebChromeClient(new WebChromeClient());
        content_antrian_gerbang.getSettings().setDomStorageEnabled(true);
        loading.setVisibility(View.VISIBLE);
        String css1 = ".grid-rows-* { grid-template-rows: 0px }";
        String js1 = "javascript:var style = document.createElement('style'); style.innerHTML = '" + css1 + "'; document.head.appendChild(style);";
        String css = "#mobile-expand-button { display: none }";
        String js = "javascript:ar style = document.createElement('style'); style.innerHTML = '" + css + "'; document.head.appendChild(style);";


        int delayMillis = 2000; // Contoh: penundaan 5 detik
        refreshLayout.setRefreshing(true);
// Buat kode JavaScript yang akan menambahkan gaya setelah penundaan
        String delayedJS1 = "setTimeout(function() {" + js1 + "}, " + delayMillis + ");";
        String delayedJS2 = "setTimeout(function() {" + js + "}, " + delayMillis + ");";

        content_antrian_gerbang.setVisibility(View.GONE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                content_antrian_gerbang.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);

            }
        }, 5000);

        String jsbutton = "setTimeout(function() {" +
                "var sidebar = document.getElementById('mobile-expand-button');" +
                "if (sidebar) {" +
                "console.log('Sidebar found, hiding it now.');" +
                "sidebar.style.display = 'none';" +
                "} else {" +
                "console.log('Sidebar not found.');" +
                "}" +
                "}, 3000);";
        String jscoba = "setTimeout(function() {" +
                "var sidebar = document.getElementById('sidebar');" +
                "if (sidebar) {" +
                "console.log('Sidebar found, hiding it now.');" +
                "sidebar.style.display = 'none';" +
                "} else {" +
                "console.log('Sidebar not found.');" +
                "}" +
                "}, 3000);";

        String jsLogout = "setTimeout(function() {" +
                "    var appBanners = document.getElementsByClassName('hidden sm:flex sm:gap-2');" +
                "    for (var i = 0; i < appBanners.length; i++) {" +
                "        appBanners[i].style.display = 'none';" +
                "    };" +
                "}, 2000);"; // Penundaan 5 detik
        // Set WebViewClient to handle page loading within WebView
        content_antrian_gerbang.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                content_antrian_gerbang.loadUrl("javascript:localStorage.setItem('token', '" + token + "');");
                loading.setVisibility(View.GONE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                content_antrian_gerbang.evaluateJavascript(jscoba, null);
                content_antrian_gerbang.evaluateJavascript(jsLogout, null);
                content_antrian_gerbang.evaluateJavascript(jsbutton, null);

                // Log the action for debugging purposes
                content_antrian_gerbang.loadUrl("javascript:console.log('Token set in localStorage: ' + localStorage.getItem('token'));");

                loading.setVisibility(View.GONE);

            }
        });
        content_antrian_gerbang.loadUrl(url_antrian);
        Appbar.appBarNoName(requireActivity(),requireActivity().getWindow().getDecorView());
        refreshLayout.setEnabled(false);
        ServiceFunction.addLogActivity(requireActivity(),title,"",title);

        return v;
    }
    @Override
    public void onPause() {
        super.onPause();
        if (content_antrian_gerbang != null) {
            content_antrian_gerbang.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (content_antrian_gerbang != null) {
            content_antrian_gerbang.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (content_antrian_gerbang != null) {
            content_antrian_gerbang.reload();
        }
    }
}