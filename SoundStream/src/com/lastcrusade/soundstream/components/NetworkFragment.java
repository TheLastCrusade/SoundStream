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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.model.UserList;
import com.lastcrusade.soundstream.net.message.FoundGuest;
import com.lastcrusade.soundstream.service.ConnectionService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.service.ConnectionService.ConnectionServiceBinder;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.ITitleable;
import com.lastcrusade.soundstream.util.Toaster;
import com.lastcrusade.soundstream.util.UserListAdapter;

/**
 * This fragment handles the ability for members to add new members to 
 * the network and to view the currently connected members
 */
public class NetworkFragment extends SherlockFragment implements ITitleable {
    
    private static String TAG = NetworkFragment.class.getSimpleName();
    
    private BroadcastRegistrar broadcastRegistrar;
    private Button addMembersButton;
    private UserListAdapter adapter;
    private ServiceLocator<ConnectionService> connectionServiceLocator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionServiceLocator = new ServiceLocator<ConnectionService>(
                this.getActivity(), ConnectionService.class, ConnectionServiceBinder.class);
        
        registerReceivers();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_network, container,false);
        
        this.addMembersButton = (Button)v.findViewById(R.id.add_members);
        this.addMembersButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                addMembersButton.setEnabled(false);
                //TODO: add some kind of visual indicator while discovering...seconds until discovery is finished, number of clients found, etc
                getConnectionService().findNewGuests();
            }
        });
        
        ListView users = (ListView)v.findViewById(R.id.connected_users);
        
        this.adapter = new UserListAdapter(getActivity(), ((CustomApp)getActivity().getApplication()).getUserList(), false );
        users.setAdapter(this.adapter);
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

    private void registerReceivers() {
        this.broadcastRegistrar = new BroadcastRegistrar();
        this.broadcastRegistrar
            .addAction(ConnectionService.ACTION_FIND_FINISHED, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    onFindFinished(intent);
                }
            })
            .addAction(UserList.ACTION_USER_LIST_UPDATE, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    adapter.notifyDataSetChanged();
                }
            })
            .register(this.getActivity());
    }

    private void unregisterReceivers() {
        this.broadcastRegistrar.unregister();
    }

    /**
     * Called to handle a find finished method.  This may be to pop up a dialog
     * or notify the user that no guests were found
     * 
     * @param intent
     */
    private void onFindFinished(Intent intent) {
        //first thing...reenable the add members button
        addMembersButton.setEnabled(true);
        
        //locally initiated device discovery...pop up a dialog for the user
        List<FoundGuest> guests = intent.getParcelableArrayListExtra(ConnectionService.EXTRA_GUESTS);
        //pull out the known items, so we can preselect them
        List<FoundGuest> known = new ArrayList<FoundGuest>();
        for (FoundGuest guest : guests) {
            if (guest.isKnown()) {
                known.add(guest);
            }
        }
        if (guests.isEmpty()) {
            Toaster.iToast(this.getActivity(), R.string.no_guests_found);
        } else {
            new MultiSelectListDialog<FoundGuest>(this.getActivity(),
                    R.string.select_guests, R.string.connect)
                    .setItems(guests)
                    .setSelected(known)
                    .setOnClickListener(
                            new IOnDialogMultiItemClickListener<FoundGuest>() {
    
                                @Override
                                public void onItemsClick(
                                        List<FoundGuest> foundGuests) {
                                    getConnectionService().connectToGuests(foundGuests);
                                }
                            })
                    .show();
        }
    }

    @Override
    public int getTitle() {
        return R.string.network;
    }
}
