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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.lastcrusade.soundstream.CoreActivity;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.model.FoundGuest;
import com.lastcrusade.soundstream.model.UserList;
import com.lastcrusade.soundstream.service.ConnectionService;
import com.lastcrusade.soundstream.service.ConnectionService.ConnectionServiceBinder;
import com.lastcrusade.soundstream.service.MusicLibraryService;
import com.lastcrusade.soundstream.service.MusicLibraryService.MusicLibraryServiceBinder;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.service.PlaylistService.PlaylistServiceBinder;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.service.UserListService;
import com.lastcrusade.soundstream.service.UserListService.UserListServiceBinder;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.ITitleable;
import com.lastcrusade.soundstream.util.Toaster;
import com.lastcrusade.soundstream.util.Transitions;
import com.lastcrusade.soundstream.util.UserListAdapter;

/**
 * This fragment handles the ability for members to add new members to 
 * the network and to view the currently connected members
 */
public class NetworkFragment extends SherlockFragment implements ITitleable {
    
    private static String TAG = NetworkFragment.class.getSimpleName();
    
    private BroadcastRegistrar broadcastRegistrar;
    private Button addMembersButton, disconnect, disband;
    private UserListAdapter userAdapter;
    private LinearLayout userView;

    private ServiceLocator<ConnectionService> connectionServiceLocator;
    private ServiceLocator<UserListService>   userListServiceLocator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionServiceLocator = new ServiceLocator<ConnectionService>(
                this.getActivity(), ConnectionService.class, ConnectionServiceBinder.class);

        connectionServiceLocator.setOnBindListener(new ServiceLocator.IOnBindListener() {
            @Override
            public void onServiceBound() {
                setDisconnectDisbandVisibility();
            }
        });

        userAdapter = new UserListAdapter(getActivity(), new UserList(), false);
        userView = new LinearLayout(getActivity());
        
        userListServiceLocator = new ServiceLocator<UserListService>(
                this.getActivity(), UserListService.class, UserListServiceBinder.class);

        userListServiceLocator.setOnBindListener(new ServiceLocator.IOnBindListener() {
            @Override
            public void onServiceBound() {
                NetworkFragment.this.userAdapter.updateUsers(getUserListFromService());
                updateUserView();
            }
        });

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

        disconnect = (Button)v.findViewById(R.id.disconnect_btn);
        disband = (Button)v.findViewById(R.id.disband_btn);

        userView = (LinearLayout) v.findViewById(R.id.connected_users);
        updateUserView();
        //users.setAdapter(this.adapter);

        //TODO react to changing state
        setDisconnectDisbandVisibility();

        disconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.dialog_disconnect)
                        .setPositiveButton(R.string.disconnect, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "Disconnecting...");
                                disconnect();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
        disband.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.dialog_disband)
                        .setPositiveButton(R.string.disband, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                disband();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
        
        Button connect = (Button)v.findViewById(R.id.connect_btn);
        connect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getConnectionService().broadcastSelfAsGuest(getActivity());
            }
        });
        return v;
    }

    private void setDisconnectDisbandVisibility() {
        //TODO @bryan sills clean this code up
        if (getConnectionService() != null && getConnectionService().isGuestConnected()) {
            if(disconnect != null){
                disconnect.setVisibility(View.INVISIBLE);
            }
            if(disband != null){
                disband.setVisibility(View.VISIBLE);
            }
        } else if (getConnectionService() != null && getConnectionService().isHostConnected()) {
            if(disconnect != null){
                disconnect.setVisibility(View.VISIBLE);
            }
            disband.setVisibility(View.INVISIBLE);
        } else {
            //if no one is connected, hide both buttons
            if(disconnect != null){
                disconnect.setVisibility(View.INVISIBLE);
            }
            if(disband != null){
                disband.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((CoreActivity)getActivity()).getTracker().sendView(TAG);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(this.userAdapter != null){
            this.userAdapter.notifyDataSetChanged();
            updateUserView();
        }
        getActivity().setTitle(getTitle());
    }
    
    @Override
    public void onDestroy() {
        unregisterReceivers();
        this.connectionServiceLocator.unbind();
        this.userListServiceLocator.unbind();
        super.onDestroy();
    }

    private ConnectionService getConnectionService() {
        ConnectionService connectionService = null;
        try {
            connectionService = this.connectionServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            //TODO Make this a real error
            Log.v(TAG, e.toString());
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
                    userAdapter.notifyDataSetChanged();
                    updateUserView();
                }
            })
            .addAction(ConnectionService.ACTION_GUEST_CONNECTED, new IBroadcastActionHandler() {
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    setDisconnectDisbandVisibility();
                }
            })
            .addAction(ConnectionService.ACTION_HOST_CONNECTED, new IBroadcastActionHandler() {
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    setDisconnectDisbandVisibility();
                }
            })
            .addAction(ConnectionService.ACTION_HOST_DISCONNECTED, new IBroadcastActionHandler() {
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    Log.i(TAG, "Host Disconnected");
                    //after the host has been disconnected, wipe everything and start fresh
                    cleanUpAfterDisconnect();
                }
            })
            .register(this.getActivity());
    }

    private void unregisterReceivers() {
        this.broadcastRegistrar.unregister();
    }

    private void disconnect() {
        //Disconnect from the host
        getConnectionService().disconnectHost();
        //Reset all variables
        cleanUpAfterDisconnect();
    }

    private void cleanUpAfterDisconnect() {
        final ServiceLocator<PlaylistService> playlistServiceLocator = new ServiceLocator<PlaylistService>(
                this.getActivity(), PlaylistService.class, PlaylistServiceBinder.class);

        playlistServiceLocator.setOnBindListener(new ServiceLocator.IOnBindListener() {
            @Override
            public void onServiceBound() {
                try {
                    playlistServiceLocator.getService().clearPlaylist();
                    //this is the only place where we bind and use the service, so we unbind as soon as we are done
                    playlistServiceLocator.unbind();
                } catch (ServiceNotBoundException e) {
                    Log.wtf(TAG,"PlaylistService not bound");
                }
            }
        });

        final ServiceLocator<MusicLibraryService> musicLibraryServiceLocator = new ServiceLocator<MusicLibraryService>(
                this.getActivity(), MusicLibraryService.class, MusicLibraryServiceBinder.class);

        musicLibraryServiceLocator.setOnBindListener(new ServiceLocator.IOnBindListener() {
            @Override
            public void onServiceBound() {
                try {
                    musicLibraryServiceLocator.getService().clearExternalMusic();
                    //this is the only place where we bind and use the service, so we unbind as soon as we are done
                    musicLibraryServiceLocator.unbind();
                } catch (ServiceNotBoundException e) {
                    Log.wtf(TAG, "MusicLibraryService not bound");
                }
            }
        });

        UserListService userService = getUserListService();
        if(userService != null){
            userService.clearExternalUsers();
        }
        
        //Send the user to a page where they can start a network or join a different network
        Transitions.transitionToConnect((CoreActivity) getActivity());
    }

    private void disband() {
        getConnectionService().disconnectAllGuests();
        cleanUpAfterDisconnect();
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
        
        //deduplicate and pull out the known items, so we can preselect them
        //NOTE: retained is the list of deduplicated found guests, known
        // is the list of guests that were previously paired.
        Set<FoundGuest>  unique = new HashSet<FoundGuest>(guests);
        List<FoundGuest> retained = new ArrayList<FoundGuest>();
        List<FoundGuest> known = new ArrayList<FoundGuest>();
        for (FoundGuest guest : guests) {
            if (unique.contains(guest)) {
                retained.add(guest);
                unique.remove(guest);
            }
            if (guest.isKnown()) {
                known.add(guest);
            }
        }
        
        if (guests.isEmpty()) {
            Toaster.iToast(this.getActivity(), R.string.no_guests_found);
        } else {
            new MultiSelectListDialog<FoundGuest>(this.getActivity(),
                    R.string.select_guests, R.string.connect)
                    .setItems(retained)
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

    private UserList getUserListFromService(){
        UserList activeUsers;
        UserListService userService = getUserListService();
        if(userService != null){
            activeUsers = userService.getUserList();
        } else {
            activeUsers = new UserList();
            Log.i(TAG, "UserListService null, returning empty userlist");
        }
        return activeUsers;
    }

    private UserListService getUserListService(){
        UserListService userService = null;
        try{
            userService = userListServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.w(TAG, "UserListService not bound");
        }
        return userService;
    }
    
    private void updateUserView(){
        userView.removeAllViews();
        
        for(int i=0; i<userAdapter.getCount(); i++){
            View user = userAdapter.getView(i, null, userView);
            userView.addView(user);
        }
        
    }
}
