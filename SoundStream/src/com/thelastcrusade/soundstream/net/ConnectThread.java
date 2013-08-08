/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lastcrusade.soundstream.net;

import java.io.IOException;
import java.util.UUID;

import com.lastcrusade.soundstream.R;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This thread is responsible for establishing a connection to a discovered device.
 * 
 * This will run on the discovering device, to connect to one and only one discoverable device.  This means
 * that connecting to multiple devices will require multiple instances of this thread.
 * 
 * @author Jesse Rosalia
 */
public abstract class ConnectThread extends AsyncTask<Void, Void, BluetoothSocket> {
    private final String TAG = "ConnectThread";
    private final BluetoothDevice mmDevice;
    private final Context mmContext;
    private BluetoothSocket mmSocket;

    public ConnectThread(Context context, BluetoothDevice device) throws UnableToCreateSocketException {
        mmContext = context;
        mmDevice = device;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        // MY_UUID is the app's UUID string, also used by the server code
        UUID uuid = UUID.fromString(mmContext.getString(R.string.app_uuid));
        try {
            mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            //if for some reason the socket cannot be created, throw an exception
            throw new UnableToCreateSocketException(e);
        }
    }
    
    @Override
    protected BluetoothSocket doInBackground(Void... params) {
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            Log.i(TAG, "Socket connected");
        } catch (IOException e) {
            // Unable to connect; close the socket and get out
            Log.e(TAG, "Unable to connect socket", e);
            //TODO: should probably pop something up to the user
            cancel();
        }

        return mmSocket;
    }

    @Override
    protected void onPostExecute(BluetoothSocket result) {
        //there was a successful connected socket...process the connection
        if (result != null) {
            onConnected(result);
        }
    }

    protected abstract void onConnected(BluetoothSocket socket);
    
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
            mmSocket = null;
        } catch (IOException e) {
            //Nothing to do..fall thru gracefully
        }
    }
}