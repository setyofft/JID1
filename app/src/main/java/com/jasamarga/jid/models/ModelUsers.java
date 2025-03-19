package com.jasamarga.jid.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class ModelUsers {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("data")
    @Expose
    private UserData data;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public boolean isStatus() {
        return status;
    }

    public UserData getData() {
        return data;
    }

    public class UserData {
        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("mail")
        @Expose
        private String mail;

        @SerializedName("expire")
        @Expose
        private int expire;

        @SerializedName("vip")
        @Expose
        private int vip;

        @SerializedName("scope")
        @Expose
        private String scope;

        @SerializedName("item")
        @Expose
        private String item;

        @SerializedName("info")
        @Expose
        private String info;

        @SerializedName("report")
        @Expose
        private String report;

        @SerializedName("dashboard")
        @Expose
        private String dashboard;

        @SerializedName("last")
        @Expose
        private String last;

        @SerializedName("login")
        @Expose
        private String login;

        @SerializedName("sisinfokom")
        @Expose
        private String sisinfokom;

        @SerializedName("infojalantol")
        @Expose
        private String infojalantol;

        @SerializedName("eventjalantol")
        @Expose
        private String eventjalantol;
        @SerializedName("verified")
        @Expose
        private boolean verified;

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getMail() {
            return mail;
        }

        public int getExpire() {
            return expire;
        }

        public int getVip() {
            return vip;
        }

        public String getScope() {
            return scope;
        }

        public String getItem() {
            return item;
        }

        public String getInfo() {
            return info;
        }

        public String getReport() {
            return report;
        }

        public String getDashboard() {
            return dashboard;
        }

        public String getLast() {
            return last;
        }

        public String getLogin() {
            return login;
        }

        public String getSisinfokom() {
            return sisinfokom;
        }

        public String getInfojalantol() {
            return infojalantol;
        }

        public String getEventjalantol() {
            return eventjalantol;
        }
    }

    public class SisinfokomMenu {
        @SerializedName("key")
        @Expose
        private int key;

        @SerializedName("menu")
        @Expose
        private String menu;

        public int getKey() {
            return key;
        }

        public String getMenu() {
            return menu;
        }
    }
}

