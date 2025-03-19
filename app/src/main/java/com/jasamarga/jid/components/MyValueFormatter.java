package com.jasamarga.jid.components;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class MyValueFormatter extends ValueFormatter {
    private DecimalFormat format;

    public MyValueFormatter() {
        format = new DecimalFormat("###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value) {
        return format.format(value) + " %";
    }
}
