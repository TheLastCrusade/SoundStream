package com.lastcrusade.fanclub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lastcrusade.fanclub.message.FindNewFansMessage;
import com.lastcrusade.fanclub.message.IMessage;
import com.lastcrusade.fanclub.message.StringMessage;
import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.util.BluetoothUtils;
import com.lastcrusade.fanclub.util.Toaster;

public class HostActivity extends Activity {

    private ConnectThread connectThread;
    private List<MessageThread> messageThreads = new ArrayList<MessageThread>();

    private StringBuilder message = new StringBuilder();
    private Object msgMutex = new Object();

    private final String TAG = "Bluetooth_Host";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        Log.w(TAG, "Create Called");

        // TODO consider moving this entire block into BluetoothUtils, because it's also used in FanActivity
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            BluetoothUtils.checkAndEnableBluetooth(this, adapter);
        } catch (BluetoothNotEnabledException e) {
            Toaster.iToast(this, "Unable to enable bluetooth adapter");
            e.printStackTrace();
            return;
        } catch (BluetoothNotSupportedException e) {
            Toaster.eToast(this, "Device may not support bluetooth");
            e.printStackTrace();
        }
        registerReceivers(adapter);

        Button button = (Button) this.findViewById(R.id.button0);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((Button) findViewById(R.id.button0)).setEnabled(false);
                Log.w(TAG, "Starting Discovery");
                if (adapter == null) {
                    Toaster.iToast(HostActivity.this,
                            "Device may not support bluetooth");
                } else {
                    adapter.startDiscovery();
                }
            }
        });

        button = (Button) this.findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onHelloButtonClicked();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectAllSockets();
    }

    private void disconnectAllSockets() {
        for (MessageThread thread : this.messageThreads) {
            thread.cancel();
        }
    }

    private String formatSong(Song song) {
        return String.format("%s by %s on their hit album %s", song.getMetadata().getTitle(),
                song.getMetadata().getArtist(), song.getMetadata().getAlbum());
    }

    private void registerReceivers(final BluetoothAdapter adapter) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(
                        BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                    onDiscoveryStarted(adapter);
                } else if (intent.getAction().equals(
                        BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    onDiscoveryFinished(adapter);
                } else if (intent.getAction().equals(
                        BluetoothDevice.ACTION_FOUND)) {
                    onDeviceFound(adapter, intent);
                }
            }
        }, filter);
    }

    protected void onHelloButtonClicked() {
        //initial test message
        Toaster.iToast(this, "Sending test message");
        String message = "Hello, Fans.  From: " + BluetoothAdapter.getDefaultAdapter().getName();
        if(this.messageThreads.isEmpty()){
            Toaster.eToast(this, "No Fans connected");
        } else {
            StringMessage sm = new StringMessage();
            sm.setString(message);
            //send to all connected fans
            broadcastMessage(sm);
        }
    }

    private void broadcastMessage(StringMessage sm) {
        for (MessageThread thread : this.messageThreads) {
            thread.write(sm);
        }
    }

    /**
     * 
     * NOTE: must be run on the UI thread.
     * 
     * @param socket
     */
    protected void onConnectedFan(BluetoothSocket socket) {
        Log.w(TAG, "Connected to server");
        Handler handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MessageThread.MESSAGE_READ) {
                    onReadMessage(msg.arg1, (IMessage)msg.obj);
                    return true;
                }
                return false;
            }
        });
        
        socket.getRemoteDevice();
        //create the message thread, which will be responsible for reading and writing messages
        MessageThread newMessageThread = new MessageThread(socket, handler);
        newMessageThread.start();
        this.messageThreads.add(newMessageThread);
    }

    protected void onDeviceFound(BluetoothAdapter adapter, Intent intent) {
        BluetoothDevice device = intent
                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Log.w(TAG,
                "Device found: " + device.getName() + "(" + device.getAddress()
                        + ")");
//        adapter.cancelDiscovery();

        for (BluetoothDevice bonded : adapter.getBondedDevices()) {
            if (bonded.getAddress().equals(device.getAddress())) {
                Log.w(TAG, "Already paired!  Using paired device");
                device = adapter.getRemoteDevice(bonded.getAddress());
            }
        }
        try {
            //one thread per device found...if there are multiple devices,
            // there are multiple threads
            //TODO: asyncTask may not work....if the host discovers 4 devices, it appears to still only use 1 async task thread
            // and if the first 3 of those devices are not fanclub, itll pause for a while attempting to conenct, which will delay
            // connection of the actual fan
            this.connectThread = new ConnectThread(this, device) {

                @Override
                protected void onConnected(BluetoothSocket socket) {
                    onConnectedFan(socket);
                }
                
            };
            this.connectThread.execute();
        } catch (IOException e) {
            e.printStackTrace();
            Toaster.iToast(this,
                    "Unable to create ConnectThread to connect to server");
        }
    }

    
    protected void onReadMessage(int messageNo, IMessage message) {
        Log.w(TAG, "Message received: " + messageNo);
        if (message instanceof StringMessage) {
            StringMessage sm = (StringMessage)message;
            synchronized (msgMutex) {
                if (this.message.length() > 0) {
                    this.message.append("\n");
                } else {
                    startDelayedDisplayMessage();
                }
                this.message.append(sm.getString());
            }
        }

        if (message instanceof FindNewFansMessage) {
            Toaster.iToast(this, R.string.finding_new_fans);
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null) {
                Toaster.eToast(this, "Bluetooth adapter is null");
            }
            adapter.startDiscovery();
        }
    }

    private void startDelayedDisplayMessage() {
        int delayMillis = 2000; /* 2s */
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                synchronized (msgMutex) {
                    Toaster.iToast(HostActivity.this, message.toString());
                    message = new StringBuilder();
                }
            }

        }, delayMillis);
    }

    protected void onDiscoveryFinished(BluetoothAdapter adapter) {
        Log.w(TAG, "Discovery finished");
        ((Button) findViewById(R.id.button0)).setEnabled(true);
    }

    protected void onDiscoveryStarted(BluetoothAdapter adapter) {
        Log.w(TAG, "Discovery started");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_host, menu);
        return true;
    }

}
