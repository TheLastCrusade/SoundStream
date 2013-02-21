package com.lastcrusade.fanclub.util;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;

import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.net.BluetoothNotEnabledException;
import com.lastcrusade.fanclub.net.BluetoothNotSupportedException;

public class BluetoothUtils {
    /* All methods should be static */
    private static final String TAG = "BluetoothUtils";
    
    /* The maximum number of seconds in which to wait for bluetooth to enable */
    private static final int ENABLE_CHECK_RETRY_SECONDS = 5;
    
    /* The number of milliseconds to wait in between calls to isEnabled */
    private static final int ENABLE_CHECK_RETRY_PAUSE_MILLISECONDS = 500;

    public static void checkAndEnableBluetooth(Context context, BluetoothAdapter adapter) throws BluetoothNotEnabledException, BluetoothNotSupportedException {
        if (adapter != null) {
            if (!adapter.isEnabled()) {
                Toaster.iToast(context, R.string.enable_bluetooth);
                //start the adapter
                boolean enableStarted = adapter.enable();
                boolean enabled = false;
                //we need to wait a bit for the enable to take effect...in this case, we poll the adapter's isEnabled method
                // after some delay and check to see if the adapter has been enabled by a set amount of time
                //NOTE: these constant times were picked somewhat randomly...the process should never take more than 5 seconds, and
                // a 500ms delay between polls should be ok in this case.
                if (enableStarted) {
                    //once the enable process is started, it takes some time to be enabled
                    int retries = (ENABLE_CHECK_RETRY_SECONDS * 1000) / ENABLE_CHECK_RETRY_PAUSE_MILLISECONDS;
                    int retry = 0;
                    do {
                        try {
                            Thread.sleep(ENABLE_CHECK_RETRY_PAUSE_MILLISECONDS);
                        } catch (InterruptedException e) {
                            //nothing to do
                        }
                        enabled = adapter.isEnabled();
                            
                    } while (!enabled && retry++ < retries);                    
                }
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

    /**
     * Get the UUIDs supported by this device.
     * 
     * NOTE: if calling this method on discovery, call this with the discovered
     * device object, not the bonded device object.  The discovered device object
     * will accurately show the services supported at that time.
     * 
     * @param device
     * @return A list of UUIDs supported on the remote system.  Note that this may be empty (but should never be null).
     */
    public static List<UUID> getUuidsForDevice(BluetoothDevice device) {
        try {
            //NOTE: this uses an undocumented method call, because the documented call
            // doesn't show up until API 15
            Method method = device.getClass().getMethod("getUuids", null);
            ParcelUuid[] puuids = (ParcelUuid[]) method.invoke(device, null);
            //transform into a simple list
            List<UUID> supportedUuids = new LinkedList<UUID>();
            if (puuids != null) {
                for (ParcelUuid puuid : puuids) {
                    supportedUuids.add(puuid.getUuid());
                }
            }
            return supportedUuids;
        } catch (Exception e) {
            //FIXME: 
            throw new RuntimeException(e);
        }
    }

}
