package com.example.meri.wifidirectchat;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.meri.wifidirectchat.fragment.DeviceListFragment;
import com.example.meri.wifidirectchat.fragment.MessageFragment;
import com.example.meri.wifidirectchat.receiver.WifiDirectBroadcastReceiver;
import com.example.meri.wifidirectchat.viewmodel.DeviceViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private DeviceListFragment.OnFragmentActionListener mOnFragmentActionListener =
            new DeviceListFragment.OnFragmentActionListener() {
                @Override
                public void onItemSelection() {
                    connect();
                }
            };

    private MessageFragment.OnFragmentActionListener mActionListener =
            new MessageFragment.OnFragmentActionListener() {
                @Override
                public void onSendMessage(String message) {
                    mSendReceive.write(message.getBytes());
                }
            };

    public static final int READ = 1;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiDirectBroadcastReceiver mReceiver;

    private DeviceListFragment mDeviceListFragment;
    private MessageFragment mMessageFragment;
    private DeviceViewModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        mDeviceListFragment = new DeviceListFragment();
        mMessageFragment = new MessageFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.layout_main_activity, mDeviceListFragment, "Devices");
        transaction.commit();

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        mModel = ViewModelProviders.of(this).get(DeviceViewModel.class);

        initIntentFilter();

        mDeviceListFragment.setOnFragmentInteractionListener(mOnFragmentActionListener);
        mMessageFragment.setOnFragmentActionListener(mActionListener);
    }

    private IntentFilter mIntentFilter;

    private void initIntentFilter(){
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.discover_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.discover_device:
                discover();
                return true;
            default:
                return false;
        }
    }

    private void discover(){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Discovered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ServerClass mServerClass;
    private ClientClass mClientClass;
    private SendReceive mSendReceive;

    public WifiP2pManager.ConnectionInfoListener mInfoListener =
            new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    final InetAddress ownerAddress = info.groupOwnerAddress;

                    if(info.groupFormed && info.isGroupOwner){
                        mServerClass = new ServerClass();
                        mServerClass.start();
                    } else if(info.groupFormed){
                        mClientClass = new ClientClass(ownerAddress);
                        mClientClass.start();
                    }
                }
            };

    public WifiP2pManager.PeerListListener mListener =
            new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                    mDeviceListFragment.add(wifiP2pDeviceList.getDeviceList());
                }
            };

    private void connect(){

        mModel.getDevice().observe(this, new Observer<WifiP2pDevice>() {
            @Override
            public void onChanged(@Nullable final WifiP2pDevice device) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        openMessageFragment();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public class ServerClass extends Thread {
        ServerSocket serverSocket;
        Socket socket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();

                mSendReceive = new SendReceive(socket);
                mSendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case READ:
                    byte[] readBuffer = (byte[]) message.obj;
                    String msg = new String(readBuffer, 0, message.arg1);
                    mMessageFragment.setMessage(msg);
                    break;
            }
            return true;
        }
    });

    private class SendReceive extends Thread {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt){
            this.socket = skt;

            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (socket != null){
                try {
                    bytes = inputStream.read(buffer);
                    if(bytes > 0){
                        mHandler.obtainMessage(READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(final byte[] bytes){
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        outputStream.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public class ClientClass extends Thread {
        Socket socket;
        String host;

        public ClientClass(InetAddress address){
            host = address.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(host, 8888), 500);
                mSendReceive = new SendReceive(socket);
                mSendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openMessageFragment(){
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.layout_main_activity, mMessageFragment, "Message");
        transaction.addToBackStack("Message");
        transaction.commit();
    }
}