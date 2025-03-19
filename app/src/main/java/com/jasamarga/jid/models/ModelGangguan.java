package com.jasamarga.jid.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelGangguan {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("data")
    @Expose
    private Data data;

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        @SerializedName("data")
        @Expose
        private DataKerjaan data;

        @SerializedName("dataDaily")
        @Expose
        private DataKerjaanDaily dataDaily;

        public DataKerjaan getData() {
            return data;
        }

        public DataKerjaanDaily getDataDaily() {
            return dataDaily;
        }
    }

    public class DataKerjaan {
        @SerializedName("kerjaan_jalan")
        @Expose
        private String kerjaanJalan;

        @SerializedName("kecelakaan")
        @Expose
        private String kecelakaan;

        @SerializedName("gangguan_lalin")
        @Expose
        private String gangguanLalin;

        @SerializedName("penyekatan")
        @Expose
        private String penyekatan;

        @SerializedName("genangan")
        @Expose
        private String genangan;

        @SerializedName("lain_lain")
        @Expose
        private String lainLain;

        public String getKerjaanJalan() {
            return kerjaanJalan;
        }

        public String getKecelakaan() {
            return kecelakaan;
        }

        public String getGangguanLalin() {
            return gangguanLalin;
        }

        public String getPenyekatan() {
            return penyekatan;
        }

        public String getGenangan() {
            return genangan;
        }

        public String getLainLain() {
            return lainLain;
        }
    }

    public class DataKerjaanDaily {
        @SerializedName("kerjaan_jalan_daily")
        @Expose
        private String kerjaanJalan;

        @SerializedName("kecelakaan_daily")
        @Expose
        private String kecelakaan;

        @SerializedName("gangguan_lalin_daily")
        @Expose
        private String gangguanLalin;
        @SerializedName("penyekatan_daily")
        @Expose
        private String penyekatan;

        @SerializedName("genangan_daily")
        @Expose
        private String genangan;

        @SerializedName("lain_lain_daily")
        @Expose
        private String lainLain;

        public String getKerjaanJalan() {
            return kerjaanJalan;
        }

        public String getKecelakaan() {
            return kecelakaan;
        }

        public String getGangguanLalin() {
            return gangguanLalin;
        }

        public String getPenyekatan() {
            return penyekatan;
        }

        public String getGenangan() {
            return genangan;
        }

        public String getLainLain() {
            return lainLain;
        }
    }
}
