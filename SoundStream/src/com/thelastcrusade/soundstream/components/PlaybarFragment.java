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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thelastcrusade.soundstream.CoreActivity;
import com.thelastcrusade.soundstream.R;
import com.thelastcrusade.soundstream.model.PlaylistEntry;
import com.thelastcrusade.soundstream.model.SongMetadata;
import com.thelastcrusade.soundstream.service.MusicLibraryService;
import com.thelastcrusade.soundstream.service.MusicLibraryService.MusicLibraryServiceBinder;
import com.thelastcrusade.soundstream.service.PlaylistService;
import com.thelastcrusade.soundstream.service.PlaylistService.PlaylistServiceBinder;
import com.thelastcrusade.soundstream.service.ServiceLocator;
import com.thelastcrusade.soundstream.service.ServiceLocator.IOnBindListener;
import com.thelastcrusade.soundstream.service.ServiceNotBoundException;
import com.thelastcrusade.soundstream.util.BroadcastRegistrar;
import com.thelastcrusade.soundstream.util.ContentDescriptionUtils;
import com.thelastcrusade.soundstream.util.IBroadcastActionHandler;
import com.thelastcrusade.soundstream.util.TrackerAPI;

public class PlaybarFragment extends Fragment {

    private static final String TAG = PlaybarFragment.class.getSimpleName();
    
    private BroadcastRegistrar registrar;
    
    private ServiceLocator<PlaylistService> playlistServiceLocator;
    private ServiceLocator<MusicLibraryService> musicLibraryLocator;
    private ImageButton playPause, skip;
    private boolean boundToPlaylistService;
    private TextView songTitle;

    private TrackerAPI tracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.playlistServiceLocator = new ServiceLocator<PlaylistService>(
                this.getActivity(), PlaylistService.class, PlaylistServiceBinder.class);
        this.playlistServiceLocator.setOnBindListener(new IOnBindListener() {

            @Override
            public void onServiceBound() {
               boundToPlaylistService = true;
               updateView();
            }
            
        });
        
        this.tracker = new TrackerAPI((CoreActivity)getActivity());
        this.musicLibraryLocator = new ServiceLocator<MusicLibraryService>(
                this.getActivity(), MusicLibraryService.class, MusicLibraryServiceBinder.class);
        registerReceivers();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        playlistServiceLocator.unbind();
        musicLibraryLocator.unbind();
        unregisterReceivers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_playbar, container, false);
        
        songTitle = (TextView) view.findViewById(R.id.text_now_playing);
        playPause = ((ImageButton) view.findViewById(R.id.btn_play_pause));
        skip = (ImageButton) view.findViewById(R.id.btn_skip);
        
        if(boundToPlaylistService){
            updateView();
        }
        
        playPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistService service = getPlaylistService();
                if (service != null) {
                    //determine the intended action based on the current play state
                    //  intended == true  => we should start playing
                    //  intended == false => we should pause
                    boolean intended = !service.isPlaying();
                    // play or pause, according to the intended action
                    if (intended) {
                        service.play();
                    } else {
                        service.pause();
                    }
                    //use the service to set the image, because we want it to reflect
                    // the real state (not the intended action)
                    smartSetPlayPauseImage(service);
                    //use the intended action...this is a small semantic point, but 
                    // we want this tracker to track the intentions, not necessarily
                    // the state (which, due to a bug, may be different).
                    //TODO: we may want to also track the real state, as a potential way
                    // of identifying bugs
                    tracker.trackPlayPauseEvent(intended);
                }
            }

        });
        playPause.setContentDescription(ContentDescriptionUtils.PLAY_PAUSE);

        skip.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    PlaylistService service = getPlaylistService();
                    if (service != null) {
                        service.skip();
                        tracker.trackSkipEvent();
                    }
                }
            });
        skip.setContentDescription(ContentDescriptionUtils.SKIP);

        return view;
    }

    private void setPlayImage() {
        playPause.setImageDrawable(getResources().getDrawable(R.drawable.av_play));
    }

    private void setPauseImage(){
        playPause.setImageDrawable(getResources().getDrawable(R.drawable.av_pause));
    }

    private void smartSetPlayPauseImage(PlaylistService service){
        if(service != null && service.isPlaying()){
            setPauseImage();
        } else {
            setPlayImage();
        }
    }

    /**
     * Register intent receivers to control this service
     * 
     */
    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        //TODO: this is almost exactly like what we have implemented in the external control client
        //...we should find a way to merge these 2 pieces of code, because they should behave
        // exactly the same -- Jesse, 07/29/13
        this.registrar
            .addLocalAction(PlaylistService.ACTION_CURRENT_SONG, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    updateCurrentSongTitle((PlaylistEntry)intent.getParcelableExtra(PlaylistService.EXTRA_SONG));
                }
            })
            .addLocalAction(PlaylistService.ACTION_PLAYING_AUDIO, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    updateCurrentSongTitle(getPlaylistService().getCurrentEntry());
                    setPauseImage();
                }
            })
            .addLocalAction(PlaylistService.ACTION_PAUSED_AUDIO, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    updateCurrentSongTitle(getPlaylistService().getCurrentEntry());
                    setPlayImage();
                }
            })
            
            .addLocalAction(PlaylistService.ACTION_PLAYLIST_UPDATED, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    updateCurrentSongTitle(getPlaylistService().getCurrentEntry());
                }
            })
            .register(this.getActivity());
    }
    
    private void unregisterReceivers() {
        this.registrar.unregister();
    }
    
    /**
     * @param song
     */
    private void updateCurrentSongTitle(SongMetadata song) {
        if (song != null) {
            songTitle.setText(song.getTitle());
        } else {
            //when song is null, it means there is no current song, and no songs
            // in the playlist...restore the default "Now Playing" text
            songTitle.setText(R.string.now_playing);
        }
    }

    private PlaylistService getPlaylistService() {
        PlaylistService playlistService = null;
        try {
            playlistService = this.playlistServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return playlistService;
    }
    
    /**
     * Updates the song title and the play button
     */
    private void updateView(){
        if(songTitle != null && getPlaylistService() != null 
                && getPlaylistService().getCurrentEntry() != null){
            songTitle.setText(getPlaylistService().getCurrentEntry().getTitle());
            smartSetPlayPauseImage(getPlaylistService());
        }
    }

}
