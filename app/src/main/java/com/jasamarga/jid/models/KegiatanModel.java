package com.jasamarga.jid.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KegiatanModel {
    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("data")
    @Expose
    private List<DataItem> data;

    @SerializedName("msg")
    @Expose
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public class DataItem {
        @SerializedName("_id")
        @Expose
        private Id id;

        @SerializedName("ket_jenis_kegiatan")
        @Expose
        private String jenisKegiatan;

        @SerializedName("selesai")
        @Expose
        private int selesai;

        @SerializedName("proses")
        @Expose
        private int proses;

        @SerializedName("rencana")
        @Expose
        private int rencana;

        public Id getId() {
            return id;
        }

        public void setId(Id id) {
            this.id = id;
        }

        public String getJenisKegiatan() {
            return jenisKegiatan;
        }

        public void setJenisKegiatan(String jenisKegiatan) {
            this.jenisKegiatan = jenisKegiatan;
        }

        public int getSelesai() {
            return selesai;
        }

        public void setSelesai(int selesai) {
            this.selesai = selesai;
        }

        public int getProses() {
            return proses;
        }

        public void setProses(int proses) {
            this.proses = proses;
        }

        public int getRencana() {
            return rencana;
        }

        public void setRencana(int rencana) {
            this.rencana = rencana;
        }
    }
   public class Id {
        @SerializedName("jenis_kegiatan")
        @Expose
        private int jenisKegiatan;

        public int getJenisKegiatan() {
            return jenisKegiatan;
        }

        public void setJenisKegiatan(int jenisKegiatan) {
            this.jenisKegiatan = jenisKegiatan;
        }
    }
}




