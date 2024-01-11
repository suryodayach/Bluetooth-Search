package com.suryodayach.bluetooth;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.suryodayach.bluetooth.repository.BluetoothRepository;

import java.util.List;

public class BluetoothViewModel extends AndroidViewModel {

    private final BluetoothRepository bluetoothRepository;
    LiveData<List<BluetoothDevice>> discoveredDevices = new MutableLiveData<>();

    public BluetoothViewModel(@NonNull Application application) {
        super(application);
        this.bluetoothRepository = new BluetoothRepository(application);
    }

    public LiveData<List<BluetoothDevice>> getDiscoveredDevices() {
        if (discoveredDevices.getValue() == null) {
            discoveredDevices = bluetoothRepository.getDiscoveredDevices();
        }
        return discoveredDevices;
    }

    public void refreshDevices() {
        bluetoothRepository.refreshDevices();
    }

    public void startDiscovery() {
        bluetoothRepository.startDiscovery();
    }

    public void stopDiscovery() {
        bluetoothRepository.stopDiscovery();
    }

}
