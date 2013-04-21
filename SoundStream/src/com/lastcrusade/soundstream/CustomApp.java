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

package com.lastcrusade.soundstream;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.util.Log;

import com.lastcrusade.soundstream.service.ConnectionService;
import com.lastcrusade.soundstream.service.MessagingService;
import com.lastcrusade.soundstream.service.MusicLibraryService;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.service.UserListService;

public class CustomApp extends Application {
    private final String TAG = CustomApp.class.getName();
    
    private ServiceLocator<ConnectionService>   connectionServiceLocator;
    private ServiceLocator<MessagingService>    messagingServiceLocator;
    private ServiceLocator<MusicLibraryService> musicLibraryLocator;
    private ServiceLocator<PlaylistService>     playlistServiceLocator;
    private ServiceLocator<UserListService>     userListServiceLocator;

    private SoundStreamExternalControlClient externalControlClient;

    public CustomApp() {
        super();
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        createServiceLocators();

        registerExternalControlClient();
        
        requestAudio();
    }
    
    private void registerExternalControlClient() {
        externalControlClient = new SoundStreamExternalControlClient(this);
    }

    private void unregisterExtranlControlClient() {
        externalControlClient.unregister();
    }

    private void requestAudio() {
        //NOTE: we need to request the audio for the remote controls to work, but
        // we also want to handle things like audio ducking and pausing here.
        AudioManager myAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        myAudioManager.requestAudioFocus(new OnAudioFocusChangeListener() {

            @Override
            public void onAudioFocusChange(int focusChange) {
                //TODO: duck audio or pause in other cases where focus has changed.
                switch (focusChange) {
                //handle loss of focus, which includes when a phonecall is coming in
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //NOTE: this calls the playlist service directly, because we want instant action.
                    getPlaylistService().pause();
                    break;
                }
            }
            
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void onTerminate() {
        unregisterExtranlControlClient();
        super.onTerminate();
    }

    public void createServiceLocators() {
        //All of these services should exist for the lifetime of the application
        // bind them here so that you can quickly bind to them in the fragments
        connectionServiceLocator = new ServiceLocator<ConnectionService>(
                this, ConnectionService.class, ConnectionService.ConnectionServiceBinder.class);

        messagingServiceLocator = new ServiceLocator<MessagingService>(
                this, MessagingService.class, MessagingService.MessagingServiceBinder.class);

        musicLibraryLocator = new ServiceLocator<MusicLibraryService>(
                this, MusicLibraryService.class, MusicLibraryService.MusicLibraryServiceBinder.class);

        playlistServiceLocator = new ServiceLocator<PlaylistService>(
                this, PlaylistService.class, PlaylistService.PlaylistServiceBinder.class);

        userListServiceLocator = new ServiceLocator<UserListService>(
                this, UserListService.class, UserListService.UserListServiceBinder.class);
    }

    private PlaylistService getPlaylistService() {
        PlaylistService playlistService = null;
        try{
            playlistService = this.playlistServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return playlistService;
    }
}
