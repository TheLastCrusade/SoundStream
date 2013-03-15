package com.lastcrusade.soundstream.components;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.model.Playlist;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.model.UserList;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.MusicListAdapter;

public class PlaylistFragment extends MusicListFragment{
    //for testing purposes so we have songs to show
    private final String TAG = PlaylistFragment.class.getName();

    private BroadcastRegistrar registrar;
    private Playlist mPlaylist;

    private ServiceLocator<PlaylistService> playlistServiceServiceLocator;

    private PlayListAdapter mPlayListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        playlistServiceServiceLocator = new ServiceLocator<PlaylistService>(
                this.getActivity(), PlaylistService.class, PlaylistService.PlaylistServiceBinder.class);

        final CustomApp curApp = (CustomApp) this.getActivity().getApplication();
        mPlayListAdapter = new PlayListAdapter(this.getActivity(), new ArrayList<SongMetadata>(), curApp.getUserList());
        setListAdapter(mPlayListAdapter);

        playlistServiceServiceLocator.setOnBindListener(new ServiceLocator.IOnBindListener() {
            @Override
            public void onServiceBound() {
                mPlaylist = getPlaylistService().getPlaylist();
                mPlayListAdapter.updateMusic(mPlaylist);
            }
        });

        registerReceivers();
    }

    @Override
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

        unregisterReceivers();
    }

    @Override
    public int getTitle() {
        return R.string.playlist;
    }

    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar.addAction(PlaylistService.ACTION_PLAYLIST_UPDATED, new IBroadcastActionHandler() {
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                mPlayListAdapter.updateMusic(mPlaylist);
            }
        }).register(this.getActivity());
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }

    private Playlist getPlaylist() {
        Playlist playlist = null;

        try {
            playlist = playlistServiceServiceLocator.getService().getPlaylist();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return playlist;
    }

    protected PlaylistService getPlaylistService() {
        PlaylistService playlistService = null;

        try {
            playlistService = this.playlistServiceServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return playlistService;
    }

    private class PlayListAdapter extends MusicListAdapter {
        public PlayListAdapter(
                Context mContext,
                List<SongMetadata> metadataList,
                UserList users
                ) {
            super(mContext, metadataList, users);
        }

        public void updateMusic(Playlist musicList){
            super.updateMusic(musicList.getSongsToPlay());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View element = super.getView(position, convertView, parent);
            //This depends on played music being above unplayed music
            if(position < mPlaylist.getIndex()){
                element.setBackgroundColor(getResources().getColor(R.color.used));
            } else {
                element.setBackgroundColor(getResources().getColor(com.actionbarsherlock.R.color.abs__background_holo_light));
            }
            return element;
        }
    }

}
