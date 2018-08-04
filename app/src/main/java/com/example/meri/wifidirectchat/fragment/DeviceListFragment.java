package com.example.meri.wifidirectchat.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meri.wifidirectchat.R;
import com.example.meri.wifidirectchat.adapter.DeviceAdapter;
import com.example.meri.wifidirectchat.viewmodel.DeviceViewModel;

import java.util.Collection;

public class DeviceListFragment extends Fragment {

    private DeviceAdapter.OnItemSelectedListener mOnItemSelectedListener =
            new DeviceAdapter.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WifiP2pDevice device) {
                    mModel.setDevice(device);
                    mOnFragmentActionListener.onItemSelection();
                }
            };

    private DeviceAdapter mAdapter;
    private DeviceViewModel mModel;
    private OnFragmentActionListener mOnFragmentActionListener;

    public DeviceListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
    }

    private void init(View view){
        RecyclerView recyclerView = view.findViewById(R.id.item_device_list_fragment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        mAdapter = new DeviceAdapter();
        recyclerView.setAdapter(mAdapter);

        mModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);
        mAdapter.setOnItemSelectedListener(mOnItemSelectedListener);
    }

    public void add(Collection<WifiP2pDevice> devices){
        mAdapter.addAll(devices);
    }

    public void setOnFragmentInteractionListener(OnFragmentActionListener onFragmentactionListener){
        mOnFragmentActionListener = onFragmentactionListener;
    }

    public interface OnFragmentActionListener{
        void onItemSelection();
    }
}
