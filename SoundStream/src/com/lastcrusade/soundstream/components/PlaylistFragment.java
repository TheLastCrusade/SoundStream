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

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.model.PlaylistEntry;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.model.UserList;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.MusicListAdapter;
import com.lastcrusade.soundstream.util.Toaster;

public class PlaylistFragment extends MusicListFragment{
    //for testing purposes so we have songs to show
    private final String TAG = PlaylistFragment.class.getName();

    private BroadcastRegistrar registrar;

    private ServiceLocator<PlaylistService> playlistServiceServiceLocator;

    private PlayListAdapter mPlayListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        final CustomApp curApp = (CustomApp) this.getActivity().getApplication();

        playlistServiceServiceLocator = new ServiceLocator<PlaylistService>(
                this.getActivity(),
                PlaylistService.class,
                PlaylistService.PlaylistServiceBinder.class
        );

        playlistServiceServiceLocator.setOnBindListener(new ServiceLocator.IOnBindListener() {
            @Override
            public void onServiceBound() {
                updatePlaylist();
            }
        });

        mPlayListAdapter = new PlayListAdapter(
                this.getActivity(),
                Collections.EMPTY_LIST,
                curApp.getUserList()
        );
        setListAdapter(mPlayListAdapter);

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
        playlistServiceServiceLocator.unbind();
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
                updatePlaylist();
            }
        }).addAction(PlaylistService.ACTION_PLAYING_AUDIO, new IBroadcastActionHandler() {
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                //when the playlist starts playing a song, we want to make sure that we are
                // showing the correct song being played, so we tell the adapter to update
                // the playlist to force a redraw of the views
                updatePlaylist();
            }
        })
        .addAction(PlaylistService.ACTION_SONG_REMOVED, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                SongMetadata entry = intent.getParcelableExtra(PlaylistService.EXTRA_SONG);
                //for now we are just toasting, but eventually this might change to something that 
                //allows the user to undo the action
                //Toaster.iToast(getActivity(), getString(R.string.removed_label) + "\"" + entry.getTitle() + "\"");
                
                //commenting out the toast, but leaving this here for now so that if we want to 
                //go back in and add some kind of pop up menu to undo the removal we have a 
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
            playlistService = this.playlistServiceServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return playlistService;
    }

    /**
     * 
     */
    private void updatePlaylist() {
        mPlayListAdapter.updateMusic(getPlaylistService().getPlaylistEntries());
    }
    

    private class PlayListAdapter extends MusicListAdapter<PlaylistEntry> {
        public PlayListAdapter(
                Context mContext,
                List<PlaylistEntry> playlistEntries,
                UserList users
                ) {
            super(mContext, playlistEntries, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View element = super.getView(position, convertView, parent);
            
            //This depends on played music being above unplayed music
            PlaylistEntry entry = super.getItem(position);
            
            if (!entry.isLoaded()) {
                element.setBackgroundColor(getResources().getColor(R.color.loading));
                element.findViewById(R.id.now_playing).setVisibility(View.INVISIBLE);
            } 
            else if (entry.isPlayed()) {
                element.setBackgroundColor(getResources().getColor(R.color.used));
                element.findViewById(R.id.now_playing).setVisibility(View.INVISIBLE);
            } 
            else {
                element.setBackgroundColor(getResources().getColor(com.actionbarsherlock.R.color.abs__background_holo_light));
                if(entry.equals(getPlaylistService().getCurrentEntry())){
                    element.findViewById(R.id.now_playing).setVisibility(View.VISIBLE);
                }
                else{
                    element.findViewById(R.id.now_playing).setVisibility(View.INVISIBLE);
                }
            }
            

            //add gesture detection on the song element
            final GestureDetectorCompat songGesture = new GestureDetectorCompat(getActivity(), new PlaylistSongGestureListener(element, entry));
            element.setOnTouchListener(new View.OnTouchListener() {       
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return songGesture.onTouchEvent(event);
                }

            });

            ImageButton delete = (ImageButton)element.findViewById(R.id.btn_remove_from_playlist);
            delete.setOnClickListener(new DeleteSongListener(entry));
            delete.setVisibility(View.VISIBLE);

            
            return element;
        }
        
        /* (non-Javadoc)
         * @see com.lastcrusade.soundstream.util.MusicListAdapter#updateMusic(java.util.List)
         */
        @Override
        public void updateMusic(List<PlaylistEntry> metadataList) {
            super.updateMusic(metadataList);
            if(metadataList.size() == 0){
                Toaster.iToast(getActivity(),R.string.no_songs_in_playlist);
            }
        }
        
        private class DeleteSongListener implements OnClickListener{
            private PlaylistEntry entry;
            public DeleteSongListener(PlaylistEntry entry){
                this.entry = entry;
            }
            @Override
            public void onClick(View v) {
                if(getPlaylistService().getCurrentEntry()!= null && getPlaylistService().getCurrentEntry().equals(entry)){
                    getPlaylistService().skip();
                }
                getPlaylistService().removeSong(entry);
               
            }
            
        }
        

      //detect gestures 
        private class PlaylistSongGestureListener extends SongGestureListener{
            private PlaylistEntry entry;
            
            public PlaylistSongGestureListener(View view, PlaylistEntry entry){
                super(view);
                this.entry = entry;
            }
            //fling a song to the right to remove it
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                    float velocityY) {
                // TODO Implement remove this way in another pull request
                return super.onFling(e1, e2, velocityX, velocityY);
            }
            
            //bump the song to the top when double tapped
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                getPlaylistService().bumpSong(entry);
                return true;
            } 
        }
    }

}
