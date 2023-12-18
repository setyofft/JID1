package com.jasamarga.jid.models;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataPemeliharaanModel {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    @SerializedName("data")
    private ArrayList<PemeliharaanData> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ArrayList<PemeliharaanData> getData() {
        return data;
    }

    public void setData(ArrayList<PemeliharaanData> data) {
        this.data = data;
    }

    public static class PemeliharaanData {
        @SerializedName("id_ruas")
        private int idRuas;

        @SerializedName("nama_ruas")
        private String namaRuas;

        @SerializedName("km")
        private String km;

        @SerializedName("jalur")
        private String jalur;

        @SerializedName("lajur")
        private String lajur;

        @SerializedName("lat")
        private double latitude;

        @SerializedName("lon")
        private double longitude;

        @SerializedName("range_km_pekerjaan")
        private String rangeKmPekerjaan;

        @SerializedName("waktu_awal")
        private String waktuAwal;

        @SerializedName("waktu_akhir")
        private String waktuAkhir;

        @SerializedName("jenis_kegiatan")
        private int jenisKegiatan;

        @SerializedName("ket_jenis_kegiatan")
        private String keteranganJenisKegiatan;

        @SerializedName("keterangan_detail")
        private String keteranganDetail;

        @SerializedName("id_status")
        private int idStatus;

        @SerializedName("ket_status")
        private String keteranganStatus;

        @SerializedName("show_peta_sd")
        private String showPetaSd;

        @SerializedName("show_hide")
        private int showHide;

        @SerializedName("tgl_entri")
        private String tanggalEntri;

        @SerializedName("id_group_segment")
        private String idGroupSegment;

        @SerializedName("created_by")
        private String createdBy;

        @SerializedName("session")
        private String session;

        @SerializedName("push_notification")
        private String pushNotification;

        @SerializedName("status_travoy_notif")
        private String statusTravoyNotif;

        @SerializedName("idx")
        private String idx;

        // Tambahkan konstruktor, getter, dan setter sesuai kebutuhan Anda.

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

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
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

        public String getKeteranganJenisKegiatan() {
            return keteranganJenisKegiatan;
        }

        public void setKeteranganJenisKegiatan(String keteranganJenisKegiatan) {
            this.keteranganJenisKegiatan = keteranganJenisKegiatan;
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

        public String getKeteranganStatus() {
            return keteranganStatus;
        }

        public void setKeteranganStatus(String keteranganStatus) {
            this.keteranganStatus = keteranganStatus;
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

        public String getTanggalEntri() {
            return tanggalEntri;
        }

        public void setTanggalEntri(String tanggalEntri) {
            this.tanggalEntri = tanggalEntri;
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
    }
}

