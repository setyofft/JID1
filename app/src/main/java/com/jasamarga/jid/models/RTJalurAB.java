package com.jasamarga.jid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RTJalurAB {
    private DataJalurA dataA;
    private DataJalurB dataB;

    public DataJalurA getDataA() {
        return dataA;
    }

    public void setDataA(DataJalurA dataA) {
        this.dataA = dataA;
    }

    public DataJalurB getDataB() {
        return dataB;
    }

    public void setDataB(DataJalurB dataB) {
        this.dataB = dataB;
    }

    public static class DataJalurA {

        @SerializedName("nama_segment")
        @Expose
        private String namaSegment;

        @SerializedName("idx")
        @Expose
        private int idx;

        @SerializedName("no_urut")
        @Expose
        private int noUrut;

        @SerializedName("nama_jalur")
        @Expose
        private String namaJalur;

        @SerializedName("kec_google")
        @Expose
        private double kecGoogle;

        @SerializedName("update_time")
        @Expose
        private String updateTime;

        @SerializedName("panjang_segment")
        @Expose
        private int panjangSegment;

        @SerializedName("waktu_tempuh")
        @Expose
        private int waktuTempuh;
        public DataJalurA(RealtimeTrModel.DataItem dataItem) {
            this.namaSegment = dataItem.getNamaSegment();
            this.idx = dataItem.getIdx();
            this.noUrut = dataItem.getNoUrut();
            this.namaJalur = dataItem.getNamaJalur();
            this.kecGoogle = dataItem.getKecGoogle();
            this.updateTime = dataItem.getUpdateTime();
            this.panjangSegment = dataItem.getPanjangSegment();
            this.waktuTempuh = dataItem.getWaktuTempuh();
        }
        // Getter and setter methods

        public String getNamaSegment() {
            return namaSegment;
        }

        public void setNamaSegment(String namaSegment) {
            this.namaSegment = namaSegment;
        }

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }

        public int getNoUrut() {
            return noUrut;
        }

        public void setNoUrut(int noUrut) {
            this.noUrut = noUrut;
        }

        public String getNamaJalur() {
            return namaJalur;
        }

        public void setNamaJalur(String namaJalur) {
            this.namaJalur = namaJalur;
        }

        public double getKecGoogle() {
            return kecGoogle;
        }

        public void setKecGoogle(double kecGoogle) {
            this.kecGoogle = kecGoogle;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getPanjangSegment() {
            return panjangSegment;
        }

        public void setPanjangSegment(int panjangSegment) {
            this.panjangSegment = panjangSegment;
        }

        public int getWaktuTempuh() {
            return waktuTempuh;
        }

        public void setWaktuTempuh(int waktuTempuh) {
            this.waktuTempuh = waktuTempuh;
        }
    }

    public static class DataJalurB {

        @SerializedName("nama_segment")
        @Expose
        private String namaSegment;

        @SerializedName("idx")
        @Expose
        private int idx;

        @SerializedName("no_urut")
        @Expose
        private int noUrut;

        @SerializedName("nama_jalur")
        @Expose
        private String namaJalur;

        @SerializedName("kec_google")
        @Expose
        private double kecGoogle;

        @SerializedName("update_time")
        @Expose
        private String updateTime;

        @SerializedName("panjang_segment")
        @Expose
        private int panjangSegment;

        @SerializedName("waktu_tempuh")
        @Expose
        private int waktuTempuh;
        public DataJalurB(RealtimeTrModel.DataItem dataItem) {
            this.namaSegment = dataItem.getNamaSegment();
            this.idx = dataItem.getIdx();
            this.noUrut = dataItem.getNoUrut();
            this.namaJalur = dataItem.getNamaJalur();
            this.kecGoogle = dataItem.getKecGoogle();
            this.updateTime = dataItem.getUpdateTime();
            this.panjangSegment = dataItem.getPanjangSegment();
            this.waktuTempuh = dataItem.getWaktuTempuh();
        }
        // Getter and setter methods

        public String getNamaSegment() {
            return namaSegment;
        }

        public void setNamaSegment(String namaSegment) {
            this.namaSegment = namaSegment;
        }

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }

        public int getNoUrut() {
            return noUrut;
        }

        public void setNoUrut(int noUrut) {
            this.noUrut = noUrut;
        }

        public String getNamaJalur() {
            return namaJalur;
        }

        public void setNamaJalur(String namaJalur) {
            this.namaJalur = namaJalur;
        }

        public double getKecGoogle() {
            return kecGoogle;
        }

        public void setKecGoogle(double kecGoogle) {
            this.kecGoogle = kecGoogle;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getPanjangSegment() {
            return panjangSegment;
        }

        public void setPanjangSegment(int panjangSegment) {
            this.panjangSegment = panjangSegment;
        }

        public int getWaktuTempuh() {
            return waktuTempuh;
        }

        public void setWaktuTempuh(int waktuTempuh) {
            this.waktuTempuh = waktuTempuh;
        }
    }
}
