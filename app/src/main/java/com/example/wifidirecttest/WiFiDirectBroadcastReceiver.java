package com.example.wifidirecttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;


public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BroadcastReceiver";

	private final WifiP2pManager manager;
    private final Channel channel;
    private final MessageLog messageLog;
    private final WifiP2pManager.PeerListListener peerListListener;
    private final WifiP2pManager.ConnectionInfoListener connectionInfoListener;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager,
                                       Channel channel,
                                       MessageLog messageLog,
                                       WifiP2pManager.PeerListListener peerListListener,
                                       WifiP2pManager.ConnectionInfoListener connectionInfoListener) {
        this.manager = manager;
        this.channel = channel;
        this.messageLog = messageLog;
        this.peerListListener = peerListListener;
        this.connectionInfoListener = connectionInfoListener;
    }

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct is enabled
            	messageLog.logMessage(TAG, "WiFi Direct enabled");
            } else {
                // Wi-Fi Direct is not enabled
            	messageLog.logMessage(TAG, "WiFi Direct not enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        	messageLog.logMessage(TAG, "Peer list changed");
        	// request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling messageLog is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                manager.requestPeers(channel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        	messageLog.logMessage(TAG, "Connection state changed");
            // Respond to new connection or disconnections
        	if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP
                manager.requestConnectionInfo(channel, connectionInfoListener);
                messageLog.logMessage(TAG, "Connected");
            } else {
                // It's a disconnect
                messageLog.logMessage(TAG, "Disconnected");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        	 messageLog.logMessage(TAG, "Wifi state changed");
        }
	}
}
