package com.suryodayach.bluetooth.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.suryodayach.bluetooth.R;
import com.suryodayach.bluetooth.adapter.BluetoothDeviceAdapter;
import com.suryodayach.bluetooth.model.Device;
import com.suryodayach.bluetooth.model.FilterOptions;
import com.suryodayach.bluetooth.utils.BluetoothDeviceUtils;
import com.suryodayach.bluetooth.viewmodel.BluetoothViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1;
    private BluetoothViewModel bluetoothViewModel;
    BluetoothDeviceAdapter adapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView textViewBluetoothStatus;
    Button btnFilter, btnBluetooth;
    FilterOptions currentFilterOptions = new FilterOptions();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
    };
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
        textViewBluetoothStatus = findViewById(R.id.text_view_bluetooth_status);
        btnFilter = findViewById(R.id.btn_filter);
        btnBluetooth = findViewById(R.id.btn_bluetooth);
        adapter = new BluetoothDeviceAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Adding the divider to the list of devices
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Opens the Details on clicking a device from list.
        adapter.setOnDeviceClickListener(device -> {
            Intent detailsIntent = new Intent(this, DeviceDetailsActivity.class);
            detailsIntent.putExtra("DeviceAddress", device.getAddress());
            startActivity(detailsIntent);
        });

        btnFilter.setOnClickListener(view -> {
            showFilterDialog();
        });

        if (bluetoothAdapter.isEnabled()) {
            textViewBluetoothStatus.setText("Bluetooth ON");
            btnBluetooth.setText("Turn OFF");
        } else {
            textViewBluetoothStatus.setText("Bluetooth OFF");
            btnBluetooth.setText("Turn ON");
        }
        btnBluetooth.setOnClickListener(view -> {
            toggleBluetooth();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkAndRequestPermissions()) {
            bluetoothViewModel.getDiscoveredDevices().observe(this, devices -> {
                Log.e(TAG, "onResume: " + devices);
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

            // Checking if the address is unique
            if (!uniqueAddresses.contains(address)) {
                uniqueAddresses.add(address);
                updatedDevices.add(
                        new Device(
                                address,
                                device.getName(),
                                BluetoothDeviceUtils.getPairingStatus(device),
                                BluetoothDeviceUtils.getMajorDeviceClass(device)
                        ));
            }
        }
        adapter.submitList(updatedDevices);
        adapter.setOriginalList(updatedDevices);
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

    //Turning On and Off Bluetooth
    private void toggleBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_CODE);
        } else {
            bluetoothAdapter.disable();
            textViewBluetoothStatus.setText("Bluetooth OFF");
            btnBluetooth.setText("Turn ON");
            bluetoothViewModel.stopDiscovery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                textViewBluetoothStatus.setText("Bluetooth ON");
                btnBluetooth.setText("Turn OFF");
                recreate();
            } else {
                Toast.makeText(this, "Bluetooth request denied or error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Checking whether Permission is already granted
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

    private void showFilterDialog() {
        FilterDialog.showFilterDialog(this, currentFilterOptions, filterOptions -> {
            // Handling the applied filter options
            currentFilterOptions = filterOptions;
            Log.e(TAG, "onFilterApplied: " + filterOptions.getSelectedTypes() );
            applyFilter(filterOptions);
        });
    }

    private void applyFilter(FilterOptions filterOptions) {
        adapter.setFilterOptions(filterOptions);
    }

}