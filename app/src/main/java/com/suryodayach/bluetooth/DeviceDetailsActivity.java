package com.suryodayach.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DeviceDetailsActivity extends AppCompatActivity {

    private static final String TAG = "DeviceDetailsActivity";

    BluetoothDeviceManager bluetoothDeviceManager;

    TextView textViewDeviceName, textViewAddress, textViewDeviceClass, textViewSignalStrength, textViewPaired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        Intent intent = getIntent();
        String bluetoothAddress = intent.getStringExtra("DeviceAddress");
        Log.e(TAG, "onCreate: " + bluetoothAddress);

        bluetoothDeviceManager = new BluetoothDeviceManager(this);

        textViewDeviceName = findViewById(R.id.text_view_device_name);
        textViewAddress = findViewById(R.id.text_view_device_address);
        textViewDeviceClass = findViewById(R.id.text_view_device_class);
        textViewSignalStrength = findViewById(R.id.text_view_signal_strength);
        textViewPaired = findViewById(R.id.text_view_paired);

        BluetoothDevice device = bluetoothDeviceManager.getDeviceByAddress(bluetoothAddress);

        if (device != null) {
            textViewDeviceName.setText(device.getName());
            textViewAddress.setText(device.getAddress());
            textViewDeviceClass.setText(device.getBluetoothClass().toString());
            textViewSignalStrength.setText(String.valueOf(bluetoothDeviceManager.getSignalStrength(device)));
            textViewPaired.setText(bluetoothDeviceManager.getPairingStatus(device));
        }
    }
}