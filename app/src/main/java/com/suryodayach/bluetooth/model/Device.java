package com.suryodayach.bluetooth.model;

import java.util.Objects;

public class Device {

    private String address;
    private String name;
    private String pairedStatus;
    private String type;

    public Device(String address, String name, String pairedStatus, String type) {
        this.address = address;
        this.name = name;
        this.pairedStatus = pairedStatus;
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPairedStatus() {
        return pairedStatus;
    }

    public void setPairedStatus(String pairedStatus) {
        this.pairedStatus = pairedStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
