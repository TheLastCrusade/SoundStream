package com.lastcrusade.fanclub.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.lastcrusade.fanclub.BluetoothNotEnabledException;

public class BluetoothUtils {

    public static void checkAndEnableBluetooth(Context context, BluetoothAdapter adapter)
            throws BluetoothNotEnabledException {
        if (adapter != null && !adapter.isEnabled()) {
            adapter.enable();
            if (!adapter.isEnabled()) {
                throw new BluetoothNotEnabledException();
            }
        } else {
            Toaster.eToast(context, "Device may not support bluetooth");
        }
    }

}
