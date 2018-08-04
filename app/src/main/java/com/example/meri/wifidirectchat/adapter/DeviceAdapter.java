package com.example.meri.wifidirectchat.adapter;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meri.wifidirectchat.R;
import com.example.meri.wifidirectchat.holder.DeviceHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder>{

    private List<WifiP2pDevice> mDevices;
    private OnItemSelectedListener mOnItemSelectedListener;

    public DeviceAdapter() {
        mDevices = new ArrayList<>();
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);

        return new DeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceHolder holder, int position) {
        WifiP2pDevice device = mDevices.get(position);

        holder.bind(device.deviceName, device.deviceAddress);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiP2pDevice device = mDevices.get(holder.getAdapterPosition());
                mOnItemSelectedListener.onItemSelected(device);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public void addAll(Collection<WifiP2pDevice> devices){
        mDevices.clear();
        mDevices.addAll(devices);
        notifyDataSetChanged();
    }

    public WifiP2pDevice getDevice(int i){
        return mDevices.get(i);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener{
        void onItemSelected(WifiP2pDevice device);
    }
}