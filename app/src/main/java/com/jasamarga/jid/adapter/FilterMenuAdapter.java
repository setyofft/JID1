package com.jasamarga.jid.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jasamarga.jid.R;

public class FilterMenuAdapter extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_menu  , container, false);
        // Menginisialisasi komponen dari custom_bottom_sheet_layout di sini
        // Contoh:
        // Button button = view.findViewById(R.id.button_id);
        // button.setOnClickListener(v -> {
        //     // Handle klik tombol di sini
        // });
        return view;
    }
}
