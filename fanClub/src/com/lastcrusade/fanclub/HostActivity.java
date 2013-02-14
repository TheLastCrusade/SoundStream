package com.lastcrusade.fanclub;

import java.io.IOException;

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
    private MessageThread messageThread;

    private final String TAG = "Bluetooth_Host";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        Log.w(TAG, "Create Called");

        // TODO consider moving this entire block into BluetoothUtils, because it's also used in FanActivity
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            BluetoothUtils.checkAndEnableBluetooth(adapter);
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
                Toaster.iToast(HostActivity.this, "Testing stack");
            }
        });
    }

    private String formatSong(Song song) {
        return String.format("%s by %s on their hit album %s", song.getName(),
                song.getArtist(), song.getAlbum());
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
        
        //create the message thread, which will be responsible for reading and writing messages
        this.messageThread = new MessageThread(socket, handler);
        this.messageThread.start();
    }

    protected void onDeviceFound(BluetoothAdapter adapter, Intent intent) {
        BluetoothDevice device = intent
                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Log.w(TAG,
                "Device found: " + device.getName() + "(" + device.getAddress()
                        + ")");
        adapter.cancelDiscovery();

        for (BluetoothDevice bonded : adapter.getBondedDevices()) {
            if (bonded.getAddress().equals(device.getAddress())) {
                Log.w(TAG, "Already paired!  Using paired device");
                device = adapter.getRemoteDevice(bonded.getAddress());
            }
        }
        try {
            //one thread per device found...if there are multiple devices,
            // there are multiple threads
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
            Toaster.iToast(this, sm.getString());
        }

        if (message instanceof FindNewFansMessage) {
            //TODO: this may need to be more robust, as discovery typically only lasts for 12 seconds
            BluetoothUtils.enableDiscovery(this);
        }
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
