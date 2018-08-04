package com.example.meri.wifidirectchat.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.meri.wifidirectchat.R;

public class DeviceHolder extends RecyclerView.ViewHolder{

    private TextView mDeviceName;
    private TextView mDeviceAddress;

    public DeviceHolder(View itemView) {
        super(itemView);

        mDeviceName = itemView.findViewById(R.id.text_recycler_view_item_device_name);
        mDeviceAddress = itemView.findViewById(R.id.text_recycler_view_item_device_address);
    }

    public void bind(String name, String detail){
        mDeviceName.setText(name);
        mDeviceAddress.setText(detail);
    }
}
