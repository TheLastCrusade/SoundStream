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
import android.util.Log;

import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.net.BluetoothNotEnabledException;
import com.lastcrusade.fanclub.net.BluetoothNotSupportedException;

public class BluetoothUtils {
    /* All methods should be static */
    private static final String TAG = BluetoothUtils.class.getName();
    
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
//        new BroadcastIntent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
//            .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
//            .send(context);
    }
    
    public static String getLocalBluetoothName() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        String name = null;
        if(mBluetoothAdapter!=null) {
            name = mBluetoothAdapter.getName();
            if(name == null){
                name = mBluetoothAdapter.getAddress();
            }
        }
        else{
            Log.wtf(TAG, "No Bluetooth Radio. Ok on emulator");
            name = "No Bluetooth";
        }
        return name;       
    }
}
