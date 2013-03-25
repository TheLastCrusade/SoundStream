package com.lastcrusade.soundstream.audio;

import java.io.File;

import com.lastcrusade.soundstream.util.BroadcastIntent;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.net.message.PlayStatusMessage;
import com.lastcrusade.soundstream.service.PlaylistService;
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

    private CustomApp application;

    public SingleFileAudioPlayer(final Context context, CustomApp application) {
        this.player = new MediaPlayer();
        this.application = application;
        player.setOnCompletionListener(
                new OnCompletionListener(){
                    @Override public void onCompletion(MediaPlayer mp) {
                        new BroadcastIntent(SingleFileAudioPlayer.ACTION_SONG_FINISHED).send(context);
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
            .send(this.application);
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
            ((CustomApp)application)
                .getMessagingService()
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
        ((CustomApp)application).getMessagingService().sendPlayStatusMessage(PlayStatusMessage.PAUSE_MESSAGE);
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
        new BroadcastIntent(PlaylistService.ACTION_PAUSED_AUDIO).send(this.application);
        ((CustomApp)application).getMessagingService().sendPlayStatusMessage("Pause");
    }

    @Override
    public void resume() {
        player.start();
        paused = false;
        ((CustomApp)application).getMessagingService().sendPlayStatusMessage("Play");
    }

    @Override
    public void skip() {
        // since this is a single file player, skip == stop
        if (player.isPlaying()) {
            player.stop();
        }
        //send this action to move to the next song
        new BroadcastIntent(SingleFileAudioPlayer.ACTION_SONG_FINISHED).send(this.application);
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }
}
