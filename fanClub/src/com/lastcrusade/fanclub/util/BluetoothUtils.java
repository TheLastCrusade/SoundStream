package com.lastcrusade.fanclub.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.lastcrusade.fanclub.BluetoothNotEnabledException;

public class BluetoothUtils {
    /* All methods should be static */

    public static void checkAndEnableBluetooth(Context context,
            BluetoothAdapter adapter) throws BluetoothNotEnabledException {
        if (adapter != null && !adapter.isEnabled()) {
            adapter.enable();
            if (!adapter.isEnabled()) {
                throw new BluetoothNotEnabledException();
            }
        } else {
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
