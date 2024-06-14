package com.jasamarga.jid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataJalurModel {
    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("data")
    @Expose
    private Data[] data;

    @SerializedName("msg")
    @Expose
    private String msg;

    // Getter dan setter untuk status, data, dan msg
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public class Data {
        @SerializedName("_id")
        @Expose
        private Id id;

        @SerializedName("jalurA")
        @Expose
        private int jalurA;

        @SerializedName("jalurB")
        @Expose
        private int jalurB;

        // Getter dan setter untuk id, jalurA, dan jalurB
        public Id getId() {
            return id;
        }

        public void setId(Id id) {
            this.id = id;
        }

        public int getJalurA() {
            return jalurA;
        }

        public void setJalurA(int jalurA) {
            this.jalurA = jalurA;
        }

        public int getJalurB() {
            return jalurB;
        }

        public void setJalurB(int jalurB) {
            this.jalurB = jalurB;
        }
    }

    public class Id {
        @SerializedName("jalur")
        @Expose
        private String jalur;

        // Getter dan setter untuk jalur
        public String getJalur() {
            return jalur;
        }

        public void setJalur(String jalur) {
            this.jalur = jalur;
        }
    }
}
