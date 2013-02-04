package com.lastcrusade.fanclub;

import java.io.IOException;

import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.util.Toaster;
import com.lastcrusade.fanclub.util.BluetoothUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HostActivity extends Activity {
	
	private ConnectThread connectThread;
	private MessageThread messageThread;
	
	private final String TAG = "Bluetooth_Host";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host);
		Log.w(TAG, "Create Called");
		
		final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		
		try {
		    BluetoothUtils.checkAndEnableBluetooth(this, adapter);
		} catch(BluetoothNotEnabledException e) {
		    Toaster.iToast(this, "Unable to enable bluetooth adapter");
			e.printStackTrace();
			return;
		}
		
		registerReceivers(adapter);
		
		Button button = (Button)this.findViewById(R.id.button0);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((Button)findViewById(R.id.button0)).setEnabled(false);
				Log.w(TAG, "Starting Discovery");
				if(adapter == null){
                    Toaster.iToast(HostActivity.this, "Device may not support bluetooth");
				} else {
				    adapter.startDiscovery();
				}
			}
		});
		
		button = (Button)this.findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                Toaster.iToast(HostActivity.this, "Testing stack");
			}
		});
	}
	
	private String formatSong(Song song) {
		return String.format("%s by %s on their hit album %s", song.getName(), song.getArtist(), song.getAlbum());
	}
	
	private void registerReceivers(final BluetoothAdapter adapter) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(ConnectThread.ACTION_CONNECTED);
        this.registerReceiver(new BroadcastReceiver() {
        	
        	@Override
        	public void onReceive(Context context, Intent intent) {
        		if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                    onDiscoveryStarted(adapter);
                } else if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    onDiscoveryFinished(adapter);
                } else if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                    onDeviceFound(adapter, intent);
                } else if (intent.getAction().equals(ConnectThread.ACTION_CONNECTED)) {
                    onConnected(adapter, intent);
                }
        	}
        }, filter);
	}
	
	protected void onConnected(BluetoothAdapter adapter, Intent intent) {
		Log.w(TAG, "Connected to server");
		Handler handler = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				if(msg.what == MessageThread.MESSAGE_READ) {
					onReadMessage(msg.obj.toString(), msg.arg1);
					return true;
				}
				return false;
			}
		});
	}
	
	protected void onDeviceFound(BluetoothAdapter adapter, Intent intent) {
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		Log.w(TAG, "Device found: " + device.getName() + "(" + device.getAddress() + ")");
		adapter.cancelDiscovery();
		
		for(BluetoothDevice bonded : adapter.getBondedDevices()) {
			if(bonded.getAddress().equals(device.getAddress())) {
				Log.w(TAG, "Already paired!  Using paired device");
				device = adapter.getRemoteDevice(bonded.getAddress());
			}
		}
		try {
			this.connectThread = new ConnectThread(this, device);
			this.connectThread.start();
		} catch(IOException e) {
			e.printStackTrace();
			Toaster.iToast(this, "Unable to create ConnectThread to connect to server");
		}
	}
	
	protected void onReadMessage(String string, int arg1) {
		Log.w(TAG, "Message received: " + string);
		Toaster.iToast(this, string);
	}
	
	protected void onDiscoveryFinished(BluetoothAdapter adapter) {
        Log.w(TAG, "Discovery finished");
        ((Button)findViewById(R.id.button0)).setEnabled(true);
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
