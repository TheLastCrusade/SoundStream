package com.lastcrusade.fanclub;

import java.io.IOException;
import java.util.UUID;

import com.lastcrusade.fanclub.util.BluetoothUtils;
import com.lastcrusade.fanclub.util.Toaster;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class FanActivity extends Activity {

    private StringBuilder message = new StringBuilder();
    private Object msgMutex = new Object();

    private final String TAG = "FanActivity";
    private BluetoothServerSocket mmServerSocket;
    private String HOST_NAME = "Patty Placeholder's party";
    protected MessageThread messageThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan);

        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            BluetoothUtils.checkAndEnableBluetooth(adapter);
        } catch (BluetoothNotEnabledException e) { // TODO This should be in
                                                   // BluetoothUtils?
            Toaster.iToast(this, "Unable to enable bluetooth adapter");
            e.printStackTrace();
            return;
        } catch (BluetoothNotSupportedException e) {
            Toaster.eToast(this, "Device may not support bluetooth");
            e.printStackTrace();
        }

        BluetoothUtils.enableDiscovery(this);

        try {
            if (adapter == null) {
                Toaster.eToast(this, "Unable to enable bluetooth adapter");
            } else {
//                mmServerSocket = adapter.listenUsingRfcommWithServiceRecord(
//                        HOST_NAME,
//                        UUID.fromString(this.getString(R.string.app_uuid)));
//                if (mmServerSocket != null) {
//                    Log.i(TAG, "Server Socket Made");
//                } else {
//                    Log.w(TAG, "Server Socket NOT made");
//                }

                AcceptThread thread = new AcceptThread(this, adapter) {

                    @Override
                    protected void onAccepted(BluetoothSocket socket) {
                        onAcceptedHost(socket);
                    }
                    
                };
                thread.execute();
            }
        } catch (IOException e) {
            Log.w(TAG, e.getStackTrace().toString());
        }
    }

    protected void onAcceptedHost(BluetoothSocket socket) {
        //disable discovery...we found our host.
        BluetoothUtils.disableDiscovery(this);
        //construct the message handler for host->fan messages
        final Handler handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MessageThread.MESSAGE_READ) {
                    onReadMessage(msg.obj.toString(), msg.arg1);
                    return true;
                }
                return false;
            }
        });

        //create the message thread for handling this connection
        this.messageThread = new MessageThread(socket, handler);
        this.messageThread.start();
    }

    protected void onReadMessage(String string, int arg1) {
        synchronized (msgMutex) {
            if (message.length() > 0) {
                message.append("\n");
            } else {
                startDelayedDisplayMessage();
            }
            message.append(string);
        }
    }

    private void startDelayedDisplayMessage() {
        int delayMillis = 2000; /* 2s */
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                synchronized (msgMutex) {
                    Toaster.iToast(FanActivity.this, message.toString());
                    message = new StringBuilder();
                }
            }

        }, delayMillis);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_fan, menu);
        return true;
    }

}
