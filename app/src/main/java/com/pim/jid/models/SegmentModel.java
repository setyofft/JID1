package com.pim.jid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SegmentModel {

    @SerializedName("nama_segment")
    @Expose
    private String namaSegment;
    @SerializedName("id_segment")
    @Expose
    private String idSegment;
    private String idRUas;

    public String getNamaSegment() {
        return namaSegment;
    }

    public void setNamaSegment(String namaSegment) {
        this.namaSegment = namaSegment;
    }

    public String getIdSegment() {
        return idSegment;
    }

    public void setIdSegment(String idSegment) {
        this.idSegment = idSegment;
    }


    public String getIdRUas() {
        return idRUas;
    }

    public void setIdRUas(String idRUas) {
        this.idRUas = idRUas;
    }

}
