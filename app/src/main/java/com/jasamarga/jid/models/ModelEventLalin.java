package com.jasamarga.jid.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelEventLalin {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    @SerializedName("total")
    private int total;

    @SerializedName("data")
    private List<DataEvent> data;

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public int getTotal() {
        return total;
    }

    public List<DataEvent> getData() {
        return data;
    }

    public static class DataEvent {

        @SerializedName("id_ruas")
        @Expose
        private int idRuas;

        @SerializedName("nama_ruas")
        @Expose
        private String namaRuas;

        @SerializedName("km")
        @Expose
        private String km;

        @SerializedName("jalur")
        @Expose
        private String jalur;

        @SerializedName("lajur")
        @Expose
        private String lajur;

        @SerializedName("lat")
        @Expose
        private double lat;

        @SerializedName("lon")
        @Expose
        private double lon;

        @SerializedName("range_km_pekerjaan")
        @Expose
        private String rangeKmPekerjaan;

        @SerializedName("waktu_awal")
        @Expose
        private String waktuAwal;

        @SerializedName("waktu_akhir")
        @Expose
        private String waktuAkhir;

        @SerializedName("jenis_kegiatan")
        @Expose
        private int jenisKegiatan;

        @SerializedName("ket_jenis_kegiatan")
        @Expose
        private String ketJenisKegiatan;

        @SerializedName("keterangan_detail")
        @Expose
        private String keteranganDetail;

        @SerializedName("id_status")
        @Expose
        private int idStatus;

        @SerializedName("ket_status")
        @Expose
        private String ketStatus;

        @SerializedName("dampak")
        @Expose
        private String dampak;

        @SerializedName("show_peta_sd")
        @Expose
        private String showPetaSd;

        @SerializedName("show_hide")
        @Expose
        private int showHide;

        @SerializedName("tgl_entri")
        @Expose
        private String tglEntri;

        @SerializedName("id_group_segment")
        @Expose
        private String idGroupSegment;

        @SerializedName("created_by")
        @Expose
        private String createdBy;

        @SerializedName("session")
        @Expose
        private String session;

        @SerializedName("push_notification")
        @Expose
        private String pushNotification;

        @SerializedName("status_travoy_notif")
        @Expose
        private String statusTravoyNotif;

        @SerializedName("idx")
        @Expose
        private String idx;

        @SerializedName("id_region")
        @Expose
        private int idRegion;

        // Add getters for each field

        public int getIdRuas() {
            return idRuas;
        }

        public void setIdRuas(int idRuas) {
            this.idRuas = idRuas;
        }

        public String getNamaRuas() {
            return namaRuas;
        }

        public void setNamaRuas(String namaRuas) {
            this.namaRuas = namaRuas;
        }

        public String getDampak() {
            return dampak;
        }

        public void setDampak(String dampak) {
            this.dampak = dampak;
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

        public String getLajur() {
            return lajur;
        }

        public void setLajur(String lajur) {
            this.lajur = lajur;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public String getRangeKmPekerjaan() {
            return rangeKmPekerjaan;
        }

        public void setRangeKmPekerjaan(String rangeKmPekerjaan) {
            this.rangeKmPekerjaan = rangeKmPekerjaan;
        }

        public String getWaktuAwal() {
            return waktuAwal;
        }

        public void setWaktuAwal(String waktuAwal) {
            this.waktuAwal = waktuAwal;
        }

        public String getWaktuAkhir() {
            return waktuAkhir;
        }

        public void setWaktuAkhir(String waktuAkhir) {
            this.waktuAkhir = waktuAkhir;
        }

        public int getJenisKegiatan() {
            return jenisKegiatan;
        }

        public void setJenisKegiatan(int jenisKegiatan) {
            this.jenisKegiatan = jenisKegiatan;
        }

        public String getKetJenisKegiatan() {
            return ketJenisKegiatan;
        }

        public void setKetJenisKegiatan(String ketJenisKegiatan) {
            this.ketJenisKegiatan = ketJenisKegiatan;
        }

        public String getKeteranganDetail() {
            return keteranganDetail;
        }

        public void setKeteranganDetail(String keteranganDetail) {
            this.keteranganDetail = keteranganDetail;
        }

        public int getIdStatus() {
            return idStatus;
        }

        public void setIdStatus(int idStatus) {
            this.idStatus = idStatus;
        }

        public String getKetStatus() {
            return ketStatus;
        }

        public void setKetStatus(String ketStatus) {
            this.ketStatus = ketStatus;
        }

        public String getShowPetaSd() {
            return showPetaSd;
        }

        public void setShowPetaSd(String showPetaSd) {
            this.showPetaSd = showPetaSd;
        }

        public int getShowHide() {
            return showHide;
        }

        public void setShowHide(int showHide) {
            this.showHide = showHide;
        }

        public String getTglEntri() {
            return tglEntri;
        }

        public void setTglEntri(String tglEntri) {
            this.tglEntri = tglEntri;
        }

        public String getIdGroupSegment() {
            return idGroupSegment;
        }

        public void setIdGroupSegment(String idGroupSegment) {
            this.idGroupSegment = idGroupSegment;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getSession() {
            return session;
        }

        public void setSession(String session) {
            this.session = session;
        }

        public String getPushNotification() {
            return pushNotification;
        }

        public void setPushNotification(String pushNotification) {
            this.pushNotification = pushNotification;
        }

        public String getStatusTravoyNotif() {
            return statusTravoyNotif;
        }

        public void setStatusTravoyNotif(String statusTravoyNotif) {
            this.statusTravoyNotif = statusTravoyNotif;
        }

        public String getIdx() {
            return idx;
        }

        public void setIdx(String idx) {
            this.idx = idx;
        }

        public int getIdRegion() {
            return idRegion;
        }

        public void setIdRegion(int idRegion) {
            this.idRegion = idRegion;
        }
    }
}

