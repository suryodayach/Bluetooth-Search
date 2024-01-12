package com.suryodayach.bluetooth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.suryodayach.bluetooth.R;
import com.suryodayach.bluetooth.utils.BluetoothDeviceManager;
import com.suryodayach.bluetooth.utils.BluetoothDeviceUtils;

public class DeviceDetailsActivity extends AppCompatActivity {

    private static final String TAG = "DeviceDetailsActivity";

    BluetoothDeviceManager bluetoothDeviceManager;

    TextView textViewDeviceName, textViewAddress, textViewDeviceClass, textViewSignalStrength, textViewPaired;
    Button btnPairDevice, btnConnectDevice;

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
        btnPairDevice = findViewById(R.id.btn_pair_device);
        btnConnectDevice = findViewById(R.id.btn_connect_device);

        BluetoothDevice device = bluetoothDeviceManager.getDeviceByAddress(bluetoothAddress);

        //Setting the details of the bluetooth devices
        if (device != null) {
            textViewDeviceName.setText(device.getName());
            textViewAddress.setText(device.getAddress());
            textViewDeviceClass.setText(BluetoothDeviceUtils.getMajorDeviceClass(device));
            textViewSignalStrength.setText(String.valueOf(bluetoothDeviceManager.getSignalStrength(device)));
            setPairDeviceStatus(device);
        }

        btnPairDevice.setOnClickListener(view -> {
            if (BluetoothDeviceUtils.getPairingStatus(device).equalsIgnoreCase("Paired")) {
                bluetoothDeviceManager.unpairDevice(device);
            } else {
                bluetoothDeviceManager.pairDevice(device);
            }
        });

        btnConnectDevice.setOnClickListener(view -> {
            bluetoothDeviceManager.connectToDevice(device);
        });
    }

    // Setting the Device Pair Status
    private void setPairDeviceStatus(BluetoothDevice device) {
        if (BluetoothDeviceUtils.getPairingStatus(device).equalsIgnoreCase("Paired")) {
            textViewPaired.setText("Paired");
            btnPairDevice.setText("Unpair Device");
        } else {
            textViewPaired.setText("Unpaired");
            btnPairDevice.setText("Pair Device");
        }
    }
}