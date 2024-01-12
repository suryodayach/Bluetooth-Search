package com.suryodayach.bluetooth.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothDeviceManager {

    private static final UUID UUID_SSP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter bluetoothAdapter;
    private Context context;
    private BluetoothSocket socket;


    public BluetoothDeviceManager(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    // Gets BluetoothDevice using device address
    public BluetoothDevice getDeviceByAddress(String deviceAddress) {
        if (bluetoothAdapter == null || !BluetoothAdapter.checkBluetoothAddress(deviceAddress)) {
            return null;
        }
        return bluetoothAdapter.getRemoteDevice(deviceAddress);
    }

    // Connect to the Bluetooth device
    public BluetoothSocket connectToDevice(BluetoothDevice device) {
        try {
            socket = device.createRfcommSocketToServiceRecord(UUID_SSP);
            socket.connect();
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get Signal Strength of Bluetooth Device

    public int getSignalStrength(BluetoothDevice device) {
        if (bluetoothAdapter == null || device == null) {
            return -1;
        }
        BluetoothGatt bluetoothGatt = device.connectGatt(context, false, new BluetoothGattCallback() {
            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                Log.d("BluetoothSignalStrength", "RSSI: " + rssi);
            }
        });

        bluetoothGatt.readRemoteRssi();

        return -1;
    }

    // Pair the device
    public boolean pairDevice(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        // Initiate pairing if the device is not bonded,
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                Method method = device.getClass().getMethod("createBond");
                return (boolean) method.invoke(device);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // The device is already bonded
            return true;
        }
    }

    // Unpair Device
    public boolean unpairDevice(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        try {
            Method method = device.getClass().getMethod("removeBond");
            return (boolean) method.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
