package com.huzefagadi.rashida.nfcapplication.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rashida on 15/07/15.
 */
public class Gourme {

  // {"com.huzefagadi.rashida.nfcapplication.bean.Gourme":[{"id ":"1", "firma ":"Test d.o.o.", "ime ":"Test", "priimek ":"Testni", "koda ":"12345", "aktiven ":"True", "limit ":"500.0000", "prioriteta ":"False" }]}
    @SerializedName("id ")
    private String id;
    @SerializedName("firma ")
    private String firma;
    @SerializedName("ime ")
    private String ime;
    @SerializedName("priimek ")
    private String priimek;
    @SerializedName("koda ")
    private String koda;
    @SerializedName("aktiven ")
    private Boolean aktiven;
    @SerializedName("limit ")
    private String limit;
    @SerializedName("prioriteta ")
    private Boolean prioriteta;

    public Gourme(String id, String firma, String ime, String priimek, String koda, Boolean aktiven, String limit, Boolean prioriteta) {
        this.id = id;
        this.firma = firma;
        this.ime = ime;
        this.priimek = priimek;
        this.koda = koda;
        this.aktiven = aktiven;
        this.limit = limit;
        this.prioriteta = prioriteta;
    }

    public Gourme() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPriimek() {
        return priimek;
    }

    public void setPriimek(String priimek) {
        this.priimek = priimek;
    }

    public String getKoda() {
        return koda;
    }

    public void setKoda(String koda) {
        this.koda = koda;
    }

    public Boolean getAktiven() {
        return aktiven;
    }

    public void setAktiven(Boolean aktiven) {
        this.aktiven = aktiven;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public Boolean getPrioriteta() {
        return prioriteta;
    }

    public void setPrioriteta(Boolean prioriteta) {
        this.prioriteta = prioriteta;
    }
}
