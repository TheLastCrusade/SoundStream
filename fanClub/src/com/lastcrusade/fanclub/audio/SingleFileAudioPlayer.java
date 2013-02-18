package com.lastcrusade.fanclub.audio;

import java.io.File;

import android.media.MediaPlayer;
import android.util.Log;


/**
 * A simple audio player that expects an audio file to be located in an accessable folder.
 * 
 * This player takes in the path to the file to play, and can play
 * 
 * @author Jesse Rosalia
 *
 */
public class SingleFileAudioPlayer implements IPlayer {

    private static final String TAG = SingleFileAudioPlayer.class.getName();
    private String filePath;

    public SingleFileAudioPlayer() {
    }
    
    public void setSongByPath(String filePath) {
        this.filePath = filePath;
    }

    public void play() {
        try {
            new File((new File(this.filePath).getParentFile().list())[0]).exists();
            MediaPlayer player = new MediaPlayer();
            if (player.isPlaying()) {
                player.stop();
            }
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
        this.pause();
    }

    @Override
    public void skip() {
        // TODO Auto-generated method stub
        
    }
}
