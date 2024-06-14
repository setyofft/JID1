package com.jasamarga.jid.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelRekayasa {
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
        private InnerData data;

        @SerializedName("dataDaily")
        @Expose
        private InnerDataDaily dataDaily;

        public InnerData getData() {
            return data;
        }

        public InnerDataDaily getDataDaily() {
            return dataDaily;
        }
    }

    public class InnerData {
        @SerializedName("contra_flow")
        @Expose
        private String contraFlow;

        @SerializedName("one_way")
        @Expose
        private String oneWay;

        @SerializedName("pengalihan")
        @Expose
        private String pengalihan;

        public String getContraFlow() {
            return contraFlow;
        }

        public String getOneWay() {
            return oneWay;
        }

        public String getPengalihan() {
            return pengalihan;
        }
    }

    public class InnerDataDaily {
        @SerializedName("contra_flow_daily")
        @Expose
        private String contraFlow;

        @SerializedName("one_way_daily")
        @Expose
        private String oneWay;

        @SerializedName("pengalihan_daily")
        @Expose
        private String pengalihan;

        public String getContraFlow() {
            return contraFlow;
        }

        public String getOneWay() {
            return oneWay;
        }

        public String getPengalihan() {
            return pengalihan;
        }
    }
}


