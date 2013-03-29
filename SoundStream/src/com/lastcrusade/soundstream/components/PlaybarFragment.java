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

import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.service.PlaylistService.PlaylistServiceBinder;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;

public class PlaybarFragment extends Fragment {

    private static final String TAG = PlaybarFragment.class.getName();
    
    private BroadcastRegistrar registrar;
    
    private ServiceLocator<PlaylistService> playlistServiceLocator;
    ImageButton playPause;

    private TextView songTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.playlistServiceLocator = new ServiceLocator<PlaylistService>(
                this.getActivity(), PlaylistService.class, PlaylistServiceBinder.class);
        registerReceivers();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        playlistServiceLocator.unbind();
        unregisterReceivers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_playbar, container, false);
        
        songTitle = (TextView) view.findViewById(R.id.text_now_playing);
        playPause = ((ImageButton) view.findViewById(R.id.btn_play_pause));

        playPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlaylistService service = playlistServiceLocator.getService();
                    if (service.isPlaying()) {
                        service.pause();
                    } else {
                        service.play();
                    }
                    smartSetPlayPauseImage(service);
                }catch (ServiceNotBoundException e) {
                    Log.wtf(TAG, e);
                }
            }

        });

        ((ImageButton) view.findViewById(R.id.btn_skip))
            .setOnClickListener(new OnClickListener() {
    
                @Override
                public void onClick(View v) {
                    try {
                        PlaylistService service = playlistServiceLocator.getService();
                        service.skip();
                    } catch (ServiceNotBoundException e) {
                        Log.wtf(TAG, e);
                    }
                }
            });

        return view;
    }

    private void setPlayImage() {
        playPause.setImageDrawable(getResources().getDrawable(R.drawable.av_play));
    }

    private void setPauseImage(){
        playPause.setImageDrawable(getResources().getDrawable(R.drawable.av_pause));
    }

    private void smartSetPlayPauseImage(PlaylistService service){
        if(service.isPlaying()){
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
        this.registrar
            .addAction(PlaylistService.ACTION_SONG_PLAYING, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    SongMetadata song = intent.getParcelableExtra(PlaylistService.EXTRA_SONG);
                    if (song != null) {
                        songTitle.setText(song.getTitle());
                    } else {
                        //TODO: what do we want to display when there are no songs to play?
                        songTitle.setText("");
                    }
                }
            })
            .addAction(PlaylistService.ACTION_PLAYING_AUDIO, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    setPauseImage();
                }
            })
            .addAction(PlaylistService.ACTION_PAUSED_AUDIO, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    setPlayImage();
                }
            })
            .register(this.getActivity());
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }

}
