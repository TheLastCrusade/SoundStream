package com.lastcrusade.fanclub;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
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
import com.lastcrusade.fanclub.util.BluetoothUtils;
import com.lastcrusade.fanclub.util.Toaster;

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
        String message = "Hello, Fans.  From: " + BluetoothAdapter.getDefaultAdapter().getName();
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

    /**
     * 
     * NOTE: must be run on the UI thread.
     * 
     * @param socket
     */
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
