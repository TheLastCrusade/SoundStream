package com.lastcrusade.fanclub.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.lastcrusade.fanclub.BluetoothNotEnabledException;
import com.lastcrusade.fanclub.BluetoothNotSupportedException;

public class BluetoothUtils {
    /* All methods should be static */
    private final String TAG = "BluetoothUtils";

    public static void checkAndEnableBluetooth(BluetoothAdapter adapter) throws BluetoothNotEnabledException, BluetoothNotSupportedException {
        if (adapter != null) {
            if (!adapter.isEnabled()) {
                adapter.enable();
                //TODO: do we need to wait until it's started here?  adapter.enable just says it's "starting up"
                if (!adapter.isEnabled()) {
                    throw new BluetoothNotEnabledException();
                }
            }
        } else {
            //adapter == null means we may not support bluetooth
            throw new BluetoothNotSupportedException();
        }
    }

    public static void disableDiscovery(Context context) {
        //TODO: this
    }

    public static void enableDiscovery(Context context) {
        Intent discoverableIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(discoverableIntent);
    }

}
