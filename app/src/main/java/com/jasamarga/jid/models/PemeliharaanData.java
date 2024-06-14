package com.jasamarga.jid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PemeliharaanData {
        @SerializedName("status")
        @Expose
        private boolean status;

        @SerializedName("data")
        @Expose
        private Data data;

        @SerializedName("msg")
        @Expose
        private String msg;

        // Getters and setters for the fields
        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        // Rest of the class remains the same...

        // Nested classes with @SerializedName and @Expose annotations

        public static class Data {
            @SerializedName("total")
            @Expose
            private int total;

            @SerializedName("result")
            @Expose
            private List<ResultEntry> result;

            // Getters and setters for the fields...

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public List<ResultEntry> getResult() {
                return result;
            }

            public void setResult(List<ResultEntry> result) {
                this.result = result;
            }
        }

        public static class ResultEntry {
            @SerializedName("_id")
            @Expose
            private Id id;

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
            // Getters and setters for the fields...
        }

        public static class Id {
            @SerializedName("yearMonthDay")
            @Expose
            private String yearMonthDay;

            @SerializedName("yearMonth")
            @Expose
            private String yearMonth;

            @SerializedName("month")
            @Expose
            private String month;

            @SerializedName("bulan")
            @Expose
            private String bulan;

            public String getYearMonthDay() {
                return yearMonthDay;
            }

            public void setYearMonthDay(String yearMonthDay) {
                this.yearMonthDay = yearMonthDay;
            }

            public String getYearMonth() {
                return yearMonth;
            }

            public void setYearMonth(String yearMonth) {
                this.yearMonth = yearMonth;
            }

            public String getMonth() {
                return month;
            }

            public void setMonth(String month) {
                this.month = month;
            }

            public String getBulan() {
                return bulan;
            }

            public void setBulan(String bulan) {
                this.bulan = bulan;
            }
            // Getters and setters for the fields...
        }

}
