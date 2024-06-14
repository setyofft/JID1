package com.jasamarga.jid.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jasamarga.jid.R;
import com.jasamarga.jid.fragment.FragmentDataGangguan;
import com.jasamarga.jid.fragment.FragmentDataPem;
import com.jasamarga.jid.fragment.FragmentWaterLevel;
import com.jasamarga.jid.fragment.dashlalin.FRealtimeTraffic;

public class ActivityFragment extends AppCompatActivity {
    //Activity ini untuk nampilin fragment jadi ada logic yang dia nerima getExtra String lalu di logic fragment mana yang mau di tampilkan

    private ImageView back;
    private TextView judul,badge;
    private CardView button_bell,button_exit;
    private Fragment fragment;
    String title;
    private ConstraintLayout constraintLayout;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Intent intent = getIntent();
        title = intent.getStringExtra("judul_app");
        constraintLayout = findViewById(R.id.layout);
        back = findViewById(R.id.back);
        back.setColorFilter(Color.BLACK);
        badge = findViewById(R.id.cart_badge);
        button_bell = findViewById(R.id.button_bell);
        button_exit = findViewById(R.id.button_exit);
        back.setOnClickListener(v->{
            finish();
        });
        judul = findViewById(R.id.title_app);
        judul.setText(title);
        judul.setTextColor(Color.BLACK);
        constraintLayout.setBackgroundColor(Color.WHITE);
        if (title.equals("Data Pemeliharaan")){
            fragment = new FragmentDataPem();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment, fragment).commit();
        }else if (title.equals("Water Level Sensor")){
            fragment = new FragmentWaterLevel();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment, fragment).commit();
        }else if (title.equals("Realtime Traffic")){
            fragment = new FRealtimeTraffic();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment, fragment).commit();
        }else if (title.equals("Gangguan Lalin")){
            fragment = new FragmentDataGangguan();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment, fragment).commit();
        }


    }
}
