package com.jasamarga.jid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelRegion {

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private List<DataRegion> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataRegion> getData() {
        return data;
    }

    public void setData(List<DataRegion> data) {
        this.data = data;
    }

    public class DataRegion{
        @SerializedName("id_region")
        @Expose
        private int id_region;
        @SerializedName("nama_region")
        @Expose
        private String nama_region;

        public int getId_region() {
            return id_region;
        }

        public void setId_region(int id_region) {
            this.id_region = id_region;
        }

        public String getNama_region() {
            return nama_region;
        }

        public void setNama_region(String nama_region) {
            this.nama_region = nama_region;
        }
    }
}
