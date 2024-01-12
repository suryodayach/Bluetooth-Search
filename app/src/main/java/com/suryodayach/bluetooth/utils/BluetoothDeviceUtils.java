package com.suryodayach.bluetooth.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceUtils {

    // Gets the bluetooth device class
    public static String getMajorDeviceClass(BluetoothDevice device) {
        if (device == null) {
            return "Unknown";
        }

        BluetoothClass bluetoothClass = device.getBluetoothClass();
        int majorDeviceClass = bluetoothClass.getMajorDeviceClass();

        switch (majorDeviceClass) {
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return "Audio/Video";

            case BluetoothClass.Device.Major.COMPUTER:
                return "Computer";

            case BluetoothClass.Device.Major.HEALTH:
                return "Health";

            case BluetoothClass.Device.Major.IMAGING:
                return "Imaging";

            case BluetoothClass.Device.Major.MISC:
                return "Miscellaneous";

            case BluetoothClass.Device.Major.NETWORKING:
                return "Networking";

            case BluetoothClass.Device.Major.PERIPHERAL:
                return "Peripheral";

            case BluetoothClass.Device.Major.PHONE:
                return "Phone";

            case BluetoothClass.Device.Major.TOY:
                return "Toy";

            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return "Uncategorized";

            case BluetoothClass.Device.Major.WEARABLE:
                return "Wearable";

            default:
                return "Unknown";
        }
    }

    // Gets pairing status of bluetooth device, returns Paired and Unpaired
    public static String getPairingStatus(BluetoothDevice device) {
        if (device == null) {
            return "Unpaired";
        }

        int bondState = device.getBondState();
        if (bondState == BluetoothDevice.BOND_BONDED) {
            return "Paired";
        }
        return "Unpaired";
    }

}
