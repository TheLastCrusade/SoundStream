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
import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.ImageButton;

import com.google.analytics.tracking.android.EasyTracker;
import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.model.UserList;
import com.lastcrusade.soundstream.service.MusicLibraryService;
import com.lastcrusade.soundstream.service.MusicLibraryService.MusicLibraryServiceBinder;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.service.PlaylistService.PlaylistServiceBinder;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceLocator.IOnBindListener;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.service.UserListService;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.MusicListAdapter;
import com.lastcrusade.soundstream.util.Toaster;

public class MusicLibraryFragment extends MusicListFragment {
    private final String TAG = MusicLibraryFragment.class.getSimpleName();
    private BroadcastRegistrar registrar;

    private ServiceLocator<PlaylistService> playlistServiceLocator;
    private ServiceLocator<UserListService> userListServiceLocator;
    private ServiceLocator<MusicLibraryService> musicLibraryServiceLocator;
    
    private MusicAdapter mMusicAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //make a new music list adapter and give it an empty list of songs and
        // empty list of users to use until the service is connected
        mMusicAdapter = new MusicAdapter(
                this.getActivity(),
                new ArrayList<SongMetadata>(),
                new UserList()
        );

        playlistServiceLocator = new ServiceLocator<PlaylistService>(
                MusicLibraryFragment.this.getActivity(),
                PlaylistService.class,
                PlaylistServiceBinder.class
        );

        userListServiceLocator = new ServiceLocator<UserListService>(
                this.getActivity(),
                UserListService.class,
                UserListService.UserListServiceBinder.class
        );
        userListServiceLocator.setOnBindListener(new IOnBindListener() {
            @Override
            public void onServiceBound() {
                mMusicAdapter.updateUsers(getUserListFromService());
            }
        });

        musicLibraryServiceLocator = new ServiceLocator<MusicLibraryService>(
                this.getActivity(),
                MusicLibraryService.class,
                MusicLibraryService.MusicLibraryServiceBinder.class
        );
        musicLibraryServiceLocator.setOnBindListener(new IOnBindListener() {
            @Override
            public void onServiceBound() {
                mMusicAdapter.updateMusicFromLibrary();
            }
        });

        registerReceivers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playlistServiceLocator.unbind();
        userListServiceLocator.unbind();
        musicLibraryServiceLocator.unbind();
        unregisterReceivers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setListAdapter(mMusicAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getTracker().sendView(TAG);
    }

    @Override
    public int getTitle() {
        return R.string.music_library;
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
                mMusicAdapter.updateMusicFromLibrary();
            }
        })
        .addAction(PlaylistService.ACTION_SONG_ADDED, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                SongMetadata entry = intent.getParcelableExtra(PlaylistService.EXTRA_SONG);
                //for now this is just a toast, but later it might change to something that allows
                //the user to undo the action
                //Toaster.iToast(getActivity(), "\"" + entry.getTitle() + "\" has been added.");
                
                //commenting out the toast, but leaving this here for now so that if we want to 
                //go back in and add some kind of pop up menu to undo the addition we have a 
                //place to do so
            }
        })
        .register(this.getActivity());
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

    protected MusicLibraryService getMusicLibraryService() {
        MusicLibraryService musicLibraryService = null;
        try {
            musicLibraryService = this.musicLibraryServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.w(TAG, "MusicLibraryService not bound");
        }
        return musicLibraryService;
    }
    private ArrayList<SongMetadata> getMusicLibraryFromService(){
        ArrayList<SongMetadata> library;
        MusicLibraryService musicLibraryService = getMusicLibraryService();
        if(musicLibraryService != null){
            library = new ArrayList<SongMetadata>(musicLibraryService.getLibrary());
        } else {
            library = new ArrayList<SongMetadata>();
            Log.i(TAG, "MusicLibarysService null, returning empty library");
        }
        return library;
    }


    private UserList getUserListFromService(){
        UserList activeUsers = new UserList();
        UserListService userService = getUserListService();
        if(userService != null){
            return userService.getUserList();
        } else {
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


    /**
     * Inner class extends MusicListAdapter to add the add to playlist image button, and click listener
     * 
     */
    private class MusicAdapter extends MusicListAdapter<SongMetadata> {
        public MusicAdapter(
                Context mContext,
                List<SongMetadata> metadataList,
                UserList users
                ) {
            super(mContext, metadataList, users);
        }
        
        public View getView(int position, View convertView, ViewGroup parent){
            View v = super.getView(position, convertView, parent);
            ImageButton imageButton = (ImageButton) v.findViewById(R.id.btn_add_to_playlist);
            imageButton.setVisibility(View.VISIBLE);
            imageButton.setBackgroundColor(getResources().getColor(R.color.transparent));
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SongMetadata meta = getItem((Integer) v.getTag());
                    getPlaylistService().addSong(meta);
                    
                    //change the color of the view for a small period of time to indicate that the add 
                    //button has been pressed
                    v.setBackgroundColor(getResources().getColor(R.color.abs__holo_blue_light));
                    Timer colorTimer = new Timer();
                    colorTimer.schedule(new ColorTimerTask(v),200);
                }
            });

            return v;
            }
        

        /**
         * Update the list with music from the library
         */
        private void updateMusicFromLibrary() {
            this.updateMusic(getMusicLibraryFromService());
            notifyDataSetChanged();
        }
        
        private class ColorTimerTask extends TimerTask{
            private View view;
            
            public ColorTimerTask(View view){
                this.view = view;
            }
            @Override
            public void run() {
                //has to run on the UI thread, and the only way to do that
                //is to encase it in a runnable
                getActivity().runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        view.setBackgroundColor(
                                getResources().getColor(R.color.abs__background_holo_light));
                        
                    }
                });
                        
            }
            
        }
    }
}
