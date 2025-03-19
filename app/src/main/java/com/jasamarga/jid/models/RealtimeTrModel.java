package com.jasamarga.jid.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RealtimeTrModel {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("data")
    @Expose
    private List<DataItem> data;

    @SerializedName("msg")
    @Expose
    private String msg;

    // Getter and setter methods

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<DataItem> getData() {
        return data;
    }

    public void setData(List<DataItem> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    // Inner class representing the "data" array
    public static class DataItem {

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

