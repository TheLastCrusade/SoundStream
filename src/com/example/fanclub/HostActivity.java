package com.example.fanclub;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class HostActivity extends Activity {
	
	private ConnectThread connectThread;
	private MessageThread messageThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fan);
		
		final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		
		try {
			checkAndEnableBluetooth(adapter);
		} catch(BluetoothNotEnabledException e) {
			Toaster.tToast(this, "Unable to enable bluetooth adapter");
			e.printStackTrace();
			return;
		}
		
		registerReceivers(adapter);
		
		Button button = (Button)this.findViewById(R.id.button0);
		button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((Button)findViewById(R.id.button0)).setEnabled(false);
                adapter.startDiscovery();
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_fan, menu);
		return true;
	}

}
