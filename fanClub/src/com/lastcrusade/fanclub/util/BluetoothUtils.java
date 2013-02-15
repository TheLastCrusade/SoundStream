package com.lastcrusade.fanclub.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.lastcrusade.fanclub.BluetoothNotEnabledException;
import com.lastcrusade.fanclub.BluetoothNotSupportedException;
import com.lastcrusade.fanclub.R;

public class BluetoothUtils {
    /* All methods should be static */
    private final String TAG = "BluetoothUtils";

    public static void checkAndEnableBluetooth(Context context, BluetoothAdapter adapter) throws BluetoothNotEnabledException, BluetoothNotSupportedException {
        if (adapter != null) {
            if (!adapter.isEnabled()) {
                Toaster.iToast(context, R.string.enable_bluetooth);
                boolean enableStarted = adapter.enable();
                boolean enabled = false;
                if (enableStarted) {
                    //once the enable process is started, it takes some time to be enabled
                    int retrySeconds = 5;
                    int retryPauseMS = 500;
                    int retries = (retrySeconds * 1000) / retryPauseMS;
                    int retry = 0;
                    do {
                        try {
                            Thread.sleep(retryPauseMS);
                        } catch (InterruptedException e) {
                            //nothing to do
                        }
                        enabled = adapter.isEnabled();
                            
                    } while (!enabled && retry++ < retries);                    
                }
                //TODO: do we need to wait until it's started here?  adapter.enable just says it's "starting up"
                if (!enabled) {
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
