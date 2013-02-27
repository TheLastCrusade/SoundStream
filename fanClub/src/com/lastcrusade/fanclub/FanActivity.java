package com.lastcrusade.fanclub;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lastcrusade.fanclub.components.IOnDialogItemClickListener;
import com.lastcrusade.fanclub.components.MultiSelectListDialog;
import com.lastcrusade.fanclub.net.AcceptThread;
import com.lastcrusade.fanclub.net.BluetoothDeviceDialogFormatter;
import com.lastcrusade.fanclub.net.BluetoothNotEnabledException;
import com.lastcrusade.fanclub.net.BluetoothNotSupportedException;
import com.lastcrusade.fanclub.net.MessageThread;
import com.lastcrusade.fanclub.net.MessageThreadMessageDispatch;
import com.lastcrusade.fanclub.net.MessageThreadMessageDispatch.IMessageHandler;
import com.lastcrusade.fanclub.net.message.ConnectFansMessage;
import com.lastcrusade.fanclub.net.message.FindNewFansMessage;
import com.lastcrusade.fanclub.net.message.FoundFan;
import com.lastcrusade.fanclub.net.message.FoundFansMessage;
import com.lastcrusade.fanclub.net.message.IMessage;
import com.lastcrusade.fanclub.net.message.StringMessage;
import com.lastcrusade.fanclub.util.BluetoothUtils;
import com.lastcrusade.fanclub.util.Toaster;

public class FanActivity extends Activity {

    private final String TAG = "FanActivity";
    private BluetoothServerSocket mmServerSocket;
    private String HOST_NAME = "Patty Placeholder's party";
    protected MessageThread messageThread;
    private MessageThreadMessageDispatch messageDispatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan);

        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            BluetoothUtils.checkAndEnableBluetooth(this, adapter);
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
        
        registerMessageHandlers();
    }

    private void registerMessageHandlers() {
        this.messageDispatch = new MessageThreadMessageDispatch();
        this.messageDispatch.registerHandler(StringMessage.class, new IMessageHandler<StringMessage>() {

            @Override
            public void handleMessage(int messageNo,
                    StringMessage message, String fromAddr) {
                StringMessage sm = (StringMessage)message;
                Toaster.iToast(FanActivity.this, sm.getString());
            }
            
        });
        this.messageDispatch.registerHandler(FoundFansMessage.class, new IMessageHandler<FoundFansMessage>() {

            @Override
            public void handleMessage(int messageNo,
                    FoundFansMessage message, String fromAddr) {
                handleFoundFans(message.getFoundFans());
            }
        });
    }

    protected void onLetMeInButtonClicked() {
        //initial test message
        Toaster.iToast(this, "Sending Find New Fans message");
        FindNewFansMessage msg = new FindNewFansMessage();
        //send the message to the host
        sendMessage(msg);
    }

    protected void onHelloButtonClicked() {
        //initial test message
        Toaster.iToast(this, "Sending hello message");
        String message = "You Rock! From: " + BluetoothAdapter.getDefaultAdapter().getName();
        StringMessage sm = new StringMessage();
        sm.setString(message);
        //send the message to the host
        sendMessage(sm);
    }

    /**
     * Helper method to send a message to the host or raise an error
     * to the user.
     * 
     * @param msg
     */
    private void sendMessage(IMessage msg) {
        if(this.messageThread == null){
            Toaster.eToast(this, "Not connected to host");
        } else {
            this.messageThread.write(msg);
        }        
    }

    private void handleFoundFans(List<FoundFan> list) {
        if (list.isEmpty()) {
            Toaster.iToast(this, R.string.no_devices_discovered);
        } else {
            new MultiSelectListDialog<FoundFan>(this, R.string.select_fans, R.string.connect)
                .setItems(list)
                .setOnClickListener(new IOnDialogItemClickListener<FoundFan>() {

                    @Override
                    public void onItemClick(FoundFan device) {
                        ConnectFansMessage msg = new ConnectFansMessage(Arrays.asList(device.getAddress()));
                        messageThread.write(msg);
                    }
                })
                .show();
        }
    }

    /**
     * 
     * NOTE: must be run on the UI thread.
     * 
     * @param socket
     */
    protected void onAcceptedHost(BluetoothSocket socket) {
        //disable discovery...we found our host.
        BluetoothUtils.disableDiscovery(this);

        //create the message thread for handling this connection
        this.messageThread = new MessageThread(socket, this.messageDispatch);
        this.messageThread.start();
    }

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
