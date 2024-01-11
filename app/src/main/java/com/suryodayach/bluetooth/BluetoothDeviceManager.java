package com.suryodayach.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothDeviceManager {

    private static final UUID UUID_SSP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter bluetoothAdapter;
    private Context context;


    public BluetoothDeviceManager(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    public BluetoothDevice getDeviceByAddress(String deviceAddress) {
        if (bluetoothAdapter == null || !BluetoothAdapter.checkBluetoothAddress(deviceAddress)) {
            return null;
        }
        return bluetoothAdapter.getRemoteDevice(deviceAddress);
    }

    public BluetoothSocket connectToDevice(BluetoothDevice device) {
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID_SSP);
            socket.connect();
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getSignalStrength(BluetoothDevice device) {
        if (bluetoothAdapter == null || device == null) {
            return -1; // Indicate an error or invalid input
        }
        BluetoothGatt bluetoothGatt = device.connectGatt(context, false, new BluetoothGattCallback() {
            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                // Use the 'rssi' value as the signal strength
                Log.d("BluetoothSignalStrength", "RSSI: " + rssi);
            }
        });

        // Trigger reading RSSI
        bluetoothGatt.readRemoteRssi();

        return -1; // Default value, indicating that the method might not provide real-time signal strength
    }

    public String getPairingStatus(BluetoothDevice device) {
        if (device == null) {
            return "No";
        }

        int bondState = device.getBondState();
        if (bondState == BluetoothDevice.BOND_BONDED) {
            return "Yes";
        }
        return "No";
    }

    public boolean pairDevice(BluetoothDevice device) {
        if (device == null) {
            return false; // Indicate an error or invalid input
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
}
