package com.jasamarga.jid.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataGate {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("data")
    @Expose
    private List<GateData> data;

    public boolean isStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public List<GateData> getData() {
        return data;
    }

    public class GateData {

        @SerializedName("_id")
        @Expose
        private String id;

        @SerializedName("id_gerbang")
        @Expose
        private int idGerbang;

        @SerializedName("nama_gerbang")
        @Expose
        private String namaGerbang;

        @SerializedName("no_urut")
        @Expose
        private int noUrut;

        @SerializedName("max_antrian")
        @Expose
        private int maxAntrian;

        @SerializedName("meter_antrian")
        @Expose
        private int meterAntrian;

        @SerializedName("waktu_update")
        @Expose
        private String waktuUpdate;

        @SerializedName("status_show")
        @Expose
        private int statusShow;

        @SerializedName("status_lalin")
        @Expose
        private int statusLalin;

        @SerializedName("id_ruas")
        @Expose
        private int idRuas;

        public String getId() {
            return id;
        }

        public int getIdGerbang() {
            return idGerbang;
        }

        public String getNamaGerbang() {
            return namaGerbang;
        }

        public int getNoUrut() {
            return noUrut;
        }

        public int getMaxAntrian() {
            return maxAntrian;
        }

        public int getMeterAntrian() {
            return meterAntrian;
        }

        public String getWaktuUpdate() {
            return waktuUpdate;
        }

        public int getStatusShow() {
            return statusShow;
        }

        public int getStatusLalin() {
            return statusLalin;
        }

        public int getIdRuas() {
            return idRuas;
        }
    }
}


