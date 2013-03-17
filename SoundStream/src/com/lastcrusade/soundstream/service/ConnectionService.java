package com.lastcrusade.soundstream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.net.AcceptThread;
import com.lastcrusade.soundstream.net.BluetoothDiscoveryHandler;
import com.lastcrusade.soundstream.net.BluetoothNotEnabledException;
import com.lastcrusade.soundstream.net.BluetoothNotSupportedException;
import com.lastcrusade.soundstream.net.ConnectThread;
import com.lastcrusade.soundstream.net.MessageThread;
import com.lastcrusade.soundstream.net.MessageThreadMessageDispatch;
import com.lastcrusade.soundstream.net.MessageThreadMessageDispatch.IMessageHandler;
import com.lastcrusade.soundstream.net.message.ConnectFansMessage;
import com.lastcrusade.soundstream.net.message.FindNewFansMessage;
import com.lastcrusade.soundstream.net.message.FoundFan;
import com.lastcrusade.soundstream.net.message.FoundFansMessage;
import com.lastcrusade.soundstream.net.message.IMessage;
import com.lastcrusade.soundstream.service.MessagingService.MessagingServiceBinder;
import com.lastcrusade.soundstream.util.BluetoothUtils;
import com.lastcrusade.soundstream.util.BroadcastIntent;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.Toaster;

public class ConnectionService extends Service {

    private static final String TAG = ConnectionService.class.getName();

    /**
     * Action to indicate find fans action has finished.  This action is sent in response to local UI initiated
     * find.
     * 
     * This uses EXTRA_DEVICES to report the devices found.
     * 
     */
    public static final String ACTION_FIND_FINISHED        = ConnectionService.class.getName() + ".action.FindFinished";

    /**
     * Action to indicate find fans action has finished.  This action is sent in response to a remote FindNewFans message.
     * 
     * This uses EXTRA_DEVICES to report the devices found.
     * 
     */
    public static final String ACTION_REMOTE_FIND_FINISHED = ConnectionService.class.getName() + ".action.RemoteFindFinished";
    public static final String EXTRA_FANS                  = ConnectionService.class.getName() + ".extra.Fans";
    
    /**
     * Action to indicate the connection service is connected.  This applies both to connections to a host and to a fan.
     * 
     */
    public static final String ACTION_FAN_CONNECTED        = ConnectionService.class.getName() + ".action.FanConnected";
    public static final String EXTRA_FAN_NAME              = ConnectionService.class.getName() + ".extra.FanName";
    public static final String EXTRA_FAN_ADDRESS           = ConnectionService.class.getName() + ".extra.FanAddress";
    public static final String ACTION_FAN_DISCONNECTED     = ConnectionService.class.getName() + ".action.FanDisconected";

    public static final String ACTION_HOST_CONNECTED       = ConnectionService.class.getName() + ".action.HostConnected";
    public static final String ACTION_HOST_DISCONNECTED    = ConnectionService.class.getName() + ".action.HostDisconnected";

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class ConnectionServiceBinder extends Binder implements ILocalBinder<ConnectionService> {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    private BluetoothAdapter adapter;
    private List<ConnectThread> pendingConnections = new ArrayList<ConnectThread>();
    private List<MessageThread> fans     = new ArrayList<MessageThread>();
    private MessageThreadMessageDispatch messageDispatch;
    private ServiceLocator<MessagingService> messagingServiceLocator;
    private BluetoothDiscoveryHandler bluetoothDiscoveryHandler;

    private MessageThread discoveryInitiator;

    private BroadcastRegistrar broadcastRegistrar;

    private MessageThread host;

    @Override
    public void onCreate() {
        super.onCreate();
        
        this.adapter = BluetoothAdapter.getDefaultAdapter();
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
        
        registerReceivers();
        
        this.bluetoothDiscoveryHandler = new BluetoothDiscoveryHandler(this, adapter);
        this.messagingServiceLocator = new ServiceLocator<MessagingService>(
                this, MessagingService.class, MessagingServiceBinder.class);
        
        this.messageDispatch = new MessageThreadMessageDispatch();
        
        registerMessageHandlers();
    }

    /**
     * Register message handlers for the messages handled by the connection service
     * and use the default handler to dispatch other messages to the messaging service.
     */
    private void registerMessageHandlers() {
        registerFindNewFansHandler();
        registerFoundFansHandler();
        registerConnectFansHandler();
        //register a handler to route all other messages to
        // the messaging service
        registerMessagingServiceHandler();
    }

    /**
     * 
     */
    private void registerFindNewFansHandler() {
        this.messageDispatch.registerHandler(FindNewFansMessage.class, new IMessageHandler<FindNewFansMessage>() {

            @Override
            public void handleMessage(int messageNo,
                    FindNewFansMessage message, String fromAddr) {
                handleFindNewFansMessage(fromAddr);
            }
        });
    }

    /**
     * 
     */
    private void registerFoundFansHandler() {
        this.messageDispatch.registerHandler(FoundFansMessage.class, new IMessageHandler<FoundFansMessage>() {

            @Override
            public void handleMessage(int messageNo, FoundFansMessage message,
                    String fromAddr) {
                //send the same intent that is sent for local finds...the listening mechanism should be
                // the same for both processes.
                new BroadcastIntent(ACTION_FIND_FINISHED)
                    .putParcelableArrayListExtra(ConnectionService.EXTRA_FANS, message.getFoundFans())
                    .send(ConnectionService.this);
            }
        });
    }

    /**
     * 
     */
    private void registerConnectFansHandler() {
        this.messageDispatch.registerHandler(ConnectFansMessage.class, new IMessageHandler<ConnectFansMessage>() {

            @Override
            public void handleMessage(int messageNo, ConnectFansMessage message,
                    String fromAddr) {
                for (String fanAddr : message.getAddresses()) {
                    //this will always return a device for a valid address, so this is safe
                    // to do w/o checks
                    connectToFanLocal(adapter.getRemoteDevice(fanAddr));
                }
            }
        });
    }

    /**
     * Register a handler to route all unhandled messages to
     * the messaging service
     */
    private void registerMessagingServiceHandler() {
        this.messageDispatch.setDefaultHandler(new IMessageHandler<IMessage>() {

            @Override
            public void handleMessage(int messageNo, IMessage message,
                    String fromAddr) {
                try {
                    messagingServiceLocator.getService().receiveMessage(
                            messageNo, message, fromAddr);
                } catch (ServiceNotBoundException e) {
                    Log.wtf(TAG, e);
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ConnectionServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
        this.messagingServiceLocator.unbind();
    }

    private void registerReceivers() {
        this.broadcastRegistrar = new BroadcastRegistrar();
        this.broadcastRegistrar
            .addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    bluetoothDiscoveryHandler.onDiscoveryStarted(discoveryInitiator != null);
                }
            })
           .addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    bluetoothDiscoveryHandler.onDiscoveryFinished();
                }
            })
           .addAction(BluetoothDevice.ACTION_FOUND, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    bluetoothDiscoveryHandler.onDiscoveryFound(
                            (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                }
            })
           .addAction(ConnectionService.ACTION_REMOTE_FIND_FINISHED, new IBroadcastActionHandler() {
            
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    //remote initiated device discovery...we want to send the list of devices back to the client
                    List<FoundFan> foundFans = intent.getParcelableArrayListExtra(ConnectionService.EXTRA_FANS);
//                    List<FoundFan> foundFans = new ArrayList<FoundFan>();
//                    for (BluetoothDevice device : devices) {
//                        // send the found fans back to the client.
//                        foundFans.add(new FoundFan(device.getName(), device.getAddress()));
//                    }
                    FoundFansMessage msg = new FoundFansMessage(foundFans);
                    discoveryInitiator.write(msg);
                    discoveryInitiator = null; //clear the initiator to handle the next one
                }
            })
           .register(this);
    }

    private void unregisterReceivers() {
        this.broadcastRegistrar.unregister();
    }
    
    /**
     * Handle the FindNewFansMessage, which enables discovery on this device and
     * reports the results back to the requestor.
     * 
     * @param remoteAddr
     */
    private void handleFindNewFansMessage(final String remoteAddr) {
        Toaster.iToast(this, R.string.finding_new_fans);

        //NOTE: we assume that the adapter is nonnull, because the activity will not
        // get past onCreate on a device w/o Bluetooth...and also, because this method is
        // called in response to a network message over Bluetooth
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        // look up the message thread that manages the connection to the remote
        // device
        BluetoothDevice remoteDevice = adapter.getRemoteDevice(remoteAddr);
        MessageThread found = null;
        for (MessageThread thread : this.fans) {
            if (thread.isRemoteDevice(remoteDevice)) {
                found = thread;
                break;
            }
        }

        if (found == null) {
            Log.wtf(TAG, "Unknown remote device: " + remoteAddr);
            return;
        }

        this.discoveryInitiator = found;
        findNewFans();
    }

    /**
     * 
     * NOTE: must be run on the UI thread.
     * 
     * @param socket
     */
    protected void onConnectedFan(final BluetoothSocket socket) {
        Log.w(TAG, "Connected to server");

        //create the message thread, which will be responsible for reading and writing messages
        MessageThread newMessageThread = new MessageThread(socket, this.messageDispatch, ACTION_FAN_DISCONNECTED) {

            @Override
            public void onDisconnected() {
                fans.remove(this);
                new BroadcastIntent(ACTION_FAN_DISCONNECTED)
                    .putExtra(EXTRA_FAN_ADDRESS, socket.getRemoteDevice().getAddress())
                    .send(ConnectionService.this);
            }
        };
        newMessageThread.start();
        this.fans.add(newMessageThread);

        //announce that we're connected
        new BroadcastIntent(ACTION_FAN_CONNECTED)
            .putExtra(EXTRA_FAN_NAME,    socket.getRemoteDevice().getName())
            .putExtra(EXTRA_FAN_ADDRESS, socket.getRemoteDevice().getAddress())
            .send(this);
    }

    public void findNewFans() {
        Log.w(TAG, "Starting Discovery");
        if (isHostConnected()) {
            //NOTE: this does not originate at the messaging service...consumer code doesn't need to worry about
            // how new fans are found, and this is a mechanism that applies directly to how connection
            // was implemented.
            sendMessageToHost(new FindNewFansMessage());
        } else {
            if (adapter == null) {
                Toaster.iToast(this,
                        "Device may not support bluetooth");
            } else {
                adapter.startDiscovery();
            }
        }
    }

    public void broadcastMessageToFans(IMessage msg) {
        if (isFanConnected()) {
            for (MessageThread fan : this.fans) {
                fan.write(msg);
            }
        } else {
            Toaster.eToast(this, "No Fans connected");
        }
    }

    public void sendMessageToHost(IMessage msg) {
        if (isHostConnected()) {
            this.host.write(msg);
        } else {
            Toaster.eToast(this, "Not connected to host");
        }
    }

    public boolean isHostConnected() {
        return this.host != null;
    }

    public void disconnectAllFans() {
        for (MessageThread thread : this.fans) {
            thread.cancel();
        }
    }

    public boolean isFanConnected() {
        return !this.fans.isEmpty();
    }

    /**
     * NOTE: must be run on the UI thread.
     * 
     * @param device
     */
    public void connectToFans(List<FoundFan> foundFans) {
        if (isHostConnected()) {
            List<String> addresses = new ArrayList<String>();
            //we need to send only the addresses back to the host
            for (FoundFan fan : foundFans) {
                addresses.add(fan.getAddress());
            }
            sendMessageToHost(new ConnectFansMessage(addresses));
        } else {
            for (FoundFan fan : foundFans) {
                //this will always return a device for a valid address, so this is safe
                // to do w/o checks
                connectToFanLocal(this.adapter.getRemoteDevice(fan.getAddress()));
            }
        }
    }

    private void connectToFanLocal(BluetoothDevice device) {
        try {
            //one thread per device found...if there are multiple devices,
            // there are multiple threads
            //TODO: asyncTask may not work....if the host discovers 4 devices, it appears to still only use 1 async task thread
            // and if the first 3 of those devices are not SoundStream, it will pause for a while attempting to connect, which will delay
            // connection of the actual fan
            ConnectThread connectThread = new ConnectThread(this, device) {

                @Override
                protected void onConnected(BluetoothSocket socket) {
                    pendingConnections.remove(this);
                    onConnectedFan(socket);
                }
                
            };
            this.pendingConnections.add(connectThread);
            connectThread.execute();
        } catch (IOException e) {
            e.printStackTrace();
            Toaster.iToast(this,
                    "Unable to create ConnectThread to connect to server");
        }
    }

    /**
     * NOTE: Must be called from an activity
     * 
     * TODO: this exposes an implementation detail, namely that bluetooth discoverability
     * @param activity
     */
    public void broadcastFan(Activity activity) {
        BluetoothUtils.enableDiscovery(activity);

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
        this.host = new MessageThread(socket, this.messageDispatch, ACTION_HOST_DISCONNECTED) {

            @Override
            public void onDisconnected() {
                host = null;
                new BroadcastIntent(ACTION_HOST_DISCONNECTED).send(ConnectionService.this);
            }
        };
        this.host.start();

        //announce that we're connected
        new BroadcastIntent(ACTION_HOST_CONNECTED).send(this);
    }
}
