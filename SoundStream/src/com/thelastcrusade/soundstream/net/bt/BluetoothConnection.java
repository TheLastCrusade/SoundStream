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

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.thelastcrusade.soundstream.net.ConnectionReader;
import com.thelastcrusade.soundstream.net.ConnectionWriteThread;
import com.thelastcrusade.soundstream.net.ConnectionWriter;
import com.thelastcrusade.soundstream.net.MessageFuture;
import com.thelastcrusade.soundstream.net.MessageReceiver;
import com.thelastcrusade.soundstream.net.MessageEnqueuer;
import com.thelastcrusade.soundstream.net.message.IMessage;
import com.thelastcrusade.soundstream.net.wire.Messenger;

/**
 * This thread is responsible for sending and receiving messages once the connection has been established.
 * 
 * TODO: this class can be further abstracted to use just ConnectionReader, ConnectionWriter and Messenger,
 * provided we also introduce an abstraction over BluetoothSocket.  We should consider this when we consider
 * supporting new communications methods.
 * 
 * @author Jesse Rosalia
 *
 */
public abstract class BluetoothConnection extends Thread {
    private final String TAG = BluetoothConnection.class.getSimpleName();

    private final BluetoothSocket socket;
    private final Messenger messenger;

    private ConnectionWriter writer;
    private ConnectionReader reader;

    private Handler handler;

    private ConnectionWriteThread writeThread;

    private MessageEnqueuer enqueuer;

    public BluetoothConnection(Context context, BluetoothSocket socket, Handler handler) throws IOException {
        super("MessageThread-" + safeSocketName(socket));
        this.socket  = socket;
        this.handler = handler;

        this.messenger = new Messenger(context.getCacheDir());
        BluetoothDevice remoteDevice = socket.getRemoteDevice();
        //create a reader and writer, using the streams exposed by the socket
        this.reader    = new ConnectionReader(messenger, socket.getInputStream(), remoteDevice.getAddress());
        this.writer    = new ConnectionWriter(messenger, socket.getOutputStream());

        this.enqueuer    = new MessageEnqueuer(writer);
        //start a thread to manage writing...this is because the bluetooth outputstream blocks on writes
        // which will cause the UI to hang.
        this.writeThread = new ConnectionWriteThread(this.getName(), writer);
        this.writeThread.start();
    }

    //NOTE: this is static so we can call it at the top of the constructor
    private static String safeSocketName(BluetoothSocket socket) {
        return socket != null && socket.getRemoteDevice() != null ? socket.getRemoteDevice().getName() : "UnknownSocket";
    }

    public boolean isRemoteDevice(BluetoothDevice device) {
        return this.socket.getRemoteDevice().equals(device);
    }

    public void run() {
        // Keep listening to the InputStream until an exception occurs
        MessageReceiver receiver = new MessageReceiver(this.handler);
        //NOTE: if we ever need to kill this thread without catching an IOException
        // or other exception, we need to move this flag to a member variable.
        // See ConnectionWriteThread for an example.
        boolean running = true;
        try {
            while (running) {
                this.reader.readAvailable(receiver);
            }
        } catch (IOException e) {
            //in this case, an io exception probably means the connection was dropped correctly.
            Log.w(TAG, "", e);
        } finally {
            running = false;
            stopWriteThread();
            //cancel and notify the handlers that the connection is dead
            disconnect();
        }
    }
 
    private void stopWriteThread() {
        this.writeThread.stopAndWait();
    }

    public abstract void onDisconnected();

    public synchronized void cancel(int messageNo) throws IOException {
        this.writer.cancel(messageNo);
    }

    /* Call this from the main activity to send data to the remote device */
    public synchronized MessageFuture write(IMessage message) throws IOException {
        Log.d(TAG, "MessageThread#write called from " + Thread.currentThread().getName());
        return this.enqueuer.enqueueMessage(message);
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void disconnect() {
        try {
            this.socket.close();
        } catch (IOException e) {
            //NOTE: we're disconnecting...we don't really care about the error here.
        } finally {
            //before we exit, notify the launcher thread that the connection is dead
            onDisconnected();
        }
    }
}
