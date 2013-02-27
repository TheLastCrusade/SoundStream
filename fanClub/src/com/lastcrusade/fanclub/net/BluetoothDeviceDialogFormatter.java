package com.lastcrusade.fanclub.net;

import android.bluetooth.BluetoothDevice;

import com.lastcrusade.fanclub.components.IDialogFormatter;

public class BluetoothDeviceDialogFormatter implements IDialogFormatter<BluetoothDevice> {

    @Override
    public String format(BluetoothDevice device) {
        return device.getName() + " (" + device.getAddress() + ")";
    }
}