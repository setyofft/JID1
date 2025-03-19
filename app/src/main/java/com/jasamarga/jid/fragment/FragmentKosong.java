package com.jasamarga.jid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasamarga.jid.R;

public class FragmentKosong extends Fragment {
    TextView textView;
    String tit;
    public FragmentKosong(String tit){
        this.tit = tit;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kosong, container, false);
        textView = view.findViewById(R.id.title);
        if(!tit.equals("")){
            textView.setText(tit);
        }
        return view;
    }
}