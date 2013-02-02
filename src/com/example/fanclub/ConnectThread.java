package com.example.fanclub;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

/**
 * This thread is responsible for establishing a connection to a discovered device.
 * 
 * @author Jesse Rosalia
 */
public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private Context mmContext;

    public static final String ACTION_CONNECTED    = "edu.gatech.thelastcrusade.bluetooth_test.action.CONNECTED";
    public static final String ACTION_DISCONNECTED = "edu.gatech.thelastcrusade.bluetooth_test.action.DISCONNECTED";
    public static final String EXTRA_SOCKET        = "edu.gatech.thelastcrusade.bluetooth_test.extra.SOCKET";

    public ConnectThread(Context context, BluetoothDevice device) throws IOException {
        mmContext = context;
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        // MY_UUID is the app's UUID string, also used by the server code
        UUID uuid = UUID.fromString(context.getString(R.string.app_uuid));
        tmp = device.createRfcommSocketToServiceRecord(uuid);
        mmSocket = tmp;
    }

    public void run() {
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }

        notifyConnected();
    }

    public BluetoothSocket getSocket() {
        return mmSocket;
    }

    /**
     * Notify the UI or any listeners that the socket is connected 
     * 
     */
    private void notifyConnected() {
        Intent intent = new Intent();
        intent.setAction(ACTION_CONNECTED);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
        mmContext.sendBroadcast(intent);
    }

//    /**
//     * Notify the UI or any listeners that the socket is disconnected 
//     * 
//     */
//    private void notifyDisconnected() {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_DISCONNECTED);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        mmContext.sendBroadcast(intent);
//    }
//    
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}