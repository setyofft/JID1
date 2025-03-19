package com.jasamarga.jid.fragment.dashperalataan;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.jasamarga.jid.R;

import java.util.ArrayList;

public class FDashboardPeralatan extends Fragment {

    private LineChart chart1, chart2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_f_dashboard_peralatan,container,false);
        chart1 = view.findViewById(R.id.chart);
        chart2 = view.findViewById(R.id.chart2);
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 4));
        entries.add(new Entry(1, 8));
        entries.add(new Entry(2, 6));
        entries.add(new Entry(3, 2));
        entries.add(new Entry(4, 7));

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(requireActivity().getResources().getColor(R.color.line_color_pwo));
        dataSet.setLineWidth(4f);
        dataSet.setValueTextSize(0f);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);

        chart1.getAxisLeft().setDrawGridLines(false);
        chart1.getDescription().setEnabled(false);
        chart1.getLegend().setEnabled(false);
        chart1.setData(lineData);
        chart1.invalidate();

        chart2.getAxisLeft().setDrawGridLines(false);
        chart2.getDescription().setEnabled(false);
        chart2.getLegend().setEnabled(false);
        chart2.setData(lineData);
        chart2.invalidate();
        return view;
    }
}