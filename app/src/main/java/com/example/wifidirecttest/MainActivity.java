package com.example.wifidirecttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MessageLog,
        PeerListListener, ConnectionInfoListener {

    private static final String TAG = "Main";

    @BindView(R.id.logMessagesList) ListView logMessagesList;
    @BindView(R.id.devicesList) ListView devicesList;

    private LogMessagesAdapter logAdapter;
    private DevicesAdapter devicesAdapter;
    private WifiP2pManager wifiP2pManager;
    private Channel channel;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        logAdapter = new LogMessagesAdapter(getApplicationContext());
        logMessagesList.setAdapter(logAdapter);

        devicesList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiP2pDevice device = devicesAdapter.getItem(position);
                connectToDevice(device);
            }
        });
        devicesAdapter = new DevicesAdapter(getApplicationContext());
        devicesList.setAdapter(devicesAdapter);

        // Init WiFiDirect
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, channel, this, this, this);

        // Init intent filters
        initWiFiP2PIntentFilter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        logMessage(TAG, "onPeersAvailable Callback received");
        devicesAdapter.setData(peers.getDeviceList());
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if (info.isGroupOwner) {
            logMessage(TAG, "I'm the host:" + info.groupOwnerAddress.getHostAddress());
        } else {
            logMessage(TAG, "Group owner host:" + info.groupOwnerAddress.getHostAddress());
        }
    }

    @Override
    public void logMessage(@Nullable String tag, @NonNull String message) {
        final String logMessage = tag != null ? tag + ": " + message : message;
        logAdapter.add(logMessage);
    }

    @OnClick(R.id.discoverDevicesButton)
    void discoverDevices() {
        logMessage(TAG, "Discovering...");
        devicesAdapter.clear();
        wifiP2pManager.discoverPeers(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                logMessage(TAG, "Discovery onSuccess");
            }

            @Override
            public void onFailure(int reasonCode) {
                logMessage(TAG, "Discovery onFailure reason:" + reasonCode);
            }
        });
    }

    @OnClick(R.id.disconnectButton)
    void disconnect() {
        wifiP2pManager.cancelConnect(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                logMessage(TAG, "Cancel connect onSuccess");
            }

            @Override
            public void onFailure(int reasonCode) {
                logMessage(TAG, "Cancel connect onFailure reason:" + reasonCode);
            }
        });
        wifiP2pManager.removeGroup(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                logMessage(TAG, "Remove group onSuccess");
            }

            @Override
            public void onFailure(int reasonCode) {
                logMessage(TAG, "Remove group onFailure reason:" + reasonCode);
            }
        });
    }

    @OnClick(R.id.clearLogButton)
    void clearLogMessages() {
        logAdapter.clear();
    }

    private void initWiFiP2PIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void connectToDevice(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        //noinspection ConstantConditions
        config.deviceAddress = device.deviceAddress;
        wifiP2pManager.connect(channel, config, new ActionListener() {
            @Override
            public void onSuccess() {
                logMessage(TAG, "Connect onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                logMessage(TAG, "Connect onFailure reason:" + reason);
            }
        });
    }
}
