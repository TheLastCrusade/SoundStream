package com.lastcrusade.fanclub.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.lastcrusade.fanclub.audio.IPlayer;
import com.lastcrusade.fanclub.audio.SingleFileAudioPlayer;

/**
 * An implementation of Service to provide background music playing functionality.  This uses ILocalBinder
 * to implement a local binder that enables binding activities to get access to the service.
 * 
 * @author Jesse Rosalia
 *
 */
public class AudioPlayerService extends Service implements IPlayer {

    private SingleFileAudioPlayer audioPlayer;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class AudioPlayerBinder extends Binder implements ILocalBinder<AudioPlayerService> {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        this.audioPlayer = new SingleFileAudioPlayer();
        return new AudioPlayerBinder();
    }

    @Override
    public void play() {
        this.audioPlayer.play();
    }

    @Override
    public void pause() {
        this.audioPlayer.pause();
    }

    @Override
    public void skip() {
        this.audioPlayer.skip();
    }

    public void setSongByPath(String filePath) {
        this.audioPlayer.setSongByPath(filePath);
    }

}
