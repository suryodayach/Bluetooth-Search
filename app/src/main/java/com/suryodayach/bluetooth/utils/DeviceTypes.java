package com.suryodayach.bluetooth.utils;

public class DeviceTypes {

    public static final String AUDIO = "Audio";
    public static final String COMPUTER = "Computer";
    public static final String PHONE = "Phone";
    public static String[] getAllTypes() {
        return new String[]{AUDIO, COMPUTER, PHONE};
    }
}
