package com.jasamarga.jid.models.model_notif;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelEvent {

    @SerializedName("akun")
    @Expose
    private String akun;
    @SerializedName("tipe_event")
    @Expose
    private String tipeEvent;
    @SerializedName("ket_jenis_event")
    @Expose
    private String ketJenisEvent;
    @SerializedName("detail_ket_jenis_event")
    @Expose
    private String detailKetJenisEvent;
    @SerializedName("ket_status")
    @Expose
    private String ketStatus;
    @SerializedName("km")
    @Expose
    private String km;
    @SerializedName("jalur")
    @Expose
    private String jalur;
    @SerializedName("nama_ruas")
    @Expose
    private String namaRuas;
    @SerializedName("dampak")
    @Expose
    private String dampak;

    @SerializedName("tanggal")
    @Expose
    private String tanggal;


    public String getDampak() {
        return dampak;
    }

    public void setDampak(String dampak) {
        this.dampak = dampak;
    }

    public String getAkun() {
        return akun;
    }

    public void setAkun(String akun) {
        this.akun = akun;
    }

    public String getTipeEvent() {
        return tipeEvent;
    }

    public void setTipeEvent(String tipeEvent) {
        this.tipeEvent = tipeEvent;
    }

    public String getKetJenisEvent() {
        return ketJenisEvent;
    }

    public void setKetJenisEvent(String ketJenisEvent) {
        this.ketJenisEvent = ketJenisEvent;
    }

    public String getDetailKetJenisEvent() {
        return detailKetJenisEvent;
    }

    public void setDetailKetJenisEvent(String detailKetJenisEvent) {
        this.detailKetJenisEvent = detailKetJenisEvent;
    }

    public String getKetStatus() {
        return ketStatus;
    }

    public void setKetStatus(String ketStatus) {
        this.ketStatus = ketStatus;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getJalur() {
        return jalur;
    }

    public void setJalur(String jalur) {
        this.jalur = jalur;
    }

    public String getNamaRuas() {
        return namaRuas;
    }

    public void setNamaRuas(String namaRuas) {
        this.namaRuas = namaRuas;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
