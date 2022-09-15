package com.pim.jid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CcctvSegmentModel {

    @SerializedName("nama_segment")
    @Expose
    private String namaSegment;
    @SerializedName("cabang")
    @Expose
    private String cabang;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("km")
    @Expose
    private String km;
    @SerializedName("key_id")
    @Expose
    private String keyId;
    @SerializedName("id_segment")
    @Expose
    private String idSegment;


    @SerializedName("status")
    @Expose
    private String status;

    public String getNamaSegment() {
        return namaSegment;
    }

    public void setNamaSegment(String namaSegment) {
        this.namaSegment = namaSegment;
    }

    public String getCabang() {
        return cabang;
    }

    public void setCabang(String cabang) {
        this.cabang = cabang;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getIdSegment() {
        return idSegment;
    }

    public void setIdSegment(String idSegment) {
        this.idSegment = idSegment;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
