package com.jasamarga.jid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelRuas {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<DataRuas> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataRuas> getData() {
        return data;
    }

    public void setData(List<DataRuas> data) {
        this.data = data;
    }

    public class DataRuas {
       @SerializedName("id_ruas")
       @Expose
       private int idRuas;

       @SerializedName("id_cabang")
       @Expose
       private int idCabang;

       @SerializedName("id_region")
       @Expose
       private int idRegion;

       @SerializedName("nama_ruas")
       @Expose
       private String namaRuas;

       @SerializedName("nama_ruas_2")
       @Expose
       private String namaRuas2;

       @SerializedName("nama_ruas_singkatan")
       @Expose
       private String namaRuasSingkatan;

       @SerializedName("id_area")
       @Expose
       private int idArea;

       @SerializedName("no_urut_region")
       @Expose
       private int noUrutRegion;

       @SerializedName("ruas_jm")
       @Expose
       private String ruasJm;

       @SerializedName("arah_jalur_a")
       @Expose
       private String arahJalurA;

       @SerializedName("arah_jalur_b")
       @Expose
       private String arahJalurB;

       public int getIdRuas() {
           return idRuas;
       }

       public void setIdRuas(int idRuas) {
           this.idRuas = idRuas;
       }

       public int getIdCabang() {
           return idCabang;
       }

       public void setIdCabang(int idCabang) {
           this.idCabang = idCabang;
       }

       public int getIdRegion() {
           return idRegion;
       }

       public void setIdRegion(int idRegion) {
           this.idRegion = idRegion;
       }

       public String getNamaRuas() {
           return namaRuas;
       }

       public void setNamaRuas(String namaRuas) {
           this.namaRuas = namaRuas;
       }

       public String getNamaRuas2() {
           return namaRuas2;
       }

       public void setNamaRuas2(String namaRuas2) {
           this.namaRuas2 = namaRuas2;
       }

       public String getNamaRuasSingkatan() {
           return namaRuasSingkatan;
       }

       public void setNamaRuasSingkatan(String namaRuasSingkatan) {
           this.namaRuasSingkatan = namaRuasSingkatan;
       }

       public int getIdArea() {
           return idArea;
       }

       public void setIdArea(int idArea) {
           this.idArea = idArea;
       }

       public int getNoUrutRegion() {
           return noUrutRegion;
       }

       public void setNoUrutRegion(int noUrutRegion) {
           this.noUrutRegion = noUrutRegion;
       }

       public String getRuasJm() {
           return ruasJm;
       }

       public void setRuasJm(String ruasJm) {
           this.ruasJm = ruasJm;
       }

       public String getArahJalurA() {
           return arahJalurA;
       }

       public void setArahJalurA(String arahJalurA) {
           this.arahJalurA = arahJalurA;
       }

       public String getArahJalurB() {
           return arahJalurB;
       }

       public void setArahJalurB(String arahJalurB) {
           this.arahJalurB = arahJalurB;
       }
   }
}
