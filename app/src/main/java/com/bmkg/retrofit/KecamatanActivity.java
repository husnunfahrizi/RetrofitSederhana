package com.bmkg.retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.bmkg.retrofit.model.*;

import java.util.ArrayList;
import java.util.List;

public class KecamatanActivity extends AppCompatActivity {

    RecyclerView rvKecamatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kecamatan);  // PERBAIKAN

        rvKecamatan = findViewById(R.id.rvKecamatan); // PERBAIKAN
        rvKecamatan.setLayoutManager(new LinearLayoutManager(this));

        String kabCode = getIntent().getStringExtra("kab_code");

        KabupatenModel kabupaten = null;

        // Cari kabupaten di provinsi
        for (KabupatenModel kab : MainActivity.provinsi.kabupaten) {   // PERBAIKAN
            if (kab.code.equals(kabCode)) {
                kabupaten = kab;
                break;
            }
        }

        List<Location> kecLocation = convertKecamatan(kabupaten.kecamatan);

        LocationAdapter adapter = new LocationAdapter(kecLocation, loc -> {
            Intent i = new Intent(KecamatanActivity.this, DesaActivity.class);
            i.putExtra("kec_code", loc.getCode());
            i.putExtra("kab_code", kabCode);
            startActivity(i);
        });

        rvKecamatan.setAdapter(adapter);
    }

    private List<Location> convertKecamatan(List<KecamatanModel> list) {
        List<Location> result = new ArrayList<>();

        for (KecamatanModel kec : list) {
            result.add(new Location(kec.code, kec.name));
        }

        return result;
    }
}
