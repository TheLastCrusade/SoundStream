package com.lastcrusade.fanclub.components;

import java.util.Hashtable;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.lastcrusade.fanclub.CustomApp;
import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.service.MusicLibraryService;
import com.lastcrusade.fanclub.service.MusicLibraryService.MusicLibraryServiceBinder;
import com.lastcrusade.fanclub.service.PlaylistService;
import com.lastcrusade.fanclub.service.PlaylistService.PlaylistServiceBinder;
import com.lastcrusade.fanclub.service.ServiceLocator;
import com.lastcrusade.fanclub.service.ServiceNotBoundException;
import com.lastcrusade.fanclub.util.BroadcastRegistrar;
import com.lastcrusade.fanclub.util.IBroadcastActionHandler;
import com.lastcrusade.fanclub.util.ITitleable;
import com.lastcrusade.fanclub.util.MusicListAdapter;
import com.lastcrusade.fanclub.util.Toaster;

public class MusicLibraryFragment extends SherlockListFragment implements ITitleable {
    private final String TAG = MusicLibraryFragment.class.getName();
    private BroadcastRegistrar registrar;

    private ServiceLocator<PlaylistService> playlistServiceLocator;

    private MusicLibraryService mMusicLibraryService;
    private boolean boundToService = false; //Since you cannot instantly bind, set a boolean
                                    // after its safe to call methods
    private List<SongMetadata> musicLibrary; //I dont like having this instance var
    private Hashtable<String, String> users; //I dont like having this instance var

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection musicLibraryConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            MusicLibraryServiceBinder binder = (MusicLibraryServiceBinder) service;
            mMusicLibraryService = binder.getService();
            boundToService = true;
            setListAdapter(
                    new MusicAdapter(
                            MusicLibraryFragment.this.getActivity(),
                            getMusicLibrary(),
                            users
                    )
            );
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundToService = false;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        playlistServiceLocator = new ServiceLocator<PlaylistService>(MusicLibraryFragment.this.getActivity(),
                PlaylistService.class, PlaylistServiceBinder.class);

        if(boundToService == false){
            Intent intentML = new Intent(this.getActivity(), MusicLibraryService.class);
            this.getActivity().bindService(intentML, musicLibraryConn, Context.BIND_AUTO_CREATE);
        }
        users = ((CustomApp) this.getActivity().getApplication()).getUserList().getUsers();

        registerReceivers();
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.list, container, false);
        return v;
    }
    
    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(getTitle());
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unbind from the MusicLibrary service
        if (boundToService) {
            this.getActivity().unbindService(musicLibraryConn);
            boundToService = false;
        }
        unregisterReceivers();
    }

    private List<SongMetadata> getMusicLibrary() {
        //If we can refresh our music library do otherwise return the old one
        if(mMusicLibraryService != null && boundToService){
            musicLibrary = mMusicLibraryService.getLibrary();
        }
        return musicLibrary;
    }

    @Override
    public String getTitle() {
        return getString(R.string.music_library);
    }
    
    /**
     * Register intent receivers to control this service
     *
     */
    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar.addAction(MusicLibraryService.ACTION_LIBRARY_UPDATED, new IBroadcastActionHandler() {

            @Override
            public void onReceiveAction(Context context, Intent intent) {
                //Update library shown when the library service gets an update
                setListAdapter(new MusicAdapter(
                                context,
                                getMusicLibrary(),
                                users
                              )
                );

            }
        }).register(this.getActivity());
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }

    protected PlaylistService getPlaylistService() {
        PlaylistService playlistService = null;

        try {
            playlistService = this.playlistServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return playlistService;
    }

    private class MusicAdapter extends MusicListAdapter {
        public MusicAdapter(
                Context mContext,
                List<SongMetadata> metadataList,
                Hashtable<String, String> users
                ) {
            super(mContext, metadataList, users);
        }
        
        public View getView(int position, View convertView, ViewGroup parent){
            View v = super.getView(position, convertView, parent);
            v.findViewById(R.id.add_to_playlist).setVisibility(View.VISIBLE);
            return v;
        }

        @Override
        public void onClick(View v) {
            SongMetadata meta = musicLibrary.get((Integer) v.getTag());
            getPlaylistService().addMetadata(meta);
        }
    }
}
