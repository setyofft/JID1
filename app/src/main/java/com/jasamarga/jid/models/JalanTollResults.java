package com.jasamarga.jid.models;

import com.google.gson.annotations.SerializedName;

public class JalanTollResults {
    private Integer id_jalan;
    private Integer no_urut;
    private Float lat;
    private Float lon;

    @SerializedName("data")
    public Integer getId_jalan() {
        return id_jalan;
    }

    public Integer getNo_urut() {
        return no_urut;
    }

    public Float getLat() {
        return lat;
    }

    public Float getLon() {
        return lon;
    }

    public JalanTollResults(Integer id_jalan, Integer no_urut, Float lat, Float lon) {
        this.id_jalan = id_jalan;
        this.no_urut = no_urut;
        this.lat = lat;
        lon = lon;
    }
}
