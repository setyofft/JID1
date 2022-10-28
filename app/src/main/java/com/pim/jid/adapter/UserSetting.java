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
    public static final String BATASKMSET = "bataskm";
    public static final String JALANPENGHUBUNGSET = "jalanpenghubungset";
    public static final String GERBANGTOLSET = "gerbangtolset";
    public static final String RESTAREASET = "restareaset";
    public static final String ROUDNESINDEXSET = "rougnesinex";
    public static final String RTMSSET = "rtmsset";
    public static final String RTMSSET2 = "rtmsset2";
    public static final String SPEDDSET = "speedset";
    public static final String WATER = "water";
    public static final String POMPA = "pompa";
    public static final String WIM = "wim";
    public static final String BIKE = "bike";
    public static final String GPSKEND = "gpskend";
    public static final String RADAR = "radar";

    public static final String onSet = "on";
    public static final String offSet = "off";

    private String jalanToll;
    private String kondisiTraffic;
    private String vms;
    private String cctv;
    private String gangguanLalin;
    private String pemeliharaan;
    private String rekayasaLalin;
    private String bataskm;
    private String jalanpenghubung;
    private String gerbangtol;
    private String restarea;
    private String rougnesindex;
    private String rtms;
    private String rtms2;

    public String getRadar() {
        return radar;
    }

    public void setRadar(String radar) {
        this.radar = radar;
    }

    private String radar;

    public String getGpskend() {
        return gpskend;
    }

    public void setGpskend(String gpskend) {
        this.gpskend = gpskend;
    }

    private String gpskend;

    public String getBike() {
        return bike;
    }

    public void setBike(String bike) {
        this.bike = bike;
    }

    private String bike;

    public String getWim() {
        return wim;
    }

    public void setWim(String wim) {
        this.wim = wim;
    }

    private String wim;

    public String getPompa() {
        return pompa;
    }

    public void setPompa(String pompa) {
        this.pompa = pompa;
    }

    private String pompa;

    public String getWaterlevel() {
        return waterlevel;
    }

    public void setWaterlevel(String waterlevel) {
        this.waterlevel = waterlevel;
    }

    private String waterlevel;

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    private String speed;

    public String getRtms() {
        return rtms;
    }

    public void setRtms(String rtms) {
        this.rtms = rtms;
    }

    public String getRtms2() {
        return rtms2;
    }

    public void setRtms2(String rtms2) {
        this.rtms2 = rtms2;
    }

    public String getRougnesindex() {
        return rougnesindex;
    }

    public void setRougnesindex(String rougnesindex) {
        this.rougnesindex = rougnesindex;
    }

    public String getRestarea() {
        return restarea;
    }

    public void setRestarea(String restarea) {
        this.restarea = restarea;
    }

    public String getGerbangtol() {
        return gerbangtol;
    }

    public void setGerbangtol(String gerbangtol) {
        this.gerbangtol = gerbangtol;
    }

    public String getJalanpenghubung() {
        return jalanpenghubung;
    }

    public void setJalanpenghubung(String jalanpenghubung) {
        this.jalanpenghubung = jalanpenghubung;
    }

    public String getBataskm() {
        return bataskm;
    }

    public void setBataskm(String bataskm) {
        this.bataskm = bataskm;
    }

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
