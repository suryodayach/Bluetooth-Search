package com.suryodayach.bluetooth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.suryodayach.bluetooth.model.FilterOptions;
import com.suryodayach.bluetooth.model.Device;
import com.suryodayach.bluetooth.R;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceAdapter extends ListAdapter<Device, BluetoothDeviceAdapter.DeviceHolder> {

    private OnDeviceClickListener onDeviceClickListener;
    private List<Device> deviceList = new ArrayList<>();
    private FilterOptions filterOptions;

    public interface OnDeviceClickListener {
        void onDeviceClick(Device device);
    }

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.onDeviceClickListener = listener;
    }

    public void setOriginalList(List<Device> originalList) {
        this.deviceList = originalList;
    }

    public void setEmptyOriginalList() {
        this.deviceList = new ArrayList<>();
    }

    public BluetoothDeviceAdapter() {
        super(DIFF_CALLBACK);
        filterOptions = new FilterOptions();
    }

    public static final DiffUtil.ItemCallback<Device> DIFF_CALLBACK = new DiffUtil.ItemCallback<Device>() {
        @Override
        public boolean areItemsTheSame(@NonNull Device oldItem, @NonNull Device newItem) {
            return oldItem.getAddress().equals(newItem.getAddress());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Device oldItem, @NonNull Device newItem) {
            return oldItem.getAddress().equals(newItem.getAddress());
        }
    };

    @NonNull
    @Override
    public BluetoothDeviceAdapter.DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothDeviceAdapter.DeviceHolder holder, int position) {
        final Device device = getItem(position);
        holder.address.setText(device.getAddress());
        holder.deviceName.setText(device.getName());

        holder.itemView.setOnClickListener(view -> {
            if (onDeviceClickListener != null) {
                onDeviceClickListener.onDeviceClick(device);
            }
        });
    }

    public class DeviceHolder extends RecyclerView.ViewHolder {

        TextView address, deviceName;
        public DeviceHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.text_view_address);
            deviceName = itemView.findViewById(R.id.text_view_name);
        }
    }

    public void setFilterOptions(FilterOptions options) {
        filterOptions = options;
        applyFilter();
    }

    private void applyFilter() {
        if (deviceList == null) {
            return;
        }

        List<Device> filteredList = new ArrayList<>();
        for (Device device : deviceList) {
            // Check if the device type matches the selected types
            boolean typeMatches = filterOptions.getSelectedTypes().isEmpty()
                    || filterOptions.getSelectedTypes().contains(device.getType());

            // Check if the paired status matches the selected paired status
            boolean pairedStatusMatches = filterOptions.getSelectedPairedStatus() == null
                    || filterOptions.getSelectedPairedStatus().equals("All")
                    || (filterOptions.getSelectedPairedStatus().equals("Paired") && device.getPairedStatus().equals("Paired"))
                    || (filterOptions.getSelectedPairedStatus().equals("Unpaired") && device.getPairedStatus().equals("Unpaired"));

            // Adding the device to the filtered list if both type and paired status match,
            if (typeMatches && pairedStatusMatches) {
                filteredList.add(device);
            }

        }
        submitList(filteredList);
    }
}
