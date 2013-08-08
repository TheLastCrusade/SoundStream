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

package com.thelastcrusade.soundstream;

import android.app.Application;
import android.util.Log;

import com.thelastcrusade.soundstream.service.ConnectionService;
import com.thelastcrusade.soundstream.service.MessagingService;
import com.thelastcrusade.soundstream.service.MusicLibraryService;
import com.thelastcrusade.soundstream.service.PlaylistService;
import com.thelastcrusade.soundstream.service.ServiceLocator;
import com.thelastcrusade.soundstream.service.ServiceNotBoundException;
import com.thelastcrusade.soundstream.service.UserListService;

public class CustomApp extends Application {
    private final String TAG = CustomApp.class.getSimpleName();
    
    private ServiceLocator<ConnectionService>   connectionServiceLocator;
    private ServiceLocator<MessagingService>    messagingServiceLocator;
    private ServiceLocator<MusicLibraryService> musicLibraryLocator;
    private ServiceLocator<PlaylistService>     playlistServiceLocator;
    private ServiceLocator<UserListService>     userListServiceLocator;

    public CustomApp() {
        super();
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        createServiceLocators();
    }
    
    @Override
    public void onTerminate() {
        
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
