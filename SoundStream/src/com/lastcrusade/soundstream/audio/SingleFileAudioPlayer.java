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

package com.lastcrusade.soundstream.audio;

import java.io.File;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.net.message.PlayStatusMessage;
import com.lastcrusade.soundstream.service.MessagingService;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.util.BroadcastIntent;

/**
 * A simple audio player that expects an audio file to be located in an
 * accessible folder.
 * 
 * This player takes in the path to the file to play, and can play
 * 
 * @author Jesse Rosalia
 */
public class SingleFileAudioPlayer implements IPlayer {

    public static final String ACTION_SONG_FINISHED = SingleFileAudioPlayer.class.getName() + ".action.SongFinished";

    private static final String TAG = SingleFileAudioPlayer.class.getName();
    private String filePath;
    private MediaPlayer player;

    private boolean paused;

    private ServiceLocator<MessagingService> messagingService;

    private Context context;

    public SingleFileAudioPlayer(Context context, ServiceLocator<MessagingService> messagingServiceLocator) {
        this.player = new MediaPlayer();
        this.context = context;
        this.messagingService = messagingServiceLocator;
        player.setOnCompletionListener(
                new OnCompletionListener(){
                    @Override public void onCompletion(MediaPlayer mp) {
                        new BroadcastIntent(SingleFileAudioPlayer.ACTION_SONG_FINISHED).send(SingleFileAudioPlayer.this.context);
                    }
        });
    }

    /**
     * Set the song path and accompanying metadata to play.
     * 
     * NOTE: these can be null, to clear the currently playing song.
     * 
     * @param filePath
     * @param song
     */
    public void setSong(String filePath, SongMetadata song) {
        this.filePath = filePath;
        new BroadcastIntent(PlaylistService.ACTION_SONG_PLAYING)
            .putExtra(PlaylistService.EXTRA_SONG, song)
            .send(this.context);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying() && !paused;
    }

    public void play() {
        try {
            //This will fail and throw and Exception if the filepath is bad
            new File((new File(this.filePath).getParentFile().list())[0])
                    .exists();
            if (player.isPlaying()) {
                player.stop();
            }
            this.paused = false;
            player.reset();
            player.setDataSource(this.filePath);
            player.prepare();
            player.start();
            this.messagingService
                .getService()
                .sendPlayStatusMessage(PlayStatusMessage.PLAY_MESSAGE);
        } catch (Exception e) {
            Log.wtf(TAG, "Unable to play song: " + this.filePath);
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        if (player.isPlaying()) {
            player.pause();
        }
        this.paused = true;

        try {
            this.messagingService
                .getService()
                .sendPlayStatusMessage(PlayStatusMessage.PAUSE_MESSAGE);
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
    }


    /**
     * Stop the player, and clear the active song.
     * 
     * Note that currently, stop is not a function exposed to the rest of the application
     * so we report that the player is "paused".
     * 
     */
    public void stop() {
        this.paused = false;
        this.player.stop();
        this.setSong(null, null);
        //indicate the system is paused
        new BroadcastIntent(PlaylistService.ACTION_PAUSED_AUDIO).send(this.context);
        try {
            this.messagingService
                .getService()
                .sendPlayStatusMessage(PlayStatusMessage.PAUSE_MESSAGE);
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
    }

    @Override
    public void resume() {
        player.start();
        paused = false;
        try {
            this.messagingService.getService().sendPlayStatusMessage("Play");
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
    }

    @Override
    public void skip() {
        // since this is a single file player, skip == stop
        if (player.isPlaying()) {
            player.stop();
        }
        //send this action to move to the next song
        new BroadcastIntent(SingleFileAudioPlayer.ACTION_SONG_FINISHED).send(this.context);
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }
}
