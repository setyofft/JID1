package com.pim.jid.adapter;

import android.app.Application;

public class UserSetting extends Application {
    public static final String PREFERENCES = "preferences";

    public static final String JALANTOLSET = "jalantolset";
    public static final String KONDISITRAFFICSET = "kondisitrafficset";
    public static final String VMSSET = "vmsset";
    public static final String CCTVSET = "cctvset";
    public static final String GANGGUANLALINSET = "gangguanlalinset";
    public static final String PEMELIHARAANSET = "pemeliharaanset";
    public static final String REKAYASALALINSET = "rekayasalalin";

    public static final String onSet = "on";
    public static final String offSet = "off";

    private String jalanToll, kondisiTraffic, vms, cctv, gangguanLalin, pemeliharaan, rekayasaLalin;

    public String getJalanToll() {
        return jalanToll;
    }

    public void setJalanToll(String jalanToll) {
        this.jalanToll = jalanToll;
    }

    public String getKondisiTraffic() {
        return kondisiTraffic;
    }

    public void setKondisiTraffic(String kondisiTraffic) {
        this.kondisiTraffic = kondisiTraffic;
    }

    public String getVms() {
        return vms;
    }

    public void setVms(String vms) {
        this.vms = vms;
    }

    public String getCctv() {
        return cctv;
    }

    public void setCctv(String cctv) {
        this.cctv = cctv;
    }

    public String getGangguanLalin() {
        return gangguanLalin;
    }

    public void setGangguanLalin(String gangguanLalin) {
        this.gangguanLalin = gangguanLalin;
    }

    public String getPemeliharaan() {
        return pemeliharaan;
    }

    public void setPemeliharaan(String pemeliharaan) {
        this.pemeliharaan = pemeliharaan;
    }

    public String getRekayasaLalin() {
        return rekayasaLalin;
    }

    public void setRekayasaLalin(String rekayasaLalin) {
        this.rekayasaLalin = rekayasaLalin;
    }
}
