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
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.thelastcrusade.soundstream.net.message.IMessage;
import com.thelastcrusade.soundstream.net.wire.Messenger;

/**
 * This thread is responsible for sending and receiving messages once the connection has been established.
 * 
 * 
 * @author Jesse Rosalia
 *
 */
public abstract class MessageThread extends Thread {
    private final String TAG = MessageThread.class.getSimpleName();
    public static final int MESSAGE_READ = 1;

    public static final String EXTRA_ADDRESS = MessageThread.class.getName() + ".extra.Address";

    /**
     * Let canceled messages live for 5 min, to allow clients to fully process the message.
     * NOTE 5 min should definitely be overkill, but the process is low overhead,
     * and we can use this functionality in the future for partial transfers/restarts.
     */
    private static final int CANCELED_MESSAGES_TTL_MINUTES = 5;
    
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
 
    private int     mmInMessageNumber = 1;
    private int     mmOutMessageNumber = 1;

    private Handler mmHandler;

    //NOTE: Messenger is stateless
    private final Messenger mmMessenger;
    private MessageThreadWriter mmWriter;
    private Thread mmWriteThread;
    protected boolean mmWriteThreadRunning;
    private Thread mmStoppingThread;

    public MessageThread(Context context, BluetoothSocket socket, Handler handler) {
        super("MessageThread-" + safeSocketName(socket));
        mmSocket  = socket;
        mmHandler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        
        mmMessenger = new Messenger(context.getCacheDir(), CANCELED_MESSAGES_TTL_MINUTES);
        mmWriter    = new MessageThreadWriter(mmMessenger, mmOutStream);
        mmWriteThreadRunning = true;
        mmWriteThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (mmWriteThreadRunning) {
                        try {
                            mmWriter.writeOne();
                            if (!mmWriter.canWrite()) {
                                Thread.sleep(10); //give the system a chance to breath
                            }
                        } catch (IOException e) {
                            //we've probably closed our socket...quit the thread
                            mmWriteThreadRunning = false;
                            //log an error
                            Log.wtf(TAG, e);
                        } catch (InterruptedException e) {
                            //nothing to do
                        }
                    }
                } finally {
                    if (mmStoppingThread != null) {
                        synchronized(mmStoppingThread) {
                            mmStoppingThread.notify();
                        }
                    }
                }
            }
        }, this.getName() + " Writer");
        
        //start a thread to manage writing...this is because the bluetooth outputstream blocks on writes
        // which will cause the UI to hang.
        mmWriteThread.start();
    }
 
    public boolean isRemoteDevice(BluetoothDevice device) {
        return mmSocket.getRemoteDevice().equals(device);
    }

    private static String safeSocketName(BluetoothSocket socket) {
        return socket != null && socket.getRemoteDevice() != null ? socket.getRemoteDevice().getName() : "UnknownSocket";
    }

    public void run() {
        // Keep listening to the InputStream until an exception occurs
        BluetoothDevice remoteDevice = mmSocket.getRemoteDevice();
        while (true) { //TODO: need way to kill this thread normally
            try {
                //attempt to deserialize from the socket input stream
                boolean messageRecvd = mmMessenger.deserializeMessage(mmInStream);
                if (messageRecvd) {
                    for (IMessage message : mmMessenger.getReceivedMessages()) {
                        //dispatch the message to the handler
                        sendMessageToHandler(message, remoteDevice.getAddress());
                    }
                    
                    mmMessenger.clearCanceledMessagesAfter(CANCELED_MESSAGES_TTL_MINUTES);
                    mmMessenger.clearReceivedMessages();
                }
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
        cancel();
    }
 
    private void stopWriteThread() {
        mmStoppingThread = Thread.currentThread();
        mmWriteThreadRunning = false;
        synchronized(mmStoppingThread) {
            try {
                //wait for the thread to stop, or for 1 second.  This number may have to be adjusted
                // as we test with larger and larger files.
                int waitTimeInMS = 1000;
                mmStoppingThread.wait(waitTimeInMS);
            } catch (InterruptedException e) {
                //fall thru, nothing to do
            }
            if (mmWriteThread.isAlive()) {
                Log.w(TAG, "Write thread is still alive...");
            }
        }
    }

    public abstract void onDisconnected();

    /**
     * Send the network message to the appropriate handler.
     * 
     * @param message
     * @param remoteAddr
     */
    private void sendMessageToHandler(IMessage message, String remoteAddr) {
        Message androidMsg = mmHandler.obtainMessage(MESSAGE_READ, this.mmInMessageNumber++, 0, message);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ADDRESS, remoteAddr);
        androidMsg.setData(bundle);
        androidMsg.sendToTarget();
    }

    /* Call this from the main activity to send data to the remote device */
    public synchronized void write(IMessage message) throws IOException {
        Log.d(TAG, "MessageThread#write called from " + Thread.currentThread().getName());
        //enqueue this message
        mmWriter.enqueue(mmOutMessageNumber++, message);
    }
 
    public synchronized void cancelMessage(IMessage message) throws IOException {
        
    }
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            
        } finally {
            //before we exit, notify the launcher thread that the connection is dead
            onDisconnected();
        }
    }
}