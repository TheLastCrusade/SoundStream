package com.lastcrusade.fanclub.util;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.R.string;
import com.lastcrusade.fanclub.components.IDialogFormatter;
import com.lastcrusade.fanclub.components.ListViewDialog;
import com.lastcrusade.fanclub.components.IOnDialogItemClickListener;

/**
 * A generic handler for discovering devices.  This handler will accumulate discovered devices and
 * pop up a dialog to allow the user to pick the device or devices to connect to.
 * 
 * @author Jesse Rosalia
 *
 */
public class BluetoothDiscoveryHandler {

    private class BluetoothDeviceDialogFormatter implements IDialogFormatter<BluetoothDevice> {

        @Override
        public String format(BluetoothDevice device) {
            return device.getName() + " (" + device.getAddress() + ")";
        }
    }

    private static final String TAG = "BluetoothDiscoveryHandler";
    
    private final Context context;
    private final BluetoothAdapter adapter;

    private List<BluetoothDevice> discoveredDevices;

    private IOnDialogItemClickListener<BluetoothDevice> onDeviceSelectedListener;

    public BluetoothDiscoveryHandler(Context context, BluetoothAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    /**
     * Set the listener that will get called with the selected device.  This may not get called if
     * the user canceled the dialog.
     * 
     * @param onDeviceSelectedListener
     */
    public void setOnDeviceSelectedListener(IOnDialogItemClickListener<BluetoothDevice> onDeviceSelectedListener) {
        this.onDeviceSelectedListener = onDeviceSelectedListener;
    }

    /**
     * Call to indicate the start of discovery.  This MUST be called before devices are discovered.
     * 
     */
    public void onDiscoveryStarted() {
        Log.w(TAG, "Discovery started");
        this.discoveredDevices = new ArrayList<BluetoothDevice>();
    }

    /**
     * Call to indicate the end of discovery.  This MUST be called to pop up the dialog box.
     * 
     */
    public void onDiscoveryFinished() {
        Log.w(TAG, "Discovery finished");
        new ListViewDialog<BluetoothDevice>(this.context, R.string.select_device)
            .setItems(this.discoveredDevices)
            .setOnClickListener(this.onDeviceSelectedListener)
            .setFormatter(new BluetoothDeviceDialogFormatter())
            .show();
    }

    /**
     * Call to hold a discovered device for selection by the user.
     * 
     * @param device
     */
    public void onDiscoveryFound(BluetoothDevice device) {
        Log.w(TAG,
                "Device found: " + device.getName() + "(" + device.getAddress()
                        + ")");

        //only connect to devices that can support our service
        for (BluetoothDevice bonded : adapter.getBondedDevices()) {
            if (bonded.getAddress().equals(device.getAddress())) {
                Log.w(TAG, "Already paired!  Using paired device");
                device = adapter.getRemoteDevice(bonded.getAddress());
            }
        }

        this.discoveredDevices.add(device);
    }
}
