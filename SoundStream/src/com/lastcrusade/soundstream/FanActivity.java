package com.lastcrusade.soundstream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.net.message.IMessage;
import com.lastcrusade.soundstream.net.message.StringMessage;
import com.lastcrusade.soundstream.service.ConnectionService;
import com.lastcrusade.soundstream.service.IMessagingService;
import com.lastcrusade.soundstream.service.MessagingService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.service.ConnectionService.ConnectionServiceBinder;
import com.lastcrusade.soundstream.service.MessagingService.MessagingServiceBinder;
import com.lastcrusade.soundstream.service.ServiceLocator.IOnBindListener;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.Toaster;

public class FanActivity extends Activity {

    private final String TAG = FanActivity.class.getName();

    private BroadcastRegistrar broadcastRegistrar;

    private ServiceLocator<ConnectionService> connectionServiceLocator;
    private ServiceLocator<MessagingService>  messagingServiceLocator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan);

        connectionServiceLocator = new ServiceLocator<ConnectionService>(
                this, ConnectionService.class, ConnectionServiceBinder.class);
        
        messagingServiceLocator = new ServiceLocator<MessagingService>(
                this, MessagingService.class, MessagingServiceBinder.class);


        connectionServiceLocator.setOnBindListener(new IOnBindListener() {

            @Override
            public void onServiceBound() {
                getConnectionService().broadcastFan(FanActivity.this);
            }
        });

        Button button = (Button) this.findViewById(R.id.btn_let_me_in);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onLetMeInButtonClicked();
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

    private void registerReceivers() {
        this.broadcastRegistrar = new BroadcastRegistrar();
        this.broadcastRegistrar
            .addAction(MessagingService.ACTION_STRING_MESSAGE, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    String string = intent.getStringExtra(MessagingService.EXTRA_STRING);
                    Toaster.iToast(FanActivity.this, string);
                }
            })
//            .addAction(ConnectionService.ACTION_FIND_FINISHED, new IBroadcastActionHandler() {
//
//                    @Override
//                    public void onReceiveAction(Context context, Intent intent) {
//                        onDiscoveredDevices(intent);
//                        ((Button) findViewById(R.id.button0)).setEnabled(true);
//                    }
//                })
            .register(this);
    }

    private void unregisterReceivers() {
        this.broadcastRegistrar.unregister();
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

    protected void onLetMeInButtonClicked() {
        //initial test message
        Toaster.iToast(this, "Sending Find New Fans message");
        getMessagingService().sendFindNewFansMessage();
    }

    protected void onHelloButtonClicked() {
        //initial test message
        Toaster.iToast(this, "Sending hello message");
        String message = "You Rock! From: " + BluetoothAdapter.getDefaultAdapter().getName();
        getMessagingService().sendStringMessage(message);
    }

//    private void handleFoundFans(List<FoundFan> list) {
//        if (list.isEmpty()) {
//            Toaster.iToast(this, R.string.no_fans_found);
//        } else {
//            Toaster.iToast(this, R.string.found_fans);
//            new MultiSelectListDialog<FoundFan>(this, R.string.select_fans, R.string.connect)
//                .setItems(list)
//                .setOnClickListener(new IOnDialogItemClickListener<FoundFan>() {
//
//                    @Override
//                    public void onItemClick(FoundFan device) {
//                        
//                        ConnectFansMessage msg = new ConnectFansMessage(Arrays.asList(device.getAddress()));
//                        messageThread.write(msg);
//                    }
//                })
//                .show();
//        }
//    }

    protected void onReadMessage(int messageNo, IMessage message) {
        Log.w(TAG, "Message received: " + messageNo);
        if (message instanceof StringMessage) {
            StringMessage sm = (StringMessage)message;
            Toaster.iToast(this, sm.getString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_fan, menu);
        return true;
    }

}
