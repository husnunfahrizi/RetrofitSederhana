package com.bmkg.retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bmkg.retrofit.api.ApiClient;
import com.bmkg.retrofit.api.ApiService;
import com.bmkg.retrofit.model.CuacaItem;
import com.bmkg.retrofit.model.DailyForecast;
import com.bmkg.retrofit.model.DataItem;
import com.bmkg.retrofit.model.ResponseData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {

    TextView tvLokasi, tvSuhu, tvCuaca;
    RecyclerView rvJam;
    RecyclerView rvDaily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        tvLokasi = findViewById(R.id.tvLokasi);
        tvSuhu = findViewById(R.id.tvSuhu);
        tvCuaca = findViewById(R.id.tvCuaca);

        rvJam = findViewById(R.id.rvJam);
        rvJam.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvDaily = findViewById(R.id.rvDaily);
        rvDaily.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        String kodeAdm4 = getIntent().getStringExtra("kode_wilayah");
        loadWeather(kodeAdm4);
    }

    private void loadWeather(String adm4Code) {

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getWeatherByAdm4(adm4Code).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("API", "Response error");
                    return;
                }

                DataItem item = response.body().data.get(0);

                // Set lokasi
                tvLokasi.setText(item.lokasi.desa + ", " + item.lokasi.kecamatan);

                // Ambil cuaca jam pertama
                CuacaItem first = item.cuaca.get(0).get(0);
                tvSuhu.setText(first.t + "°");
                tvCuaca.setText(first.weather_desc);

                // Buat list semua cuaca
                List<CuacaItem> jamList = new ArrayList<>();

                for (List<CuacaItem> inner : item.cuaca) {
                    jamList.addAll(inner);
                }

                // ------------------------------------------------------
                // 🔥 SORT JAM AGAR URUT (HH:mm) — PALING AMAN 🔥
                // ------------------------------------------------------
                try {
                    Collections.sort(jamList, (a, b) -> {
                        try {
                            String rawA = a.local_datetime;
                            String rawB = b.local_datetime;

                            // Validasi format
                            if (rawA == null || rawA.length() < 16) return 0;
                            if (rawB == null || rawB.length() < 16) return 0;

                            // Ambil jam:menit => "HH:mm"
                            String jamA = rawA.substring(11, 13);
                            String menitA = rawA.substring(14, 16);

                            String jamB = rawB.substring(11, 13);
                            String menitB = rawB.substring(14, 16);

                            int totalA = Integer.parseInt(jamA) * 60 + Integer.parseInt(menitA);
                            int totalB = Integer.parseInt(jamB) * 60 + Integer.parseInt(menitB);

                            return Integer.compare(totalA, totalB);

                        } catch (Exception ex) {
                            Log.e("SORT JAM", "Error parsing jam: " + ex.getMessage());
                            return 0;
                        }
                    });
                } catch (Exception e) {
                    Log.e("SORT", "Gagal sort jam: " + e.getMessage());
                }


                // Set adapter jam
                rvJam.setAdapter(new WeatherHourAdapter(jamList, WeatherActivity.this));

                // ------------------------------------------------------
                // 🔥 PRAKIRAAN 5 HARI 🔥
                // ------------------------------------------------------
                List<DailyForecast> dailyList = new ArrayList<>();

                int maxHari = Math.min(item.cuaca.size(), 5); // ambil maksimal 5 hari

                for (int d = 0; d < maxHari; d++) {
                    List<CuacaItem> hariList = item.cuaca.get(d);
                    if (hariList == null || hariList.isEmpty()) continue;

                    int maxTemp = Integer.MIN_VALUE;
                    int minTemp = Integer.MAX_VALUE;
                    double totalTp = 0;

                    for (CuacaItem c : hariList) {
                        if (c.t > maxTemp) maxTemp = c.t;
                        if (c.t < minTemp) minTemp = c.t;
                        totalTp += c.tp;
                    }

                    double avgTp = totalTp / hariList.size();
                    int rainChance = (int) Math.min(100, Math.round(avgTp * 20));

                    CuacaItem rep = hariList.get(hariList.size() / 2);

                    String localDate = rep.local_datetime.substring(0, 10);
                    String dayLabel = localDate.substring(8, 10) + "/" + localDate.substring(5, 7);

                    DailyForecast df = new DailyForecast(
                            dayLabel,
                            maxTemp,
                            minTemp,
                            rep.weather_desc,
                            rep.image,
                            rainChance
                    );

                    dailyList.add(df);
                }

                rvDaily.setAdapter(new DailyForecastAdapter(dailyList, WeatherActivity.this));
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.e("API ERROR", t.getMessage());
            }
        });

    }
}
