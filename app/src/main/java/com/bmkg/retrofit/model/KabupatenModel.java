package com.bmkg.retrofit.model;

import java.util.List;

public class KabupatenModel {
    public String name;
    public String code;
    public List<KecamatanModel> kecamatan;

    public String getName() { return name; }
    public String getCode() { return code; }
}
