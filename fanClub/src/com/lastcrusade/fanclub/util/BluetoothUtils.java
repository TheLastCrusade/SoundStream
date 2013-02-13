package com.lastcrusade.fanclub.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.lastcrusade.fanclub.BluetoothNotEnabledException;

public class BluetoothUtils {
    /* All methods should be static */
    private final String TAG = "BluetoothUtils";

    public static void checkAndEnableBluetooth(Context context,
            BluetoothAdapter adapter) throws BluetoothNotEnabledException {
        if (adapter != null) {
            if (!adapter.isEnabled()) {
                adapter.enable();
                if (!adapter.isEnabled()) {
                    throw new BluetoothNotEnabledException();
                }
            }
        } else {
            //adapter == null means we may not support bluetooth
            Toaster.eToast(context, "Device may not support bluetooth");
        }
    }

    public static void enableDiscovery(Context context) {
        Intent discoverableIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(discoverableIntent);
    }

}
