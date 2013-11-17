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

package com.thelastcrusade.soundstream.net.bt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.thelastcrusade.soundstream.net.ConnectionWriteThread;
import com.thelastcrusade.soundstream.net.IConnectionInternal;
import com.thelastcrusade.soundstream.net.ConnectionReader;
import com.thelastcrusade.soundstream.net.ConnectionWriter;
import com.thelastcrusade.soundstream.net.message.IMessage;
import com.thelastcrusade.soundstream.net.wire.Messenger;

/**
 * This thread is responsible for sending and receiving messages once the connection has been established.
 * 
 * 
 * @author Jesse Rosalia
 *
 */
public abstract class BluetoothConnection extends Thread implements IConnectionInternal {
    private final String TAG = BluetoothConnection.class.getSimpleName();

    private final BluetoothSocket mmSocket;
    private final Messenger mmMessenger;

    private ConnectionWriter mmWriter;
    private ConnectionReader mmReader;

    private int mmInMessageNumber  = 1;
    private int mmOutMessageNumber = 1;


    private Handler mmHandler;

    private ConnectionWriteThread mmWriteThread;

    public BluetoothConnection(Context context, BluetoothSocket socket, Handler handler) throws IOException {
        super("MessageThread-" + safeSocketName(socket));
        mmSocket  = socket;

        mmMessenger = new Messenger(context.getCacheDir());
        BluetoothDevice remoteDevice = mmSocket.getRemoteDevice();
        //create a reader and writer, using the streams exposed by the socket
        mmReader    = new ConnectionReader(mmMessenger, socket.getInputStream(),  this, remoteDevice.getAddress());
        mmWriter    = new ConnectionWriter(mmMessenger, socket.getOutputStream(), this, remoteDevice.getAddress());

        //start a thread to manage writing...this is because the bluetooth outputstream blocks on writes
        // which will cause the UI to hang.
        mmWriteThread = new ConnectionWriteThread(this.getName(), mmWriter);
        mmWriteThread.start();
    }

    //NOTE: this is static so we can call it at the top of the constructor
    private static String safeSocketName(BluetoothSocket socket) {
        return socket != null && socket.getRemoteDevice() != null ? socket.getRemoteDevice().getName() : "UnknownSocket";
    }

    public boolean isRemoteDevice(BluetoothDevice device) {
        return mmSocket.getRemoteDevice().equals(device);
    }

    public void run() {
        // Keep listening to the InputStream until an exception occurs
        while (true) { //TODO: need way to kill this thread normally
            try {
                mmReader.readAvailable();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (Throwable t) {
                t.printStackTrace();
                break;
            }
        }
        stopWriteThread();
        //cancel and notify the handlers that the connection is dead
        disconnect();
    }
 
    private void stopWriteThread() {
        mmWriteThread.stopAndWait();
    }

    public abstract void onDisconnected();

    /* Call this from the main activity to send data to the remote device */
    public synchronized void write(IMessage message) throws IOException {
        Log.d(TAG, "MessageThread#write called from " + Thread.currentThread().getName());
        //enqueue this message
        mmWriter.enqueue(mmOutMessageNumber++, message);
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void disconnect() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            
        } finally {
            //before we exit, notify the launcher thread that the connection is dead
            onDisconnected();
        }
    }
    
    /**
     * Send the network message to the appropriate handler.
     * 
     * @param message
     * @param remoteAddr
     */
    public void messageReceived(IMessage message, String remoteAddr) {
        Message androidMsg = mmHandler.obtainMessage(MESSAGE_READ, this.mmInMessageNumber++, 0, message);
        Bundle bundle = new Bundle();
        bundle.putString(IConnectionInternal.EXTRA_ADDRESS, remoteAddr);
        androidMsg.setData(bundle);
        androidMsg.sendToTarget();
    }
    
    public void messageTransferFinished(int messageNo) {
        
    }
}