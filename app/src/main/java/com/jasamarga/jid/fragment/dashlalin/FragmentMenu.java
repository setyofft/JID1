package com.jasamarga.jid.fragment.dashlalin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.progressindicator.CircularProgressIndicator;
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

    private WebView content_antrian_gerbang;
    WebSettings contentsetting;

    String url_antrian;
    private CircularProgressIndicator loading;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_URL = "url";
    private static final String ARG_title = "title";

    // TODO: Rename and change types of parameters
    private String mURL;
    private String title;
    Sessionmanager sessionmanager;
    LinearLayout loadingLayout;
    TextView textLoad,textOffline;
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
        loading =  v.findViewById(R.id.loading);
        url_antrian = mURL;
        content_antrian_gerbang = v.findViewById(R.id.content_antrian_gerbang);
        sessionmanager = new Sessionmanager(requireActivity());
        contentsetting = content_antrian_gerbang.getSettings();
        contentsetting.setJavaScriptEnabled(true);
        loadingLayout = v.findViewById(R.id.loadingLayout);
        textLoad = v.findViewById(R.id.load);
        textOffline = v.findViewById(R.id.textOffline);
        // Set WebViewClient agar tautan terbuka di dalam WebView
        HashMap<String, String> user = sessionmanager.getUserDetails();
        String token = Objects.requireNonNull(user.get(Sessionmanager.nameToken)).replace("Bearer ","");
        content_antrian_gerbang.setWebChromeClient(new WebChromeClient());
        content_antrian_gerbang.evaluateJavascript("javascript:localStorage.setItem('token', '" + token + "');", null);
        content_antrian_gerbang.evaluateJavascript("javascript:localStorage.setItem('isWebview', 'Webview');", null);
//        content_antrian_gerbang.addJavascriptInterface(new MyJavaScriptInterface(requireActivity()), "Android");
        isInternet();
        content_antrian_gerbang.getSettings().setDomStorageEnabled(true);
        content_antrian_gerbang.setVisibility(View.GONE);
//        89ií.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
        String defaultUserAgent = contentsetting.getUserAgentString();
        String newUserAgent = defaultUserAgent + " Jid-Mobile";
        contentsetting.setUserAgentString(newUserAgent);
        String css1 = ".grid-rows-* { grid-template-rows: 0px }";
        String js1 = "javascript:var style = document.createElement('style'); style.innerHTML = '" + css1 + "'; document.head.appendChild(style);";
        String css = "#mobile-expand-button { display: none }";
        String js = "javascript:ar style = document.createElement('style'); style.innerHTML = '" + css + "'; document.head.appendChild(style);";

        int delayMillis = 2000; // Contoh: penundaan 5 detik
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                content_antrian_gerbang.setVisibility(View.VISIBLE);
//                loading.setVisibility(View.GONE);
//                loadingLayout.setVisibility(View.GONE);
//
//            }
//        }, 3000);
        content_antrian_gerbang.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Logika untuk deteksi URL halaman login
                if (url.contains("login")) {
                    sessionmanager.logout();
                    return true; // Mencegah WebView memuat URL ini
                }
                return false; // Izinkan WebView memuat URL lain
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isInternet();
                content_antrian_gerbang.loadUrl("javascript:localStorage.setItem('token', '" + token + "');");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isInternet();
//                content_antrian_gerbang.evaluateJavascript(jscoba, null);
//                content_antrian_gerbang.evaluateJavascript(javascriptCode, null);
//                content_antrian_gerbang.evaluateJavascript(jsLogout, null);
//                content_antrian_gerbang.evaluateJavascript(jsbutton, null);
//                content_antrian_gerbang.evaluateJavascript(lalinperjam, null);
                // JavaScript untuk menambahkan style
                String css1 = ".grid-rows-* { grid-template-rows: 0px }";
                String js1 = "var style = document.createElement('style'); style.innerHTML = '" + css1 + "'; document.head.appendChild(style);";

                String css = "#mobile-expand-button { display: none }";
                String js = "var style = document.createElement('style'); style.innerHTML = '" + css + "'; document.head.appendChild(style);";

                String css2 = ".flex-grow, flex.z-[10500] { display: none }";
                String js2 = "var style = document.createElement('style'); style.innerHTML = '" + css2 + "'; document.head.appendChild(style);";

                String side = "#sidebar{ display: none }";
                String jsside = "var style = document.createElement('style'); style.innerHTML = '" + side + "'; document.head.appendChild(style);";

                String csslogout = "#user_logout_btn{ display: none }";
                String jslogout = "var style = document.createElement('style'); style.innerHTML = '" + csslogout + "'; document.head.appendChild(style);";
                String cssTrafficRecommendation = "#traffic-recommendation-button { display: none !important; }";
                String jsTrafficRecommendation = "var style = document.createElement('style'); style.innerHTML = '" + cssTrafficRecommendation.replace("'", "\\'") + "'; document.head.appendChild(style);";

                // Eksekusi JavaScript
                content_antrian_gerbang.evaluateJavascript(js1, null);
                content_antrian_gerbang.evaluateJavascript(js, null);
                content_antrian_gerbang.evaluateJavascript(js2, null);
                content_antrian_gerbang.evaluateJavascript(jsside, null);
                content_antrian_gerbang.evaluateJavascript(jslogout, null);
                content_antrian_gerbang.evaluateJavascript(jsTrafficRecommendation, null);
                // Log the action for debugging purposes
                content_antrian_gerbang.loadUrl("javascript:console.log('Token set in localStorage: ' + localStorage.getItem('token'));");
                content_antrian_gerbang.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
//                loading.setVisibility(View.GONE);

            }

        });
        if (ServiceFunction.Terkoneksi(requireActivity())){
            content_antrian_gerbang.loadUrl(url_antrian);
        }else {
            Toast.makeText(requireContext(),"Maaf tidak dapat mengakses dikarena kan tidak ada koneksi",Toast.LENGTH_SHORT).show();
        }
        Appbar.appBarNoName(requireActivity(),requireActivity().getWindow().getDecorView());

        return v;
    }
    public void isInternet(){
        if (getActivity() != null && ServiceFunction.Terkoneksi(getActivity())) {
            content_antrian_gerbang.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
//            loading.setVisibility(View.GONE);

        }else {
            content_antrian_gerbang.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            textLoad.setVisibility(View.GONE);
            textOffline.setVisibility(View.VISIBLE);
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