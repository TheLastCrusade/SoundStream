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

package com.lastcrusade.soundstream.components;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.lastcrusade.soundstream.CoreActivity;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.service.ConnectionService;
import com.lastcrusade.soundstream.service.ConnectionService.ConnectionServiceBinder;
import com.lastcrusade.soundstream.service.IMessagingService;
import com.lastcrusade.soundstream.service.MessagingService;
import com.lastcrusade.soundstream.service.MessagingService.MessagingServiceBinder;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.ITitleable;
import com.lastcrusade.soundstream.util.Transitions;

/*
 * This fragment should be what is first presented to the user when
 * they enter the app and are not connected to any network
 */
public class ConnectFragment extends SherlockFragment implements ITitleable{
    
    private static final String TAG = ConnectFragment.class.getName();

    private BroadcastRegistrar broadcastRegistrar;
    private Button connectButton;

    private ServiceLocator<ConnectionService> connectionServiceLocator;

    private ServiceLocator<MessagingService> messagingServiceLocator;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionServiceLocator = new ServiceLocator<ConnectionService>(
                this.getActivity(), ConnectionService.class, ConnectionServiceBinder.class);
        
        messagingServiceLocator = new ServiceLocator<MessagingService>(
                this.getActivity(), MessagingService.class, MessagingServiceBinder.class);
        
        registerReceivers();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connect, container, false);

        Button create = (Button)v.findViewById(R.id.btn_create);
        create.setOnClickListener( new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Transitions.transitionToNetwork((CoreActivity)getActivity());
                ((CoreActivity)getActivity()).enableSlidingMenu();
                
                //add the playbar fragment onto the active content view
                ((CoreActivity)getActivity()).showPlaybar();
            }
        });
        
        this.connectButton = (Button)v.findViewById(R.id.btn_connect);
        this.connectButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getConnectionService().broadcastGuest(getActivity());
            }
        });

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(getTitle());
    }
    
    @Override
    public void onDestroy() {
        unregisterReceivers();
        
        this.connectionServiceLocator.unbind();
        this.messagingServiceLocator.unbind();
        super.onDestroy();
    }
    
    private ConnectionService getConnectionService() {
        ConnectionService connectionService = null;
        try {
            connectionService = this.connectionServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return connectionService;
    }

    private IMessagingService getMessagingService() {
        MessagingService messagingService = null;
        try {
            messagingService = this.messagingServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return messagingService;
    }
    
    private void registerReceivers() {
        this.broadcastRegistrar = new BroadcastRegistrar();
        this.broadcastRegistrar
            .addAction(ConnectionService.ACTION_HOST_CONNECTED, new IBroadcastActionHandler() {

                    @Override
                    public void onReceiveAction(Context context, Intent intent) {
                        connectButton.setEnabled(true);
                        //switch 
                        Transitions.transitionToHome((CoreActivity)getActivity());
                        ((CoreActivity)getActivity()).enableSlidingMenu();
                        
                      //add the playbar fragment onto the active content view
                        ((CoreActivity)getActivity()).showPlaybar();
                    }
                })
             .addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    int mode = intent.getIntExtra(
                            BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.SCAN_MODE_NONE);
                    if(connectButton != null){
                        switch(mode){
                        case BluetoothAdapter.SCAN_MODE_NONE:
                            connectButton.setEnabled(true);
                            break;
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                            connectButton.setEnabled(true);
                            break;
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                            connectButton.setEnabled(false);
                            break;
                        default:
                            Log.wtf(TAG, "Recieved scan mode changed with unknown mode");
                            break;
                        }
                    }
                }
            })
            .register(this.getActivity());
    }

    private void unregisterReceivers() {
        this.broadcastRegistrar.unregister();
    }

    @Override
    public int getTitle() {
        return R.string.connect;
    }
}
