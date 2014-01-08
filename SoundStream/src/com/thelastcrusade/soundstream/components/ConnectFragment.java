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

package com.thelastcrusade.soundstream.components;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thelastcrusade.soundstream.CoreActivity;
import com.thelastcrusade.soundstream.R;
import com.thelastcrusade.soundstream.service.ConnectionService;
import com.thelastcrusade.soundstream.service.ConnectionService.ConnectionServiceBinder;
import com.thelastcrusade.soundstream.service.IMessagingService;
import com.thelastcrusade.soundstream.service.MessagingService;
import com.thelastcrusade.soundstream.service.MessagingService.MessagingServiceBinder;
import com.thelastcrusade.soundstream.service.ServiceLocator;
import com.thelastcrusade.soundstream.service.ServiceNotBoundException;
import com.thelastcrusade.soundstream.util.BluetoothUtils;
import com.thelastcrusade.soundstream.util.BroadcastRegistrar;
import com.thelastcrusade.soundstream.util.ContentDescriptionUtils;
import com.thelastcrusade.soundstream.util.IBroadcastActionHandler;
import com.thelastcrusade.soundstream.util.ITitleable;
import com.thelastcrusade.soundstream.util.Transitions;

/*
 * This fragment should be what is first presented to the user when
 * they enter the app and are not connected to any network
 */
public class ConnectFragment extends Fragment implements ITitleable{
    
    private static final String TAG = ConnectFragment.class.getSimpleName();
    private final String SEARCHING_TAG = "isSearching";

    private BroadcastRegistrar broadcastRegistrar;
    private View joinView;
    private boolean isSearching;

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

        ((CoreActivity)getActivity()).hidePlaybar();
        
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        if(rotation == Surface.ROTATION_270 || rotation == Surface.ROTATION_90){
            ((LinearLayout)v).setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams clickableParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1);
            clickableParams.setMargins(5, 10, 10, 10);
            v.findViewById(R.id.join).setLayoutParams(clickableParams);
            clickableParams.setMargins(10, 10, 5, 10);
            v.findViewById(R.id.create).setLayoutParams(clickableParams); 
        }
        else{
            ((LinearLayout)v).setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams clickableParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1);
            clickableParams.setMargins(10, 5, 10, 10);
            v.findViewById(R.id.join).setLayoutParams(clickableParams);
            clickableParams.setMargins(10, 10, 10, 5);
            v.findViewById(R.id.create).setLayoutParams(clickableParams);
        }
        View create = v.findViewById(R.id.create);
        create.setOnClickListener( new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Transitions.transitionToHome((CoreActivity)getActivity());
                ((CoreActivity)getActivity()).enableSlidingMenu();
                //add the playbar fragment onto the active content view
                ((CoreActivity)getActivity()).showPlaybar();
            }
        });
        create.setContentDescription(ContentDescriptionUtils.CREATE);
        
        this.joinView = v.findViewById(R.id.join);
        this.joinView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new WithBluetoothEnabled(getActivity(), getConnectionService()).run(new Runnable() {

                    @Override
                    public void run() {
                        getConnectionService().broadcastSelfAsGuest(getActivity());
                    }
                });
            }
        });
        joinView.setContentDescription(ContentDescriptionUtils.CONNECT);

        TextView joinText = (TextView) v.findViewById(R.id.join_network_id);
        joinText.setText(String.format(
                         getString(R.string.join_network),
                         BluetoothUtils.getLocalBluetoothName()));
        
        if(savedInstanceState != null){
            isSearching = savedInstanceState.getBoolean(SEARCHING_TAG);
            if(isSearching)
                setJoinToSearchingState();
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
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
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SEARCHING_TAG, isSearching);
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
            .addLocalAction(ConnectionService.ACTION_HOST_CONNECTED, new IBroadcastActionHandler() {

                    @Override
                    public void onReceiveAction(Context context, Intent intent) {
                        joinView.setEnabled(true);
                        joinView.findViewById(R.id.searching).setVisibility(View.INVISIBLE);
                        //switch 
                        Transitions.transitionToHome((CoreActivity)getActivity());
                        ((CoreActivity)getActivity()).enableSlidingMenu();
                        
                      //add the playbar fragment onto the active content view
                        ((CoreActivity)getActivity()).showPlaybar();
                    }
                })
             .addGlobalAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    int mode = intent.getIntExtra(
                            BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.SCAN_MODE_NONE);
                    if(joinView != null){
                        switch(mode){
                        case BluetoothAdapter.SCAN_MODE_NONE: //fall through to connectable
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                            isSearching = false;
                            setJoinToDefaultState();
                            break;
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                            isSearching = true;
                            setJoinToSearchingState();
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
    
    private void setJoinToDefaultState(){
        setJoinState(true, View.INVISIBLE, R.color.holo_light);
    }
    
    private void setJoinToSearchingState(){
        setJoinState(false, View.VISIBLE, R.color.gray);
    }
    
    private void setJoinState(boolean enabled, int visibility, int color){
        joinView.setEnabled(enabled);
        joinView.findViewById(R.id.searching).setVisibility(visibility);
        joinView.setBackgroundColor(getResources().getColor(color));
    }

    private void unregisterReceivers() {
        this.broadcastRegistrar.unregister();
    }

    @Override
    public int getTitle() {
        return R.string.select;
    }
}
