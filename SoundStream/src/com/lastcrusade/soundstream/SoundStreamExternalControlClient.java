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

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.os.Build;

import com.lastcrusade.soundstream.components.ExternalMusicControlHandler;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.RemoteControlClientCompat;

/**
 * Manage the interface to Android's external music controls.
 * 
 * This class manages:
 *  Registering the MEDIA_BUTTON handler
 *  Registering a RemoteControlClient object (for lock screen remote control on ICS +)
 * 
 * @author thejenix
 *
 */
public class SoundStreamExternalControlClient {

    private Context context;
    private BroadcastRegistrar registrar;
    private RemoteControlClientCompat mRemoteControlClientCompat;
    private ComponentName mediaButtonEventReceiver;
    private SongMetadata currentSong;

    public SoundStreamExternalControlClient(Context context) {
        this.context = context;
        
        buildClient();
        //we want the receivers even before we've registered the client,
        // to accumulate state
        registerReceivers();
    }
    
    /**
     * 
     */
    private void buildClient() {
        Class<?> receiverClass = ExternalMusicControlHandler.class;
        this.mediaButtonEventReceiver = new ComponentName(
                context.getPackageName(),
                receiverClass.getName());
        // build the PendingIntent for the remote control client
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mediaButtonEventReceiver);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, mediaButtonIntent, 0);
        // create and register the remote control client
        //NOTE: the client is created in all cases, but we call a method to
        // set up and register it...this is so we can gracefully handle older versions of android.
        mRemoteControlClientCompat = new RemoteControlClientCompat(mediaPendingIntent);
    }

    public void registerClient() {
        registerRemoteControlClient();
        updateRemoteControlClient();
    }
    
    public void unregisterClient() {
        unregisterRemoteControlClient();
    }

    public void unregister() {
        unregisterReceivers();
        unregisterRemoteControlClient();    
    }

    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar
            .addAction(PlaylistService.ACTION_PAUSED_AUDIO, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    setPaused();
                }
            })
            .addAction(PlaylistService.ACTION_PLAYING_AUDIO, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    setPlaying();
                }
            })
            .addAction(PlaylistService.ACTION_SONG_PLAYING, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    SongMetadata song = intent.getParcelableExtra(PlaylistService.EXTRA_SONG);
                    setCurrentSong(song);
                }
            })
            .register(this.context);
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }

    private void registerRemoteControlClient() {
        AudioManager audioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.registerMediaButtonEventReceiver(mediaButtonEventReceiver);
        registerRemoteControlClientCompat(audioManager);
    }

    private void unregisterRemoteControlClient() {
        AudioManager audioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.unregisterMediaButtonEventReceiver(mediaButtonEventReceiver);
        unregisterRemoteControlClientCompat();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void registerRemoteControlClientCompat(AudioManager audioManager) {
        if (mRemoteControlClientCompat.isSupportsRemoteControl()) {
            mRemoteControlClientCompat.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_NEXT |
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE);
            mRemoteControlClientCompat.register(audioManager);
        }
    }

    private void unregisterRemoteControlClientCompat() {
        if (mRemoteControlClientCompat.isSupportsRemoteControl()) {
            mRemoteControlClientCompat.unregister();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setPlaying() {
        if (mRemoteControlClientCompat.isSupportsRemoteControl()) {
            mRemoteControlClientCompat.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setPaused() {
        if (mRemoteControlClientCompat.isSupportsRemoteControl()) {
            mRemoteControlClientCompat.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
        }
    }

    public void setCurrentSong(SongMetadata song) {
        this.currentSong = song;
        updateRemoteControlClient();
    }

    private void updateRemoteControlClient() {
        if (mRemoteControlClientCompat.isSupportsRemoteControl() && currentSong != null) {
            mRemoteControlClientCompat.editMetadata(true)
                .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, currentSong.getArtist())
                .putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, currentSong.getAlbum())
                .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, currentSong.getTitle())
        //        .putLong(MediaMetadataRetriever.METADATA_KEY_DURATION,
        //                playingItem.getDuration())
        //        // TODO: fetch real item artwork
        //        .putBitmap(
        //                RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK,
        //                mDummyAlbumArt)
                .apply();
        }
    }
}
