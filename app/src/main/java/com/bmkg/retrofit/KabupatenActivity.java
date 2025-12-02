package com.bmkg.retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.bmkg.retrofit.model.*;
import java.util.ArrayList;
import java.util.List;

public class KabupatenActivity extends AppCompatActivity {

    RecyclerView rvKab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kabupaten);

        rvKab = findViewById(R.id.rvKabupaten);
        rvKab.setLayoutManager(new LinearLayoutManager(this));

        List<KabupatenModel> kabListOriginal = MainActivity.provinsi.kabupaten;

        List<Location> kabList = new ArrayList<>();
        for (KabupatenModel kab : kabListOriginal) {
            kabList.add(new Location(kab.code, kab.name));
        }

        LocationAdapter adapter = new LocationAdapter(kabList, loc -> {
            Intent i = new Intent(KabupatenActivity.this, KecamatanActivity.class);
            i.putExtra("kab_code", loc.getCode());
            startActivity(i);
        });

        rvKab.setAdapter(adapter);
    }
}
