package com.lastcrusade.fanclub.net;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.lastcrusade.fanclub.util.BroadcastIntent;

/**
 * A generic handler for discovering devices.  This handler will accumulate discovered devices and
 * pop up a dialog to allow the user to pick the device or devices to connect to.
 * 
 * @author Jesse Rosalia
 *
 */
public class BluetoothDiscoveryHandler {

    private static final String TAG = "BluetoothDiscoveryHandler";
    
    public static final String ACTION_DISCOVERED_DEVICES = "com.lastcrusade.fanclub.net.discoveredDevices";
    public static final String EXTRA_DEVICES = "com.lastcrusade.fanclub.net.extra.devices";

    private final Context context;
    private final BluetoothAdapter adapter;

    private ArrayList<BluetoothDevice> discoveredDevices;

    public BluetoothDiscoveryHandler(Context context, BluetoothAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
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
        
        sendDiscoveredDevices();
    }

    private void sendDiscoveredDevices() {
        new BroadcastIntent(ACTION_DISCOVERED_DEVICES)
            .putParcelableArrayListExtra(EXTRA_DEVICES, this.discoveredDevices)
            .send(this.context);
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
