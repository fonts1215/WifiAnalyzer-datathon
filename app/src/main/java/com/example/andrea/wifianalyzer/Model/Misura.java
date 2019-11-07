package com.example.andrea.wifianalyzer.Model;

public class Misura {
    private String ssid;
    private String qualita;
    private String larghezza_banda;
    private String distance;
    private String canale;
    private String SignalStrenght;
    private String tdata;
    private String tora;

    public Misura(String ssid, String qualita, String larghezza_banda, String distance, String canale, String signalStrenght, String tdata, String tora) {
        this.ssid = ssid;
        this.qualita = qualita;
        this.larghezza_banda = larghezza_banda;
        this.distance = distance;
        this.canale = canale;
        SignalStrenght = signalStrenght;
        this.tdata = tdata;
        this.tora = tora;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getQualita() {
        return qualita;
    }

    public void setQualita(String qualita) {
        this.qualita = qualita;
    }

    public String getLarghezza_banda() {
        return larghezza_banda;
    }

    public void setLarghezza_banda(String larghezza_banda) {
        this.larghezza_banda = larghezza_banda;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCanale() {
        return canale;
    }

    public void setCanale(String canale) {
        this.canale = canale;
    }

    public String getSignalStrenght() {
        return SignalStrenght;
    }

    public void setSignalStrenght(String signalStrenght) {
        SignalStrenght = signalStrenght;
    }

    public String getTdata() {
        return tdata;
    }

    public void setTdata(String tdata) {
        this.tdata = tdata;
    }

    public String getTora() {
        return tora;
    }

    public void setTora(String tora) {
        this.tora = tora;
    }
}
