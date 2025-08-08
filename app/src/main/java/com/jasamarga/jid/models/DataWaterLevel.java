package com.jasamarga.jid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataWaterLevel {
    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("data")
    @Expose
    private List<Data> dataList;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Data> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }

    public class Data {
        @SerializedName("power_current")
        @Expose
        private String powerCurrent;

        @SerializedName("power_voltage")
        @Expose
        private String powerVoltage;

        @SerializedName("raindrop")
        @Expose
        private String raindrop;

        @SerializedName("_id")
        @Expose
        private String id;

        @SerializedName("id")
        @Expose
        private int numericId;

        @SerializedName("nama_lokasi")
        @Expose
        private String namaLokasi;

        @SerializedName("ruas_id")
        @Expose
        private int ruasId;

        @SerializedName("nama_ruas")
        @Expose
        private String namaRuas;

        @SerializedName("level_sensor")
        @Expose
        private int levelSensor;

        @SerializedName("level")
        @Expose
        private String level;

        @SerializedName("hujan")
        @Expose
        private String hujan;

        @SerializedName("pompa")
        @Expose
        private String pompa;

        @SerializedName("status_pompa")
        @Expose
        private boolean statusPompa;

        @SerializedName("latitude")
        @Expose
        private double latitude;

        @SerializedName("longitude")
        @Expose
        private double longitude;

        @SerializedName("waktu_update")
        @Expose
        private String waktuUpdate;

        @SerializedName("status")
        @Expose
        private int status;

        @SerializedName("rec")
        @Expose
        private int rec;

        @SerializedName("vendor")
        @Expose
        private String vendor;

        @SerializedName("kode_alat_vendor")
        @Expose
        private String kodeAlatVendor;

        @SerializedName("url_cctv")
        @Expose
        private String urlCctv;

        // --- NEW FIELDS ADDED BELOW ---

        @SerializedName("remote_pompa")
        @Expose
        private String remotePompa; // Added based on JSON

        @SerializedName("camera_id")
        @Expose
        private String cameraId; // Added based on JSON

        @SerializedName("key_id")
        @Expose
        private String keyId; // Added based on JSON

        // --- NEW GETTERS AND SETTERS ---

        public String getPowerCurrent() {
            return powerCurrent;
        }

        public void setPowerCurrent(String powerCurrent) {
            this.powerCurrent = powerCurrent;
        }

        public String getPowerVoltage() {
            return powerVoltage;
        }

        public void setPowerVoltage(String powerVoltage) {
            this.powerVoltage = powerVoltage;
        }

        public String getRaindrop() {
            return raindrop;
        }

        public void setRaindrop(String raindrop) {
            this.raindrop = raindrop;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getNumericId() {
            return numericId;
        }

        public void setNumericId(int numericId) {
            this.numericId = numericId;
        }

        public String getNamaLokasi() {
            return namaLokasi;
        }

        public void setNamaLokasi(String namaLokasi) {
            this.namaLokasi = namaLokasi;
        }

        public int getRuasId() {
            return ruasId;
        }

        public void setRuasId(int ruasId) {
            this.ruasId = ruasId;
        }

        public String getNamaRuas() {
            return namaRuas;
        }

        public void setNamaRuas(String namaRuas) {
            this.namaRuas = namaRuas;
        }

        public int getLevelSensor() {
            return levelSensor;
        }

        public void setLevelSensor(int levelSensor) {
            this.levelSensor = levelSensor;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getHujan() {
            return hujan;
        }

        public void setHujan(String hujan) {
            this.hujan = hujan;
        }

        public String getPompa() {
            return pompa;
        }

        public void setPompa(String pompa) {
            this.pompa = pompa;
        }

        public boolean isStatusPompa() {
            return statusPompa;
        }

        public void setStatusPompa(boolean statusPompa) {
            this.statusPompa = statusPompa;
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

        public String getWaktuUpdate() {
            return waktuUpdate;
        }

        public void setWaktuUpdate(String waktuUpdate) {
            this.waktuUpdate = waktuUpdate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getRec() {
            return rec;
        }

        public void setRec(int rec) {
            this.rec = rec;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public String getKodeAlatVendor() {
            return kodeAlatVendor;
        }

        public void setKodeAlatVendor(String kodeAlatVendor) {
            this.kodeAlatVendor = kodeAlatVendor;
        }

        public String getUrlCctv() {
            return urlCctv;
        }

        public void setUrlCctv(String urlCctv) {
            this.urlCctv = urlCctv;
        }

        public String getRemotePompa() {
            return remotePompa;
        }

        public void setRemotePompa(String remotePompa) {
            this.remotePompa = remotePompa;
        }

        public String getCameraId() {
            return cameraId;
        }

        public void setCameraId(String cameraId) {
            this.cameraId = cameraId;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }
    }
}