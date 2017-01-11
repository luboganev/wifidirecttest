package com.example.wifidirecttest;

import android.content.Context;
import android.graphics.Typeface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collection;

public class DevicesAdapter extends ArrayAdapter<WifiP2pDevice> {
    public DevicesAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    public void setData(Collection<WifiP2pDevice> devices) {
        clear();
        for (WifiP2pDevice wifiP2pDevice : devices) {
            add(wifiP2pDevice);
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final TextView item = (TextView) super.getView(position, convertView, parent);
        WifiP2pDevice device = getItem(position);
        //noinspection ConstantConditions
        item.setText(device.deviceName + " " + device.deviceAddress);
        item.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
        item.setTypeface(Typeface.MONOSPACE);
        return item;
    }
}
