package com.suryodayach.bluetooth.model;

import java.util.HashSet;
import java.util.Set;

public class FilterOptions {

    private Set<String> selectedTypes = new HashSet<>();
    private String selectedPairedStatus;

    public Set<String> getSelectedTypes() {
        return selectedTypes;
    }

    public void addSelectedType(String type) {
        selectedTypes.add(type);
    }

    public String getSelectedPairedStatus() {
        return selectedPairedStatus;
    }

    public void setSelectedPairedStatus(String pairedStatus) {
        this.selectedPairedStatus = pairedStatus;
    }
}
