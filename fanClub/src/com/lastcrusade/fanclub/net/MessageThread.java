package com.lastcrusade.fanclub.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lastcrusade.fanclub.net.message.IMessage;
import com.lastcrusade.fanclub.net.message.Messenger;

/**
 * This thread is responsible for sending and receiving messages once the connection has been established.
 * 
 * 
 * @author Jesse Rosalia
 *
 */
public abstract class MessageThread extends Thread {
    private final String TAG = MessageThread.class.getName();
    public static final int MESSAGE_READ = 1;

    public static final String EXTRA_ADDRESS = MessageThread.class.getName() + ".extra.Address";

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
 
    private int   messageNumber = 0;
    private Handler mmHandler;

    private String mmDisconnectAction;
    //NOTE: Messenger is stateless
    private final Messenger mmMessenger;
    public MessageThread(BluetoothSocket socket, Handler handler, String disconnectAction) {
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
        
        mmDisconnectAction = disconnectAction;
        
        mmMessenger = new Messenger();
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
                    //dispatch the message to the 
                    sendMessageToHandler(mmMessenger.getReceivedMessage(), remoteDevice.getAddress());
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (Throwable t) {
                t.printStackTrace();
                break;
            }
        }
        //cancel and notify the handlers that the connection is dead
        cancel();
    }
 
    public abstract void onDisconnected();

    /**
     * Send the network message to the appropriate handler.
     * 
     * @param message
     * @param remoteAddr
     */
    private void sendMessageToHandler(IMessage message, String remoteAddr) {
        Message androidMsg = mmHandler.obtainMessage(MESSAGE_READ, this.messageNumber, 0, message);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ADDRESS, remoteAddr);
        androidMsg.setData(bundle);
        androidMsg.sendToTarget();
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(IMessage message) {
        try {
            mmMessenger.serializeMessage(message);
            byte[] bytes = mmMessenger.getOutputBytes();
            mmOutStream.write(bytes);
            mmMessenger.clearOutputBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
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