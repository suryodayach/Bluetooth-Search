package com.suryodayach.bluetooth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.suryodayach.bluetooth.model.Device;
import com.suryodayach.bluetooth.R;

public class BluetoothDeviceAdapter extends ListAdapter<Device, BluetoothDeviceAdapter.DeviceHolder> {

    public BluetoothDeviceAdapter() {
        super(DIFF_CALLBACK);
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
    }

    public class DeviceHolder extends RecyclerView.ViewHolder {

        TextView address, deviceName;
        public DeviceHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.text_view_address);
            deviceName = itemView.findViewById(R.id.text_view_name);
        }
    }
}
