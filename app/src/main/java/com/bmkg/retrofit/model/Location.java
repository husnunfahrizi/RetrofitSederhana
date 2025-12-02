package com.bmkg.retrofit.model;

public class Location {

    private String code;
    private String name;

    public Location() {
        // constructor kosong (tetap biarkan ada)
    }

    // ✔ TAMBAHKAN CONSTRUCTOR INI
    public Location(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

