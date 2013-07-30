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
import java.io.FileInputStream;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.lastcrusade.soundstream.model.PlaylistEntry;
import com.lastcrusade.soundstream.service.MessagingService;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.util.LocalBroadcastIntent;

/**
 * A simple audio player that expects an audio file to be located in an
 * accessible folder.
 * 
 * This player takes in the path to the file to play, and can play
 * 
 * @author Jesse Rosalia
 */
public class SingleFileAudioPlayer implements IPlayer, IDuckable {

    public static final String ACTION_SONG_FINISHED = SingleFileAudioPlayer.class.getName() + ".action.SongFinished";

    private static final String TAG = SingleFileAudioPlayer.class.getName();

    private static final float DUCK_VOLUME = 0.1f;

    private static final float NORMAL_VOLUME = 1.0f;

    private PlaylistEntry entry;
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
                        new LocalBroadcastIntent(SingleFileAudioPlayer.ACTION_SONG_FINISHED).send(SingleFileAudioPlayer.this.context);
                    }
        });
    }

    /**
     * Clear the song that is currently playing.  This is called
     * when stopping and tearing down the player.
     * 
     */
    private void clearSong() {
        setEntryAndNotify(null);
    }

    /**
     * Set the song path and accompanying metadata to play.
     * 
     * NOTE: these can be null, to clear the currently playing song.
     * 
     * @param filePath
     * @param song
     */
    public void setSong(PlaylistEntry song) {
        setEntryAndNotify(song);
    }

    /**
     * A helper method to set entry and send a broadcast intent
     * to notify listeners of the song change.
     * 
     * @param entry The entry to set, or null (note that a broadcast intent goes out in either case).
     */
    private void setEntryAndNotify(PlaylistEntry entry) {
        this.entry = entry;
        //This is sending a playlist entry not a SongMetadata
        new LocalBroadcastIntent(PlaylistService.ACTION_CURRENT_SONG)
            .putExtra(PlaylistService.EXTRA_SONG, this.entry)
            .send(this.context);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying() && !paused;
    }

    public void play() {
        if (isValidPath()) {
            try {
                if (player.isPlaying()) {
                    player.stop();
                }
                this.paused = false;
                player.reset();
                //changed to use the underlying file descriptor, because this doesnt want
                // to work on a Samsung Galaxy S3 and other phones
                //(see http://stackoverflow.com/questions/1972027/android-playing-mp3-from-byte)
                FileInputStream fis = new FileInputStream(entry.getFilePath());
                player.setDataSource(fis.getFD());
                player.prepare();
                player.start();
                this.messagingService.getService().sendPlayStatusMessage(
                        this.entry, true);
            } catch (Exception e) {
                Log.wtf(TAG, "Unable to play song: " + entry.getFilePath());
            }
        } else {
            Log.w(TAG, "File Path was not valid");
        }
    }

    private boolean isValidPath() {
        boolean isValid = false;
        try {
            //This will fail and throw and Exception if the filepath is bad
            new File((new File(entry.getFilePath()).getParentFile().list())[0]).exists();
            isValid = true;
        } catch (Exception e) {
            isValid = false;
            e.printStackTrace();
        }
        return isValid;
    }

    @Override
    public void pause() {
        if (player.isPlaying()) {
            player.pause();
        }
        this.paused = true;

        if (this.entry != null) {
            try {
                this.messagingService
                    .getService()
                    .sendPlayStatusMessage(this.entry, false);
            } catch (ServiceNotBoundException e) {
                Log.wtf(TAG, e);
            }
        } else {
            Log.wtf(TAG, "pause called without playing song.  This isnt right.");
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
        if (player.isPlaying()) {
            player.stop();
        }
        if (this.entry != null) {
            try {
                this.messagingService
                    .getService()
                    .sendPlayStatusMessage(this.entry, false);
            } catch (ServiceNotBoundException e) {
                Log.wtf(TAG, e);
            }
        }
        this.clearSong();
    }

    @Override
    public void resume() {
        player.start();
        paused = false;
        if (this.entry != null) {
            try {
                this.messagingService.getService().sendPlayStatusMessage(this.entry, true);
            } catch (ServiceNotBoundException e) {
                Log.wtf(TAG, e);
            }
        } else {
            Log.wtf(TAG, "resume called without paused song.  This isnt right.");
        }
    }

    @Override
    public void skip() {
        //send this action to move to the next song
        new LocalBroadcastIntent(SingleFileAudioPlayer.ACTION_SONG_FINISHED).send(this.context);
    }

    public boolean isPaused() {
        return paused;
    }
    
    /* (non-Javadoc)
     * @see com.lastcrusade.soundstream.audio.IDuckable#duck()
     */
    @Override
    public void duck() {
        player.setVolume(DUCK_VOLUME, DUCK_VOLUME);
    }
    
    /* (non-Javadoc)
     * @see com.lastcrusade.soundstream.audio.IDuckable#unduck()
     */
    @Override
    public void unduck() {
        player.setVolume(NORMAL_VOLUME, NORMAL_VOLUME);
    }
}
