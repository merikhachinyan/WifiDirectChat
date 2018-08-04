package com.example.meri.wifidirectchat.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.wifi.p2p.WifiP2pDevice;

public class DeviceViewModel extends ViewModel{
    MutableLiveData<WifiP2pDevice> mDevice =
            new MutableLiveData<>();

    public void setDevice(WifiP2pDevice device){
        mDevice.setValue(device);
    }

    public MutableLiveData<WifiP2pDevice> getDevice(){
        return mDevice;
    }
}
