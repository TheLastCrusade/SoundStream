package com.lastcrusade.fanclub;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;

/**
 * This thread is responsible for establishing a connection to a discovered device.
 * 
 * @author Jesse Rosalia
 */
public abstract class ConnectThread extends AsyncTask<Void, Void, BluetoothSocket> {
    private final String TAG = "ConnectThread";
    private final BluetoothDevice mmDevice;
    private Context mmContext;

    public ConnectThread(Context context, BluetoothDevice device) throws IOException {
        mmContext = context;
        mmDevice = device;
    }
    
    @Override
    protected BluetoothSocket doInBackground(Void... params) {
        BluetoothSocket socket = null;
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            // MY_UUID is the app's UUID string, also used by the server code
            UUID uuid = UUID.fromString(mmContext.getString(R.string.app_uuid));
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            socket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            socket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException closeException) {
            }
            return null;
        }

        return socket;
    }

    @Override
    protected void onPostExecute(BluetoothSocket result) {
        if (result != null) {
            onConnected(result);
        }
    }

    protected abstract void onConnected(BluetoothSocket socket);
    
//    /** Will cancel an in-progress connection, and close the socket */
//    public void cancel() {
//        try {
//            mmSocket.close();
//        } catch (IOException e) {
//        }
//    }
}