package com.example.andrea.wifianalyzer;

public class AccessPoint {
    public static int ID = 0;
    public String SSID;
    public String BSSID;
    public int signalStrenght;
    public int frequency;
    public int channel;
    public long timestamp;
    public double distance;
    public int misurationNumber;

    public AccessPoint(){
        setMisurationNumber(ID);
    }

    public void setMisurationNumber(int misurationNumber) {
        this.misurationNumber = misurationNumber;
    }

    public int getMisurationNumber() {
        return misurationNumber;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public int getSignalStrenght() {
        return signalStrenght;
    }

    public void setSignalStrenght(int signalStrenght) {
        this.signalStrenght = signalStrenght;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
