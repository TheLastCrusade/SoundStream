/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.thelastcrusade.soundstream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.thelastcrusade.soundstream.R;
import com.thelastcrusade.soundstream.model.FoundGuest;
import com.thelastcrusade.soundstream.net.AcceptThread;
import com.thelastcrusade.soundstream.net.BluetoothDiscoveryHandler;
import com.thelastcrusade.soundstream.net.BluetoothNotEnabledException;
import com.thelastcrusade.soundstream.net.BluetoothNotSupportedException;
import com.thelastcrusade.soundstream.net.ConnectThread;
import com.thelastcrusade.soundstream.net.MessageThread;
import com.thelastcrusade.soundstream.net.MessageThreadMessageDispatch;
import com.thelastcrusade.soundstream.net.MessageThreadMessageDispatch.IMessageHandler;
import com.thelastcrusade.soundstream.net.message.ConnectGuestsMessage;
import com.thelastcrusade.soundstream.net.message.FindNewGuestsMessage;
import com.thelastcrusade.soundstream.net.message.FoundGuestsMessage;
import com.thelastcrusade.soundstream.net.message.IMessage;
import com.thelastcrusade.soundstream.service.MessagingService.MessagingServiceBinder;
import com.thelastcrusade.soundstream.util.BluetoothUtils;
import com.thelastcrusade.soundstream.util.BroadcastRegistrar;
import com.thelastcrusade.soundstream.util.IBroadcastActionHandler;
import com.thelastcrusade.soundstream.util.LocalBroadcastIntent;
import com.thelastcrusade.soundstream.util.Toaster;

public class ConnectionService extends Service {

    private static final String TAG = ConnectionService.class.getSimpleName();

    /**
     * Action to indicate find guests action has finished.  This action is sent in response to local UI initiated
     * find.
     * 
     * This uses EXTRA_DEVICES to report the devices found.
     * 
     */
    public static final String ACTION_FIND_FINISHED        = ConnectionService.class.getName() + ".action.FindFinished";

    /**
     * Action to indicate find guests action has finished.  This action is sent in response to a remote FindNewGuests message.
     * 
     * This uses EXTRA_DEVICES to report the devices found.
     * 
     */
    public static final String ACTION_REMOTE_FIND_FINISHED = ConnectionService.class.getName() + ".action.RemoteFindFinished";
    public static final String EXTRA_GUESTS                = ConnectionService.class.getName() + ".extra.Guestss";
    
    /**
     * Action to indicate the connection service is connected.  This applies both to connections to a host and to a guest.
     * 
     */
    public static final String ACTION_GUEST_CONNECTED      = ConnectionService.class.getName() + ".action.GuestConnected";
    public static final String EXTRA_GUEST_NAME            = ConnectionService.class.getName() + ".extra.GuestName";
    public static final String EXTRA_GUEST_ADDRESS         = ConnectionService.class.getName() + ".extra.GuestAddress";
    public static final String ACTION_GUEST_DISCONNECTED   = ConnectionService.class.getName() + ".action.GuestDisconected";

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

    private BluetoothAdapter    adapter;
    private List<ConnectThread> pendingConnections = new ArrayList<ConnectThread>();
    private List<MessageThread> guests             = new ArrayList<MessageThread>();
    private MessageThreadMessageDispatch     messageDispatch;
    private ServiceLocator<MessagingService> messagingServiceLocator;
    private BluetoothDiscoveryHandler        bluetoothDiscoveryHandler;

    private MessageThread discoveryInitiator;

    private BroadcastRegistrar broadcastRegistrar;

    private MessageThread host;

    @Override
    public void onCreate() {
        super.onCreate();
        
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        Log.d(TAG, "MAC Address: " + this.adapter.getAddress());

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
        registerFindNewGuestsHandler();
        registerFoundGuestsHandler();
        registerConnectGuestsHandler();
        //register a handler to route all other messages to
        // the messaging service
        registerMessagingServiceHandler();
    }

    /**
     * 
     */
    private void registerFindNewGuestsHandler() {
        this.messageDispatch.registerHandler(FindNewGuestsMessage.class, new IMessageHandler<FindNewGuestsMessage>() {

            @Override
            public void handleMessage(int messageNo,
                    FindNewGuestsMessage message, String fromAddr) {
                handleFindNewGuestsMessage(fromAddr);
            }
        });
    }

    /**
     * 
     */
    private void registerFoundGuestsHandler() {
        this.messageDispatch.registerHandler(FoundGuestsMessage.class, new IMessageHandler<FoundGuestsMessage>() {

            @Override
            public void handleMessage(int messageNo, FoundGuestsMessage message,
                    String fromAddr) {
                //send the same intent that is sent for local finds...the listening mechanism should be
                // the same for both processes.
                new LocalBroadcastIntent(ACTION_FIND_FINISHED)
                    .putParcelableArrayListExtra(ConnectionService.EXTRA_GUESTS, message.getFoundGuests())
                    .send(ConnectionService.this);
            }
        });
    }

    /**
     * 
     */
    private void registerConnectGuestsHandler() {
        this.messageDispatch.registerHandler(ConnectGuestsMessage.class, new IMessageHandler<ConnectGuestsMessage>() {

            @Override
            public void handleMessage(int messageNo, ConnectGuestsMessage message,
                    String fromAddr) {
                for (String guestAddr : message.getAddresses()) {
                    //this will always return a device for a valid address, so this is safe
                    // to do w/o checks
                    connectToGuestLocal(adapter.getRemoteDevice(guestAddr));
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
            .addGlobalAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    //if this has already been called, don't call it again (see below
                    // ...this is to handle any weird race conditions)
                    if (!bluetoothDiscoveryHandler.isDiscoveryStarted()) {
                        bluetoothDiscoveryHandler.onDiscoveryStarted(discoveryInitiator != null);
                    }
                }
            })
           .addGlobalAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    //NOTE: if we start discovery immediately after enabling bluetooth,
                    // we MAY not get the ACTION_DISCOVERY_STARTED intent...in that case,
                    // "fake it" by calling onDiscoveryStarted, so that our internal
                    // member variables are set up.  this will only happen if no guests
                    // are found, but we want to be internally consistent.
                    if (!bluetoothDiscoveryHandler.isDiscoveryStarted()) {
                        bluetoothDiscoveryHandler.onDiscoveryStarted(discoveryInitiator != null);
                    }
                    bluetoothDiscoveryHandler.onDiscoveryFinished();
                }
            })
           .addGlobalAction(BluetoothDevice.ACTION_FOUND, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    //NOTE: if we start discovery immediately after enabling bluetooth,
                    // we MAY not get the ACTION_DISCOVERY_STARTED intent...in that case,
                    // "fake it" by calling onDiscoveryStarted, so that our internal
                    // member variables are set up to record a found guest
                    if (!bluetoothDiscoveryHandler.isDiscoveryStarted()) {
                        bluetoothDiscoveryHandler.onDiscoveryStarted(discoveryInitiator != null);
                    }
                    bluetoothDiscoveryHandler.onDiscoveryFound(
                            (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                }
            })
           .addLocalAction(ConnectionService.ACTION_REMOTE_FIND_FINISHED, new IBroadcastActionHandler() {
            
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    //remote initiated device discovery...we want to send the list of devices back to the client
                    List<FoundGuest> foundGuests = intent.getParcelableArrayListExtra(ConnectionService.EXTRA_GUESTS);
                    FoundGuestsMessage msg = new FoundGuestsMessage(foundGuests);
                    try {
                        discoveryInitiator.write(msg);
                    } catch (IOException e) {
                        Log.wtf(TAG, e);
                        Toaster.eToast(ConnectionService.this, "Unable to enqueue message " + msg.getClass().getSimpleName());
                    }
                    discoveryInitiator = null; //clear the initiator to handle the next one
                }
            })
           .register(this);
    }

    private void unregisterReceivers() {
        this.broadcastRegistrar.unregister();
    }
    
    /**
     * Handle the FindNewGuestsMessage, which enables discovery on this device and
     * reports the results back to the requestor.
     * 
     * @param remoteAddr
     */
    private void handleFindNewGuestsMessage(final String remoteAddr) {
        Toaster.iToast(this, R.string.finding_new_guests);

        MessageThread found = findMessageThreadByAddress(remoteAddr);

        if (found == null) {
            Log.wtf(TAG, "Unknown remote device: " + remoteAddr);
            return;
        }

        this.discoveryInitiator = found;
        findNewGuests();
    }

    /**
     * @param remoteAddr
     * @return
     */
    private MessageThread findMessageThreadByAddress(String address) {
        // look up the message thread that manages the connection to the remote
        // device
        BluetoothDevice remoteDevice = adapter.getRemoteDevice(address);
        MessageThread found = null;
        for (MessageThread thread : this.guests) {
            if (thread.isRemoteDevice(remoteDevice)) {
                found = thread;
                break;
            }
        }
        return found;
    }

    /**
     * 
     * NOTE: must be run on the UI thread.
     * 
     * @param socket
     */
    protected void onConnectedGuest(final BluetoothSocket socket) {
        Log.w(TAG, "Connected to server");

        //create the message thread, which will be responsible for reading and writing messages
        MessageThread newMessageThread = new MessageThread(this, socket, this.messageDispatch) {

            @Override
            public void onDisconnected() {
                guests.remove(this);
                new LocalBroadcastIntent(ACTION_GUEST_DISCONNECTED)
                    .putExtra(EXTRA_GUEST_ADDRESS, socket.getRemoteDevice().getAddress())
                    .send(ConnectionService.this);
            }
        };
        newMessageThread.start();
        this.guests.add(newMessageThread);

        //announce that we're connected
        new LocalBroadcastIntent(ACTION_GUEST_CONNECTED)
            .putExtra(EXTRA_GUEST_NAME,    socket.getRemoteDevice().getName())
            .putExtra(EXTRA_GUEST_ADDRESS, socket.getRemoteDevice().getAddress())
            .send(this);
    }

    public void findNewGuests() {
        Log.w(TAG, "Starting Discovery");
        //check and enable bluetooth if needed
        try {
            BluetoothUtils.checkAndEnableBluetooth(this, adapter);
        } catch (BluetoothNotEnabledException e) {
            Toaster.iToast(this, R.string.enable_bt_fail);
            e.printStackTrace();
            return;
        } catch (BluetoothNotSupportedException e) {
            Toaster.eToast(this, R.string.no_bt_support);
            e.printStackTrace();
            return;
        }
        
        if (isHostConnected()) {
            //NOTE: this does not originate at the messaging service...consumer code doesn't need to worry about
            // how new guests are found, and this is a mechanism that applies directly to how connection
            // was implemented.
            sendMessageToHost(new FindNewGuestsMessage());
        } else {
            if (adapter == null) {
                Toaster.iToast(this, R.string.no_bt_support);
            } else {
                adapter.startDiscovery();
            }
        }
    }

    public void broadcastMessageToGuests(IMessage msg) {
        if (isGuestConnected()) {
            try {
                for (MessageThread guest : this.guests) {
                    guest.write(msg);
                }
            } catch (IOException e) {
                Log.wtf(TAG, e);
                Toaster.eToast(this, "Unable to enqueue message " + msg.getClass().getSimpleName());
            }
        } else {
            Toaster.eToast(this, R.string.no_guests_connected);
        }
    }

    public void sendMessageToGuest(String address, IMessage msg) {
        MessageThread fan = findMessageThreadByAddress(address);
        if (fan != null) {
            try {
                fan.write(msg);
            } catch (IOException e) {
                Log.wtf(TAG, e);
                Toaster.eToast(this, "Unable to enqueue message " + msg.getClass().getSimpleName());
            }
        } else {
            Toaster.eToast(this, R.string.guest_not_connected);
        }
    }

    public void sendMessageToHost(IMessage msg) {
        if (isHostConnected()) {
            try {
                this.host.write(msg);
            } catch (IOException e) {
                Log.wtf(TAG, e);
                Toaster.eToast(this, "Unable to enqueue message " + msg.getClass().getSimpleName());
            }
        } else {
            Toaster.eToast(this, R.string.no_host_connected);
        }
    }

    public boolean isHostConnected() {
        return this.host != null;
    }

    public void disconnectAllGuests() {
        //copy the contents into separate array...the cancel/disconnect procedure
        // will remove each thread from the guests array, and this will avoid
        // a ConcurrentModificationException
        List<MessageThread> toCancel = new ArrayList<MessageThread>(this.guests);
        Log.d(TAG, String.format("Disconnecting %d guests...", toCancel.size()));
        for (MessageThread thread : toCancel) {
            thread.cancel();
        }
    }

    public void disconnectHost() {
        if (host != null) {
            host.cancel();
        }
    }

    public boolean isGuestConnected() {
        return !this.guests.isEmpty();
    }

    public boolean isGuestConnected(String address) {
        return findMessageThreadByAddress(address) != null;
    }

    /**
     * NOTE: must be run on the UI thread.
     * 
     * @param device
     */
    public void connectToGuests(List<FoundGuest> foundGuests) {
        if (isHostConnected()) {
            List<String> addresses = new ArrayList<String>();
            //we need to send only the addresses back to the host
            for (FoundGuest guest : foundGuests) {
                addresses.add(guest.getAddress());
            }
            sendMessageToHost(new ConnectGuestsMessage(addresses));
        } else {
            for (FoundGuest guest : foundGuests) {
                //this will always return a device for a valid address, so this is safe
                // to do w/o checks
                connectToGuestLocal(this.adapter.getRemoteDevice(guest.getAddress()));
            }
        }
    }

    private void connectToGuestLocal(BluetoothDevice device) {
        try {
            //one thread per device found...if there are multiple devices,
            // there are multiple threads
            //TODO: asyncTask may not work....if the host discovers 4 devices, it appears to still only use 1 async task thread
            // and if the first 3 of those devices are not SoundStream, it will pause for a while attempting to connect, which will delay
            // connection of the actual guest
            ConnectThread connectThread = new ConnectThread(this, device) {

                @Override
                protected void onConnected(BluetoothSocket socket) {
                    pendingConnections.remove(this);
                    onConnectedGuest(socket);
                }
            };
            this.pendingConnections.add(connectThread);
            connectThread.execute();
        } catch (IOException e) {
            e.printStackTrace();
            Toaster.iToast(this, R.string.connect_thread_fail);
        }
    }

    /**
     * NOTE: Must be called from an activity
     * 
     * TODO: this exposes an implementation detail, namely that bluetooth discoverability
     * @param contex
     */
    public void broadcastSelfAsGuest(Context contex) {
        BluetoothUtils.enableDiscovery(contex);

        try {
            if (adapter == null) {
                Toaster.eToast(this, R.string.enable_bt_fail);
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
        this.host = new MessageThread(this, socket, this.messageDispatch) {

            @Override
            public void onDisconnected() {
                host = null;
                new LocalBroadcastIntent(ACTION_HOST_DISCONNECTED).send(ConnectionService.this);
            }
        };
        this.host.start();

        //announce that we're connected
        new LocalBroadcastIntent(ACTION_HOST_CONNECTED).send(this);
    }

    /**
     * @return
     */
    public boolean isNetworkEnabled() {
        return adapter.isEnabled();
    }

    /**
     * @return
     */
    public boolean enableNetwork() {
        boolean bluetoothEnabled = false;
        try {
            BluetoothUtils.checkAndEnableBluetooth(this, adapter);
            bluetoothEnabled = true;
        } catch (BluetoothNotEnabledException e) {
            Toaster.iToast(this.getBaseContext(),
                    R.string.enable_bt_fail);
            e.printStackTrace();
        } catch (BluetoothNotSupportedException e) {
            Toaster.eToast(this.getBaseContext(),
                    R.string.no_bt_support);
            e.printStackTrace();
        }
        return bluetoothEnabled;
    }
}
