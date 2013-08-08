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

package com.thelastcrusade.soundstream.net;

import java.io.IOException;
import java.util.UUID;

import com.thelastcrusade.soundstream.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Listen for incoming connections for a specific service (identified by the UUID).
 * 
 * This runs on the discoverable device, to accept connections from discovering devices.
 * 
 * @author Jesse Rosalia
 *
 */
public abstract class AcceptThread extends AsyncTask<Void, Void, BluetoothSocket> {
    private final String TAG = "AcceptThread";
    private final BluetoothServerSocket mmServerSocket;
    private String HOST_NAME = "Patty Placeholder's party";
    
    public AcceptThread(Context context, BluetoothAdapter adapter) throws UnableToCreateSocketException {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            //TODO: for scaling to large number of users, we may have to use different UUIDs
            mmServerSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(
                    HOST_NAME,
                    UUID.fromString(context.getString(R.string.app_uuid)));
        } catch (IOException e) {
            throw new UnableToCreateSocketException(e);
        }
    }
 
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }

    @Override
    protected BluetoothSocket doInBackground(Void... params) {
        BluetoothSocket socket = null;
        try {
            socket = mmServerSocket.accept();
            Log.i(TAG, "Connection accepted");
        } catch (IOException e) {
            Log.e(TAG, "Unable to accept connection", e);
        } finally {
            cancel();
        }
        return socket;
    }

    @Override
    protected void onPostExecute(BluetoothSocket result) {
        if (result != null) {
            onAccepted(result);
        }
    }

    protected abstract void onAccepted(BluetoothSocket socket);
    
}
