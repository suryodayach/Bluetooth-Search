package com.suryodayach.bluetooth.repository;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class BluetoothRepository {

    private static final String TAG = "BluetoothRepository";
    private final Context applicationContext;

    public BluetoothRepository(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();

    private MutableLiveData<List<BluetoothDevice>> discoveredDevicesLiveData = new MutableLiveData<>();

    public MutableLiveData<List<BluetoothDevice>> getDiscoveredDevices() {
        return discoveredDevicesLiveData;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredDevices.add(device);
                discoveredDevicesLiveData.setValue(discoveredDevices);
            }
        }
    };

    public void startDiscovery() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        applicationContext.registerReceiver(receiver, filter);

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothAdapter.startDiscovery();
        }
    }

    public void refreshDevices() {
        if (bluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothAdapter.cancelDiscovery();
            discoveredDevices = new ArrayList<>();
            discoveredDevicesLiveData.setValue(discoveredDevices);
            startDiscovery();
        }
    }

    public void stopDiscovery() {
        if (ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        bluetoothAdapter.cancelDiscovery();
        try {
            applicationContext.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(TAG, "stopDiscovery: ", e);
        }
    }

}
