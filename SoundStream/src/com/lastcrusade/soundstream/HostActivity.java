package com.lastcrusade.soundstream;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.components.IOnDialogItemClickListener;
import com.lastcrusade.soundstream.components.MultiSelectListDialog;
import com.lastcrusade.soundstream.net.BluetoothDeviceDialogFormatter;
import com.lastcrusade.soundstream.service.ConnectionService;
import com.lastcrusade.soundstream.service.IMessagingService;
import com.lastcrusade.soundstream.service.MessagingService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.service.ConnectionService.ConnectionServiceBinder;
import com.lastcrusade.soundstream.service.MessagingService.MessagingServiceBinder;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.Toaster;

/**
 * 
 * 
 * @author Jesse Rosalia
 *
 */
public class HostActivity extends Activity {

    private StringBuilder textBuffer = new StringBuilder();
    private Object msgMutex = new Object();

    private final String TAG = HostActivity.class.getName();
    private BroadcastRegistrar broadcastRegistrar;

    private ServiceLocator<ConnectionService> connectionServiceLocator;
    private ServiceLocator<MessagingService>  messagingServiceLocator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        Log.w(TAG, "Create Called");

        connectionServiceLocator = new ServiceLocator<ConnectionService>(
                this, ConnectionService.class, ConnectionServiceBinder.class);
        
        messagingServiceLocator = new ServiceLocator<MessagingService>(
                this, MessagingService.class, MessagingServiceBinder.class);

        Button button = (Button) this.findViewById(R.id.button0);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((Button) findViewById(R.id.button0)).setEnabled(false);
                //this service will be populated in onResume
                getConnectionService().findNewFans();
            }
        });

        button = (Button) this.findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onHelloButtonClicked();
            }
        });
        
        registerReceivers();
    }
    
    protected ConnectionService getConnectionService() {
        ConnectionService connectionService = null;
        try {
            connectionService = this.connectionServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return connectionService;
    }

    protected IMessagingService getMessagingService() {
        MessagingService messagingService = null;
        try {
            messagingService = this.messagingServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return messagingService;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getConnectionService().disconnectAllFans();
        unbindAllServices();
        unregisterReceivers();
    }

    private void unbindAllServices() {
        this.connectionServiceLocator.unbind();
        this.messagingServiceLocator.unbind();
    }

    private void registerReceivers() {
        this.broadcastRegistrar = new BroadcastRegistrar();
        this.broadcastRegistrar
            .addAction(MessagingService.ACTION_STRING_MESSAGE, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    String string = intent.getStringExtra(MessagingService.EXTRA_STRING);
                    synchronized (msgMutex) {
                        if (textBuffer.length() > 0) {
                            textBuffer.append("\n");
                        } else {
                            startDelayedDisplayMessage();
                        }
                        textBuffer.append(string);
                    }
                }
            })
            .addAction(ConnectionService.ACTION_FIND_FINISHED, new IBroadcastActionHandler() {

                    @Override
                    public void onReceiveAction(Context context, Intent intent) {
                        onDiscoveredDevices(intent);
                        ((Button) findViewById(R.id.button0)).setEnabled(true);
                    }
                })
            .register(this);
    }

    private void unregisterReceivers() {
        this.broadcastRegistrar.unregister();
    }

    protected void onHelloButtonClicked() {
        //initial test message
        Toaster.iToast(this, "Sending test message");
        String message = "Hello, Fans.  From: " + BluetoothAdapter.getDefaultAdapter().getName();
        getMessagingService().sendStringMessage(message);
    }

    private void onDiscoveredDevices(Intent intent) {
        
        //locally initiated device discovery...pop up a dialog for the user
        //TODO: this should probably use FoundFan, to decouple this code from the bluetooth code
        List<BluetoothDevice> devices = intent.getParcelableArrayListExtra(ConnectionService.EXTRA_DEVICES);
        
//            Toaster.iToast(this, R.string.found_fans);
        new MultiSelectListDialog<BluetoothDevice>(this,
                R.string.select_fans, R.string.connect)
                .setItems(devices)
                .setOnClickListener(
                        new IOnDialogItemClickListener<BluetoothDevice>() {

                            @Override
                            public void onItemClick(
                                    BluetoothDevice device) {
                                getConnectionService().connectToFan(device);
                            }
                        })
                .setFormatter(new BluetoothDeviceDialogFormatter())
                .show();
    }

    private void startDelayedDisplayMessage() {
        int delayMillis = 2000; /* 2s */
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                synchronized (msgMutex) {
                    Toaster.iToast(HostActivity.this, textBuffer.toString());
                    textBuffer = new StringBuilder();
                }
            }

        }, delayMillis);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_host, menu);
        return true;
    }
}
