package com.lastcrusade.soundstream.audio;

import java.io.File;

import com.lastcrusade.soundstream.util.BroadcastIntent;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

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

    public SingleFileAudioPlayer(final Context context) {
        this.player = new MediaPlayer();
        player.setOnCompletionListener(
                new OnCompletionListener(){
                    @Override public void onCompletion(MediaPlayer mp) {
                        new BroadcastIntent(SingleFileAudioPlayer.ACTION_SONG_FINISHED).send(context);
                    }
        });
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
            //This will fail and throw and Exception if the filepath is bad
            new File((new File(this.filePath).getParentFile().list())[0])
                    .exists();
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
        // since this is a single file player, skip == stop
        if (player.isPlaying()) {
            player.stop();
        }
    }
}
