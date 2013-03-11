package com.lastcrusade.fanclub.components;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.lastcrusade.fanclub.CustomApp;
import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.model.Playlist;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.model.UserList;
import com.lastcrusade.fanclub.service.PlaylistService;
import com.lastcrusade.fanclub.service.ServiceLocator;
import com.lastcrusade.fanclub.service.ServiceNotBoundException;
import com.lastcrusade.fanclub.util.BroadcastRegistrar;
import com.lastcrusade.fanclub.util.IBroadcastActionHandler;
import com.lastcrusade.fanclub.util.MusicListAdapter;

public class PlaylistFragment extends MusicListFragment{
    //for testing purposes so we have songs to show
    private final String TAG = PlaylistFragment.class.getName();

    private BroadcastRegistrar registrar;
    private Playlist metadataList;

    private ServiceLocator<PlaylistService> playlistServiceServiceLocator;

    private final int SHORT_VIEW = 1;
    private final int EXPANDED_VIEW = 10;

    private PlayListAdapter mPlayListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        playlistServiceServiceLocator = new ServiceLocator<PlaylistService>(
                this.getActivity(), PlaylistService.class, PlaylistService.PlaylistServiceBinder.class);

        //TODO: get the userlist better
        final CustomApp curApp = (CustomApp) this.getActivity().getApplication();
        mPlayListAdapter = new PlayListAdapter(this.getActivity(), new ArrayList<SongMetadata>(), curApp.getUserList());
        setListAdapter(mPlayListAdapter);

        playlistServiceServiceLocator.setOnBindListener(new ServiceLocator.IOnBindListener() {
            @Override
            public void onServiceBound() {
                metadataList = getPlaylistService().getPlaylist();
                mPlayListAdapter.updateMusic(metadataList.getUnPlayedList(), metadataList.getPlayedList());
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
    public String getTitle() {
        return getString(R.string.playlist);
    }

    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar.addAction(PlaylistService.ACTION_PLAYLIST_UPDATED, new IBroadcastActionHandler() {
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                Log.i(PlaylistFragment.class.getName(), "action playlist updated");
                mPlayListAdapter.updateMusic(metadataList.getUnPlayedList(), metadataList.getPlayedList());
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

        public void updateMusic(List<SongMetadata> unPlayed, List<SongMetadata> played) {
            List<SongMetadata> combinedList = new ArrayList<SongMetadata>(unPlayed);
            combinedList.addAll(played);
            super.updateMusic(combinedList);
        }
    }

}
