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

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.util.Log;

import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.model.UserList;
import com.lastcrusade.soundstream.service.ConnectionService;
import com.lastcrusade.soundstream.service.IMessagingService;
import com.lastcrusade.soundstream.service.MessagingService;
import com.lastcrusade.soundstream.service.MusicLibraryService;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.service.UserListService;
import com.lastcrusade.soundstream.util.BluetoothUtils;
import com.lastcrusade.soundstream.util.BroadcastIntent;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;

public class CustomApp extends Application {
    private final String TAG = CustomApp.class.getName();
    
    private UserList userList;
    
    private ServiceLocator<ConnectionService>   connectionServiceLocator;
    private ServiceLocator<MessagingService>    messagingServiceLocator;
    private ServiceLocator<MusicLibraryService> musicLibraryLocator;
    private ServiceLocator<PlaylistService>     playlistServiceLocator;
    private ServiceLocator<UserListService>     userListServiceLocator;

    private BroadcastRegistrar registrar;

    private SoundStreamExternalControlClient externalControlClient;

    public CustomApp() {
        super();
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        userList = new UserList();

        createServiceLocators();

        registerReceivers();
        
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
        unregisterReceivers();
        externalControlClient.unregister();
        super.onTerminate();
    }

    public void createServiceLocators() {
        connectionServiceLocator = new ServiceLocator<ConnectionService>(
                this, ConnectionService.class, ConnectionService.ConnectionServiceBinder.class);

        connectionServiceLocator.setOnBindListener(new ServiceLocator.IOnBindListener() {

            @Override
            public void onServiceBound() {
                //TODO: Move this to something like connect activity or the connection fragment
                addSelfToUserList();
            }
        });
        messagingServiceLocator = new ServiceLocator<MessagingService>(
                this, MessagingService.class, MessagingService.MessagingServiceBinder.class);

        musicLibraryLocator = new ServiceLocator<MusicLibraryService>(
                this, MusicLibraryService.class, MusicLibraryService.MusicLibraryServiceBinder.class);

        playlistServiceLocator = new ServiceLocator<PlaylistService>(
                this, PlaylistService.class, PlaylistService.PlaylistServiceBinder.class);

        userListServiceLocator = new ServiceLocator<UserListService>(
                this, UserListService.class, UserListService.UserListServiceBinder.class);
    }

    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar
            .addAction(ConnectionService.ACTION_HOST_CONNECTED, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    //send the library to the connected host
                    List<SongMetadata> metadata = getMusicLibraryService().getMyLibrary();
                    getMessagingService().sendLibraryMessageToHost(metadata);
                }
            })
            .register(this);
    }
 
    private void unregisterReceivers() {
        this.registrar.unregister();
    }

    public UserList getUserList(){
        return userList;
    }

    public void clearExternalUsers() {
        this.userList.clear();
        addSelfToUserList();
        new BroadcastIntent(UserList.ACTION_USER_LIST_UPDATE).send(CustomApp.this);
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

    private IMessagingService getMessagingService() {
        MessagingService messagingService = null;
        try {
            messagingService = this.messagingServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return messagingService;
    }
    
    public MusicLibraryService getMusicLibraryService() {
        MusicLibraryService musicLibraryService = null;
        try {
            musicLibraryService = this.musicLibraryLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return musicLibraryService;
    }
    
    public PlaylistService getPlaylistService() {
        PlaylistService playlistService = null;
        try{
            playlistService = this.playlistServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return playlistService;
    }

    public void addSelfToUserList() {
        getUserList().addUser(BluetoothUtils.getLocalBluetoothName(), BluetoothUtils.getLocalBluetoothMAC());
    }
}
