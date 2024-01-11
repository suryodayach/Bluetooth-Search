package com.suryodayach.bluetooth.repository;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class BluetoothRepository {

    private final Context applicationContext;

    public BluetoothRepository(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<>();

    private MutableLiveData<List<BluetoothDevice>> discoveredDevicesLiveData = new MutableLiveData<>();

    public LiveData<List<BluetoothDevice>> getDiscoveredDevices() {
        return discoveredDevicesLiveData;
    }

    public void startDiscovery() {
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

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        applicationContext.registerReceiver(receiver, filter);

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothAdapter.startDiscovery();
        }
    }

    public void stopDiscovery() {
        if (ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        bluetoothAdapter.cancelDiscovery();
    }

}
