package com.huzefagadi.rashida.nfcapplication.bean;

import java.util.List;

/**
 * Created by Rashida on 15/07/15.
 */
public class ResponseFromServer {

    private List<Gourme> Gourme;

    public ResponseFromServer() {
    }

    public ResponseFromServer(List<com.huzefagadi.rashida.nfcapplication.bean.Gourme> gourme) {
        Gourme = gourme;
    }

    public List<com.huzefagadi.rashida.nfcapplication.bean.Gourme> getGourme() {
        return Gourme;
    }

    public void setGourme(List<com.huzefagadi.rashida.nfcapplication.bean.Gourme> gourme) {
        Gourme = gourme;
    }
}
