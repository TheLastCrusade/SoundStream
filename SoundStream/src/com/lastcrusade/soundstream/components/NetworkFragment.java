package com.lastcrusade.soundstream.components;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.lastcrusade.soundstream.CoreActivity;
import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.model.UserList;
import com.lastcrusade.soundstream.net.message.FoundGuest;
import com.lastcrusade.soundstream.service.ConnectionService;
import com.lastcrusade.soundstream.service.ConnectionService.ConnectionServiceBinder;
import com.lastcrusade.soundstream.service.MusicLibraryService;
import com.lastcrusade.soundstream.service.MusicLibraryService.MusicLibraryServiceBinder;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.service.PlaylistService.PlaylistServiceBinder;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
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
    private Button addMembersButton;
    private UserListAdapter adapter;
    private ServiceLocator<ConnectionService> connectionServiceLocator;
    private ServiceLocator<PlaylistService> playlistServiceLocator;
    private ServiceLocator<MusicLibraryService> musicLibraryServiceLocator;

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

        Button disconnect = (Button)v.findViewById(R.id.disconnect_btn);
        disconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.dialog_disconnect)
                        .setPositiveButton(R.string.disconnect, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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

    private void disconnect() {
        getConnectionService().disconnectHost();

        playlistServiceLocator = new ServiceLocator<PlaylistService>(
                this.getActivity(), PlaylistService.class, PlaylistServiceBinder.class);
        playlistServiceLocator.setOnBindListener(new ServiceLocator.IOnBindListener() {
            @Override
            public void onServiceBound() {
                try {
                    playlistServiceLocator.getService().clearPlaylist();
                } catch (ServiceNotBoundException e) {
                    Log.wtf(TAG,"Service not bound");
                }
            }
        });

        musicLibraryServiceLocator = new ServiceLocator<MusicLibraryService>(
                this.getActivity(), MusicLibraryService.class, MusicLibraryServiceBinder.class);
        musicLibraryServiceLocator.setOnBindListener(new ServiceLocator.IOnBindListener() {
            @Override
            public void onServiceBound() {
                try {
                    musicLibraryServiceLocator.getService().clearExternalMusic();
                } catch (ServiceNotBoundException e) {
                    Log.wtf(TAG,"Service not bound");
                }
            }
        });

        ((CustomApp)getActivity().getApplication()).setUserList(new UserList());

        Transitions.transitionToConnect((CoreActivity) getActivity());
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
