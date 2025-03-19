package com.jasamarga.jid.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ModelGangguanLalin {

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("total")
    @Expose
    private int total;

    @SerializedName("data")
    @Expose
    private List<GangguanData> data;

    // Constructors, getters, and setters

    // Inner class representing the "data" array

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<GangguanData> getData() {
        return data;
    }

    public void setData(List<GangguanData> data) {
        this.data = data;
    }

    public static class GangguanData {

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

        @SerializedName("tipe_gangguan")
        @Expose
        private int tipeGangguan;

        @SerializedName("ket_tipe_gangguan")
        @Expose
        private String ketTipeGangguan;

        @SerializedName("waktu_kejadian")
        @Expose
        private String waktuKejadian;

        @SerializedName("detail_kejadian")
        @Expose
        private String detailKejadian;

        @SerializedName("id_status")
        @Expose
        private int idStatus;

        @SerializedName("ket_status")
        @Expose
        private String ketStatus;

        @SerializedName("show_peta_sd")
        @Expose
        private String showPetaSd;

        @SerializedName("dampak")
        @Expose
        private String dampak;

        @SerializedName("show_hide")
        @Expose
        private int showHide;

        @SerializedName("tgl_entri")
        @Expose
        private String tglEntri;

        @SerializedName("waktu_selesai")
        @Expose
        private String waktuSelesai;

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
        private boolean pushNotification;

        @SerializedName("status_travoy_notif")
        @Expose
        private int statusTravoyNotif;

        @SerializedName("status_dms_notif")
        @Expose
        private boolean statusDmsNotif;

        @SerializedName("idx")
        @Expose
        private int idx;

        @SerializedName("id_region")
        @Expose
        private int idRegion;

        // Constructors, getters, and setters

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

        public int getTipeGangguan() {
            return tipeGangguan;
        }

        public void setTipeGangguan(int tipeGangguan) {
            this.tipeGangguan = tipeGangguan;
        }

        public String getKetTipeGangguan() {
            return ketTipeGangguan;
        }

        public void setKetTipeGangguan(String ketTipeGangguan) {
            this.ketTipeGangguan = ketTipeGangguan;
        }

        public String getWaktuKejadian() {
            return waktuKejadian;
        }

        public void setWaktuKejadian(String waktuKejadian) {
            this.waktuKejadian = waktuKejadian;
        }

        public String getDetailKejadian() {
            return detailKejadian;
        }

        public void setDetailKejadian(String detailKejadian) {
            this.detailKejadian = detailKejadian;
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

        public String getDampak() {
            return dampak;
        }

        public void setDampak(String dampak) {
            this.dampak = dampak;
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

        public String getWaktuSelesai() {
            return waktuSelesai;
        }

        public void setWaktuSelesai(String waktuSelesai) {
            this.waktuSelesai = waktuSelesai;
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

        public boolean isPushNotification() {
            return pushNotification;
        }

        public void setPushNotification(boolean pushNotification) {
            this.pushNotification = pushNotification;
        }

        public int getStatusTravoyNotif() {
            return statusTravoyNotif;
        }

        public void setStatusTravoyNotif(int statusTravoyNotif) {
            this.statusTravoyNotif = statusTravoyNotif;
        }

        public boolean isStatusDmsNotif() {
            return statusDmsNotif;
        }

        public void setStatusDmsNotif(boolean statusDmsNotif) {
            this.statusDmsNotif = statusDmsNotif;
        }

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
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

