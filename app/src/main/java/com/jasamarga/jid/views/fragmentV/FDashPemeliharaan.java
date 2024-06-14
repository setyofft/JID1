package com.jasamarga.jid.views.fragmentV;

import static com.jasamarga.jid.components.PopupDetailLalin.TAG;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jasamarga.jid.R;
import com.jasamarga.jid.RoundedBarChart;
import com.jasamarga.jid.Sessionmanager;
import com.jasamarga.jid.components.CustomXAxisRenderer;
import com.jasamarga.jid.components.MyValueFormatter;
import com.jasamarga.jid.components.RoundedBarChartRenderer;
import com.jasamarga.jid.models.DataJalurModel;
import com.jasamarga.jid.models.KegiatanModel;
import com.jasamarga.jid.models.PemeliharaanData;
import com.jasamarga.jid.models.PemeliharaanGroupChart;
import com.jasamarga.jid.router.ApiClient;
import com.jasamarga.jid.router.ApiClientNew;
import com.jasamarga.jid.router.ReqInterface;
import com.jasamarga.jid.service.LoadingDialog;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FDashPemeliharaan extends Fragment {
    BarChart kegiatanChart;
    BarChart barChart;
    PieChart pieChart;
    Sessionmanager sessionmanager;
    String scope,token;
    LoadingDialog loadingDialog;
    float barSpace = 0.06f;
    float groupSpace = -0.08f;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_dashboard_pemeliharaan,container,false);

        barChart = v.findViewById(R.id.chart);
        kegiatanChart = v.findViewById(R.id.chart2);
        pieChart = v.findViewById(R.id.chart3);

        sessionmanager = new Sessionmanager(requireContext());
        HashMap<String,String> dataUser = sessionmanager.getUserDetails();
        scope = dataUser.get(Sessionmanager.set_scope);
        token = dataUser.get(Sessionmanager.nameToken);
        loadingDialog = new LoadingDialog(requireActivity());

        getDataPemeliharaan();
        return v;
    }

    private void getDataPemeliharaan(){
        loadingDialog.showLoadingDialog("Loading Data Pemeliharaan. . .");
        Log.d(TAG, "getDataPemeliharaan: " + token + scope);
        ReqInterface newService = ApiClientNew.getServiceNew();
        Call<PemeliharaanData> call = newService.getDataPemeliharaan(scope,"bulan","2023-01","2023-12",token);
        String fullUrl = call.request().url().toString();
        Log.d(TAG, "getDataPemeliharaan URL: " + fullUrl);
        call.enqueue(new Callback<PemeliharaanData>() {
            @Override
            public void onResponse(Call<PemeliharaanData> call, Response<PemeliharaanData> response) {
                if (response.body() != null) {
                    PemeliharaanData pemeliharaanData = response.body();
                    ArrayList<BarEntry> selesaiEntries = new ArrayList<>();
                    ArrayList<BarEntry> prosesEntries = new ArrayList<>();
                    ArrayList<BarEntry> rencanaEntries = new ArrayList<>();
                    ArrayList<String> months = new ArrayList<>();
                    Log.d(TAG, "onResponse: " + pemeliharaanData.getData().getTotal());

                    for (PemeliharaanData.ResultEntry item : pemeliharaanData.getData().getResult()){
                        PemeliharaanData.Id config = item.getId();
                        String month = config.getBulan();
                        selesaiEntries.add(new BarEntry(1, item.getSelesai()));
                        prosesEntries.add(new BarEntry(2, item.getProses()));
                        rencanaEntries.add(new BarEntry(3, item.getRencana()));
                        if (month.length() >= 3) {
                            month = month.substring(0, 3);
                        }
                        months.add(month);

                        Log.d(TAG, "onResponse: Pemeliharaan" + config.getBulan());

                    }
                    BarDataSet selesaiDataSet = new BarDataSet(selesaiEntries, "Selesai");
                    selesaiDataSet.setColor(requireActivity().getResources().getColor(R.color.legend_selesai));

                    BarDataSet prosesDataSet = new BarDataSet(prosesEntries, "Proses");
                    prosesDataSet.setColor(requireActivity().getResources().getColor(R.color.legend_proses));

                    BarDataSet rencanaDataSet = new BarDataSet(rencanaEntries, "Rencana");
                    rencanaDataSet.setColor(requireActivity().getResources().getColor(R.color.legend_rencana));

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(selesaiDataSet);
                    dataSets.add(prosesDataSet);
                    dataSets.add(rencanaDataSet);

                    BarData barData2 = new BarData(selesaiDataSet,prosesDataSet,rencanaDataSet);
                    barData2.setBarWidth(0.3f);

                    barChart.setData(barData2);

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
                    xAxis.setCenterAxisLabels(true);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1);
                    xAxis.setGranularityEnabled(true);

                    barChart.getXAxis().setAxisMinimum(0);
                    barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace,barSpace) * 7);
                    barChart.getAxisLeft().setAxisMinimum(0);
                    barChart.groupBars(0,groupSpace,barSpace);

                    barChart.getAxisLeft().setDrawGridLines(false);
//                    barChart.getXAxis().setDrawGridLines(false);
//                    barChart.getAxisRight().setDrawGridLines(false);
                    barChart.getDescription().setEnabled(false);
                    barChart.getLegend().setEnabled(false);

                    barChart.invalidate(); // Refresh the chart
                }else {
                    Toast.makeText(getContext(),"Maaf Data Kosong",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: PEMELIHARAAN" + response.body() + response.message());
                }
                loadingDialog.hideLoadingDialog();
                getDataKegiatan();

            }
            @Override
            public void onFailure(Call<PemeliharaanData> call, Throwable t) {
                Toast.makeText(getContext(),"Maaf ada kesalahan data " +t.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("Error Data Pemeliharaan", Objects.requireNonNull(t.getMessage()));
                loadingDialog.hideLoadingDialog();
            }
        });
    }
    private void getDataLajur(){
        loadingDialog.showLoadingDialog("Loading Data Jalur. . .");
        Log.d(TAG, "getDataPemeliharaan: " + token + scope);
        ReqInterface newService = ApiClientNew.getServiceNew();
        Call<DataJalurModel> call = newService.getDataPJalur("5","bulan","2022-01","2022-08",token);
        String fullUrl = call.request().url().toString();
        Log.d(TAG, "getDataPemeliharaan URL: " + fullUrl);
        call.enqueue(new Callback<DataJalurModel>() {
            @Override
            public void onResponse(Call<DataJalurModel> call, Response<DataJalurModel> response) {
                if (response.body() != null) {
                    DataJalurModel kegiatanModel = response.body();
                    ArrayList<PieEntry> entries = new ArrayList<>();
                    ArrayList<PieEntry> entriesA = new ArrayList<>();
                    ArrayList<PieEntry> entriesB = new ArrayList<>();

                    int totalJalurA = 0;
                    int totalJalurB = 0;

                    for (DataJalurModel.Data item : kegiatanModel.getData()) {
                        if (item.getId().getJalur().contains("A")) {
                            totalJalurA += item.getJalurA();
                        } else if (item.getId().getJalur().contains("B")) {
                            totalJalurB += item.getJalurB();
                        }
//                        entries.add(new PieEntry(item.getJalurA(), "Jalur A"));
//                        entries.add(new PieEntry(item.getJalurB(), "Jalur B"));

                        Log.d(TAG, "onResponse: Kegiatan" + item.getJalurA());
                    }
                    entriesA.add(new PieEntry(totalJalurA,"Jalur A"));
                    entriesB.add(new PieEntry(totalJalurB,"Jalur B"));
                    ArrayList<PieEntry> combinedEntries = new ArrayList<>();
                    combinedEntries.addAll(entriesA);
                    combinedEntries.addAll(entriesB);

                    PieDataSet dataSet = new PieDataSet(combinedEntries, "");

                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                    dataSet.setValueTextSize(12f);
                    PieData pieData = new PieData(dataSet);
                    pieData.setValueFormatter(new MyValueFormatter());
                    pieChart.setUsePercentValues(true);
                    pieChart.setData(pieData);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setCenterText(" ");
                    pieChart.animateY(1000);
                    pieChart.invalidate(); // Refresh the chart
                }else {
                    Toast.makeText(getContext(),"Maaf Data Kosong",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: PEMELIHARAAN" + response.body() + response.message());
                }
                loadingDialog.hideLoadingDialog();

            }
            @Override
            public void onFailure(Call<DataJalurModel> call, Throwable t) {
                Toast.makeText(getContext(),"Maaf ada kesalahan data " +t.getMessage(),Toast.LENGTH_SHORT).show();

                Log.d("Error Data Pemeliharaan", Objects.requireNonNull(t.getMessage()));
                loadingDialog.hideLoadingDialog();
            }
        });
    }
    private void getDataKegiatan(){
        loadingDialog.showLoadingDialog("Loading Data Kegiatan . . .");

        Log.d(TAG, "getDataKegiatan: " + token + scope);
        ReqInterface newService = ApiClientNew.getServiceNew();
        Call<KegiatanModel> call = newService.getDataPKegiatan("5","bulan","2022-01","2022-05",token);
        String fullUrl = call.request().url().toString();
        Log.d(TAG, "getDataKegiatan URL: " + fullUrl);
        call.enqueue(new Callback<KegiatanModel>() {
            @Override
            public void onResponse(Call<KegiatanModel> call, Response<KegiatanModel> response) {
                if (response.body() != null) {
                    KegiatanModel kegiatanModel = response.body();
                    ArrayList<BarEntry> selesaiEntries = new ArrayList<>();
                    ArrayList<BarEntry> prosesEntries = new ArrayList<>();
                    ArrayList<BarEntry> rencanaEntries = new ArrayList<>();
                    ArrayList<String> months = new ArrayList<>();

                    for (KegiatanModel.DataItem item : kegiatanModel.getData()){
//                        KegiatanModel.Id config = item.getId();
                        if (item.getJenisKegiatan() != null){
                            String month = item.getJenisKegiatan();
                            if (month.equals("Scrapping Filling Overlay (SFO)")) {
                                month = "SFO";
                            } else if (month.equals("Patching")) {
                                month = "Patching";
                            } else if (month.equals("Pekerjaan Sarana Keselamatan Jalan")) {
                                month = "Sarana Jalan";
                            } else if (month.equals("Pekerjaan Jembatan")) {
                                month = "Jembatan";
                            } else if (month.equals("Pekerjaan Marka")) {
                                month = "Marka";
                            } else if (month.equals("Pelebaran Jalan")) {
                                month = "Jalan";
                            } else if (month.equals("Pekerjaan Expansion Joint")) {
                                month = "Joint";
                            } else if (month.equals("Pekerjaan lainnya")) {
                                month = "Lainnya";
                            }else if (month.equals("Pilih Jenis Kegiatan")) {
                                month = "Pilih JK";
                            }
                            months.add(month);

                        }
                        selesaiEntries.add(new BarEntry(1, item.getSelesai()));
                        prosesEntries.add(new BarEntry(2, item.getProses()));
                        rencanaEntries.add(new BarEntry(3, item.getRencana()));

                        Log.d(TAG, "onResponse: Kegiatan" + item.getJenisKegiatan());

                    }

                    BarDataSet selesaiDataSet = new BarDataSet(selesaiEntries, "Selesai");
                    selesaiDataSet.setColor(requireActivity().getResources().getColor(R.color.legend_selesai));

                    BarDataSet prosesDataSet = new BarDataSet(prosesEntries, "Proses");
                    prosesDataSet.setColor(requireActivity().getResources().getColor(R.color.legend_proses));

//                    BarDataSet rencanaDataSet = new BarDataSet(rencanaEntries, "Rencana");
//                    rencanaDataSet.setColor(requireActivity().getResources().getColor(R.color.legend_rencana));

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(selesaiDataSet);
                    dataSets.add(prosesDataSet);
//                    dataSets.add(rencanaDataSet);


                    BarData barData2 = new BarData(selesaiDataSet,prosesDataSet);
                    barData2.setBarWidth(0.3f);

                    CombinedData combinedData = new CombinedData();
                    combinedData.setData(barData2);

//                    ArrayList<Drawable> drawables = new ArrayList<>();
//                    for(String item : months){
//                        Log.d(TAG, "onResponseIcon: " + item + months.size());
//                        if (item.equals("Scrapping Filling Overlay (SFO)")) {
//                            // Tambahkan drawable yang sesuai untuk item ini ke dalam arraylist
//                            drawables.add(getResources().getDrawable(R.drawable.slo));
//                        } else if (item.equals("Rekonstruksi")) {
//                            // Tambahkan drawable yang sesuai untuk item ini ke dalam arraylist
//                            drawables.add(getResources().getDrawable(R.drawable.rekonstruksi));
//                        } else if (item.equals("Patching")) {
//                            // Tambahkan drawable yang sesuai untuk item ini ke dalam arraylist
//                            drawables.add(getResources().getDrawable(R.drawable.patching));
//                        } else if (item.equals("Pekerjaan Sarana Keselamatan Jalan")) {
//                            // Tambahkan drawable yang sesuai untuk item ini ke dalam arraylist
//                            drawables.add(getResources().getDrawable(R.drawable.marka));
//                        } else if (item.equals("Pekerjaan Jembatan")) {
//                            // Tambahkan drawable yang sesuai untuk item ini ke dalam arraylist
//                            drawables.add(getResources().getDrawable(R.drawable.bridge));
//                        } else if (item.equals("Pekerjaan Marka")) {
//                            // Tambahkan drawable yang sesuai untuk item ini ke dalam arraylist
//                            drawables.add(getResources().getDrawable(R.drawable.marking));
//                        } else if (item.equals("Pelebaran Jalan")) {
//                            // Tambahkan drawable yang sesuai untuk item ini ke dalam arraylist
//                            drawables.add(getResources().getDrawable(R.drawable.lebar));
//                        } else if (item.equals("Pekerjaan Expansion Joint")) {
//                            // Tambahkan drawable yang sesuai untuk item ini ke dalam arraylist
//                            drawables.add(getResources().getDrawable(R.drawable.joint));
//                        } else if (item.equals("Pekerjaan lainnya")) {
//                            // Tambahkan drawable yang sesuai untuk item ini ke dalam arraylist
//                            drawables.add(getResources().getDrawable(R.drawable.icon_park_solid_road_sign_both));
//                        }
//                        // Lanjutkan untuk item-item lainnya
//                    }


//                    CustomXAxisRenderer iconsKegiatan = new CustomXAxisRenderer(kegiatanChart.getViewPortHandler(), kegiatanChart.getXAxis(), kegiatanChart.getTransformer(YAxis.AxisDependency.LEFT),drawables);
//                    kegiatanChart.setXAxisRenderer(iconsKegiatan);

                    XAxis xAxis = kegiatanChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
                    xAxis.setCenterAxisLabels(true);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1);
                    xAxis.setGranularityEnabled(true);


                    kegiatanChart.setData(barData2);
                    groupSpace = 0.28f;
                    kegiatanChart.getXAxis().setAxisMinimum(0);
                    kegiatanChart.getXAxis().setAxisMaximum(0 + kegiatanChart.getBarData().getGroupWidth(groupSpace,barSpace) * 7);
                    kegiatanChart.getAxisLeft().setAxisMinimum(0);
                    kegiatanChart.groupBars(0,groupSpace,barSpace);
//                    kegiatanChart.getXAxis().setLabelRotationAngle(-50f);
//                    kegiatanChart.getXAxis().setYOffset(10f);
                    kegiatanChart.getAxisLeft().setDrawGridLines(false);
//                    kegiatanChart.getXAxis().setDrawGridLines(false);
//                    kegiatanChart.getAxisRight().setDrawGridLines(false);
                    kegiatanChart.getDescription().setEnabled(false);

//                    kegiatanChart.getLegend().setXOffset(50f);
                    kegiatanChart.getLegend().setEnabled(false);
                    kegiatanChart.invalidate(); // Refresh the chart

                }else {
                    Toast.makeText(getContext(),"Maaf Data Kosong",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: Kegiatan" + response.body() + response.message());
                }

                loadingDialog.hideLoadingDialog();
                getDataLajur();


            }

            @Override
            public void onFailure(Call<KegiatanModel> call, Throwable t) {
                Toast.makeText(getContext(),"Maaf ada kesalahan data " +t.getMessage(),Toast.LENGTH_SHORT).show();

                Log.d("Error Data Kegiatan", Objects.requireNonNull(t.getMessage()));
                loadingDialog.hideLoadingDialog();
            }
        });
    }
}
