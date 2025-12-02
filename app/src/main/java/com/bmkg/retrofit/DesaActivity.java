package com.bmkg.retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.bmkg.retrofit.model.*;

import java.util.ArrayList;
import java.util.List;

public class DesaActivity extends AppCompatActivity {

    RecyclerView rvDesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvDesa = findViewById(R.id.rvLokasi);
        rvDesa.setLayoutManager(new LinearLayoutManager(this));

        String kecCode = getIntent().getStringExtra("kec_code");
        String kabCode = getIntent().getStringExtra("kab_code");

        KabupatenModel kabupaten = null;
        KecamatanModel kecamatan = null;

        // Cari kabupaten
        for (KabupatenModel kab : MainActivity.provinsi.kabupaten) {
            if (kab.code.equals(kabCode)) {
                kabupaten = kab;
                break;
            }
        }

        // Cari kecamatan
        for (KecamatanModel kec : kabupaten.kecamatan) {
            if (kec.code.equals(kecCode)) {
                kecamatan = kec;
                break;
            }
        }

        List<Location> desaLocation = convertDesa(kecamatan.desa);

        LocationAdapter adapter = new LocationAdapter(desaLocation, loc -> {
            Intent i = new Intent(this, WeatherActivity.class);
            i.putExtra("kode_wilayah", loc.getCode());
            startActivity(i);
        });

        rvDesa.setAdapter(adapter);
    }

    private List<Location> convertDesa(List<DesaModel> list) {
        List<Location> result = new ArrayList<>();

        for (DesaModel desa : list) {
            result.add(new Location(desa.code, desa.name));
        }

        return result;
    }
}
