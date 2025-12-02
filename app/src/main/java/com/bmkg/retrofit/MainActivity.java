package com.bmkg.retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.bmkg.retrofit.model.Location;
import com.bmkg.retrofit.model.ProvinsiModel;
import com.bmkg.retrofit.model.WilayahJateng;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvLokasi;
    public static ProvinsiModel provinsi; // simpan provinsi biar bisa diakses halaman lain

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvLokasi = findViewById(R.id.rvLokasi);
        rvLokasi.setLayoutManager(new LinearLayoutManager(this));

        provinsi = loadProvinsi();

        // tampilkan hanya provinsi
        List<Location> provList = new ArrayList<>();
        provList.add(new Location(provinsi.code, provinsi.name));

        LocationAdapter adapter = new LocationAdapter(provList, loc -> {
            // jika provinsi diklik → buka daftar kabupaten
            Intent i = new Intent(MainActivity.this, KabupatenActivity.class);
            startActivity(i);
        });

        rvLokasi.setAdapter(adapter);
    }

    private ProvinsiModel loadProvinsi() {
        try {
            InputStream is = getAssets().open("wilayah_jateng.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");

            WilayahJateng data = new Gson().fromJson(json, WilayahJateng.class);
            return data.provinsi;   // ambil provinsi

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
