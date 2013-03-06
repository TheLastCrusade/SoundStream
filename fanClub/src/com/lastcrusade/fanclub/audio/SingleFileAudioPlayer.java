package com.lastcrusade.fanclub.audio;

import java.io.File;

import android.media.MediaPlayer;
import android.util.Log;


/**
 * A simple audio player that expects an audio file to be located in an accessible folder.
 * 
 * This player takes in the path to the file to play, and can play
 * 
 * @author Jesse Rosalia
 *
 */
public class SingleFileAudioPlayer implements IPlayer {

    private static final String TAG = SingleFileAudioPlayer.class.getName();
    private String filePath;
    private MediaPlayer player;

    public SingleFileAudioPlayer() {
        this.player = new MediaPlayer();
    }
    
    public void setSongByPath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }
    
    public void play() {
        try {
            new File((new File(this.filePath).getParentFile().list())[0]).exists();
            if (player.isPlaying()) {
                player.stop();
            }
            player.reset();
            player.setDataSource(this.filePath);
            player.prepare();
            player.start();
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
    }

    @Override
    public void skip() {
        //since this is a single file player, skip == stop
        if (player.isPlaying()) {
            player.stop();
        }
    }
}
