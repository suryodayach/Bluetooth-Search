package com.suryodayach.bluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.suryodayach.bluetooth.adapter.BluetoothDeviceAdapter;
import com.suryodayach.bluetooth.model.Device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BluetoothViewModel bluetoothViewModel;
    BluetoothDeviceAdapter adapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
    };
    private static final int REQUEST_CODE = 1;
    List<Device> foundDevices = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        checkAndRequestPermissions();

        bluetoothViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (checkAndRequestPermissions()) {
                bluetoothViewModel.refreshDevices();
            } else {
                Toast.makeText(getApplicationContext(), "Please Grant Permissions", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initUI() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.recycler_view_devices);
        adapter = new BluetoothDeviceAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter.setOnDeviceClickListener(device -> {
            Intent detailsIntent = new Intent(this, DeviceDetailsActivity.class);
            detailsIntent.putExtra("DeviceAddress", device.getAddress());
            startActivity(detailsIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkAndRequestPermissions()) {
            bluetoothViewModel.getDiscoveredDevices().observe(this, devices -> {
                Log.e(TAG, "onResume: " + devices );
                swipeRefreshLayout.setRefreshing(false);
                updateDeviceList(devices);
            });

            bluetoothViewModel.startDiscovery();
        }
    }

    private void updateDeviceList(List<BluetoothDevice> devices) {
        Set<String> uniqueAddresses = new HashSet<>();
        List<Device> updatedDevices = new ArrayList<>();

        for (BluetoothDevice device : devices) {
            String address = device.getAddress();

            // Check if the address is unique
            if (!uniqueAddresses.contains(address)) {
                uniqueAddresses.add(address);
                updatedDevices.add(new Device(address, device.getName()));
            }
        }

        adapter.submitList(updatedDevices);
    }

        @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothViewModel.stopDiscovery();
    }

    private boolean checkAndRequestPermissions() {
        if (!checkIfAlreadyHavePermission()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
            return false;
        }
        return true;
    }

    boolean checkIfAlreadyHavePermission() {
        for (String permission : permissions) {
            int result = this.checkSelfPermission(permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (!(grantResult == PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(getApplicationContext(), "Please Grant Permissions", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
            }
            recreate();
        }
    }
}