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
package com.thelastcrusade.soundstream.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.thelastcrusade.soundstream.library.SongNotFoundException;
import com.thelastcrusade.soundstream.model.SongMetadata;
import com.thelastcrusade.soundstream.net.MessageFuture;
import com.thelastcrusade.soundstream.net.MessageFuture.IFinishedHandler;
import com.thelastcrusade.soundstream.util.BroadcastRegistrar;
import com.thelastcrusade.soundstream.util.IBroadcastActionHandler;

/**
 * This service centralizes and manages song transfers, to avoid fragmenting
 * this functionality across peripherally related services (e.g. music library,
 * playlist, and connection service)
 * 
 * @author Jesse Rosalia
 */
public class TransferService extends Service {

    private static String TAG = TransferService.class.getSimpleName();

    private BroadcastRegistrar registrar;

    private ServiceLocator<MessagingService> messagingServiceLocator;
    private ServiceLocator<MusicLibraryService> musicLibraryServiceLocator;

    private Map<String, MessageFuture> messageFutures = new HashMap<String, MessageFuture>();

    public class TransferServiceBinder extends Binder implements
            ILocalBinder<TransferService> {
        public TransferService getService() {
            // Return this instance of LocalService so clients can call public
            // methods
            return TransferService.this;
        }
    }

    @Override
    public void onCreate() {
//        messagingServiceLocator = new ServiceLocator<MessagingService>(
//                this, MessagingService.class, MessagingServiceBinder.class);
//
        registerReceivers();
    }
    
    @Override
    public void onDestroy() {
        unregisterReceivers();
//        messagingServiceLocator.unbind();
//        userListServiceLocator.unbind();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new TransferServiceBinder();
    }


    /**
     * 
     */
    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar
            .addLocalAction(MessagingService.ACTION_REQUEST_SONG_MESSAGE, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    String fromAddr = intent.getStringExtra(MessagingService.EXTRA_ADDRESS);
                    long   songId   = intent.getLongExtra(  MessagingService.EXTRA_SONG_ID, SongMetadata.UNKNOWN_SONG);

                    if (songId == SongMetadata.UNKNOWN_SONG) {
                        Log.wtf(TAG, "REQUEST_SONG_MESSAGE action received without a valid song id");    
                    } else {
                        sendSongData(fromAddr, songId);
                    }
                }
            })
            .addLocalAction(MessagingService.ACTION_CANCEL_SONG_MESSAGE, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    String fromAddr = intent.getStringExtra(MessagingService.EXTRA_ADDRESS);
                    long   songId   = intent.getLongExtra(  MessagingService.EXTRA_SONG_ID, SongMetadata.UNKNOWN_SONG);

                    try {
                        if (songId == SongMetadata.UNKNOWN_SONG) {
                            Log.wtf(TAG, "REQUEST_SONG_MESSAGE action received without a valid song id");    
                        } else {
                            MessageFuture future = messageFutures.remove(makeKey(fromAddr, songId));
                            future.cancel();
                            
                        }
                    } catch (IOException e) {
                        Log.wtf(TAG, e);
                    }
                }
            })
//            .addLocalAction(MessagingService.ACTION_MESSAGE_FINISHED, new IBroadcastActionHandler() {
//
//                @Override
//                public void onReceiveAction(Context context, Intent intent) {
//                    String fromAddr = intent.getStringExtra(MessagingService.EXTRA_ADDRESS);
//                    long   songId   = intent.getLongExtra(  MessagingService.EXTRA_SONG_ID, SongMetadata.UNKNOWN_SONG);
//
//                    if (songId == SongMetadata.UNKNOWN_SONG) {
//                        Log.wtf(TAG, "REQUEST_SONG_MESSAGE action received without a valid song id");    
//                    } else {
//                        sendSongData(fromAddr, songId);
//                    }
//                }
//            })

            .register(this);
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }
    
    private String makeKey(String requestAddr, long songId) {
        return requestAddr + "_" + songId;
    }

    private void sendSongData(String requestAddr, long songId) {
        MessageFuture future = null;
        try {
            String filePath = getMusicLibraryService().getSongFilePath(songId);
            File songFile = new File(filePath);
            //send the transfer song message back to the requester
            future = getMessagingService().sendTransferSongMessage(requestAddr, songId, songFile.getName(), songFile.getCanonicalPath());
            final String key = makeKey(requestAddr, songId);
            this.messageFutures.put(key, future);
            future.setFinishedHandler(new IFinishedHandler() {

                @Override
                public void finished() {
                    messageFutures.remove(key);
                }
            });
        } catch (SongNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private MusicLibraryService getMusicLibraryService() {
        MusicLibraryService musicLibraryService = null;
        try{
            musicLibraryService = musicLibraryServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.w(TAG, "UserListService not bound");
        }
        return musicLibraryService;
    }
}
