package com.lastcrusade.fanclub.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.lastcrusade.fanclub.audio.IPlayer;
import com.lastcrusade.fanclub.audio.SingleFileAudioPlayer;
import com.lastcrusade.fanclub.util.BroadcastIntent;
import com.lastcrusade.fanclub.util.BroadcastRegistrar;
import com.lastcrusade.fanclub.util.IBroadcastActionHandler;

/**
 * An implementation of Service to provide background music playing functionality.  This uses ILocalBinder
 * to implement a local binder that enables binding activities to get access to the service.
 * 
 * @author Jesse Rosalia
 *
 */
public class AudioPlayerService extends Service implements IPlayer {

    /**
     * Broadcast action sent when the Audio Player service is paused.
     * 
     */
    public static final String ACTION_PAUSED   = AudioPlayerService.class.getName() + ".action.Paused";
    
    /**
     * Broadcast action sent when the Audio Player service starts playing.
     * 
     */
    public static final String ACTION_PLAYING  = AudioPlayerService.class.getName() + ".action.Playing";

    /**
     * Broadcast action sent when the Audio Player service is asked to skip a song.
     * 
     */
    public static final String ACTION_SKIPPING = AudioPlayerService.class.getName() + ".action.Skipping";

    private SingleFileAudioPlayer audioPlayer;
    private BroadcastRegistrar registrar;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class AudioPlayerServiceBinder extends Binder implements ILocalBinder<AudioPlayerService> {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        this.audioPlayer = new SingleFileAudioPlayer();
        registerReceivers();
        return new AudioPlayerServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceivers();
        return super.onUnbind(intent);
    }
    
    /**
     * Register intent receivers to control this service
     * 
     */
    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                //pause the song for incoming phone calls
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.CALL_STATE_RINGING)) {
                    pause();
                }
            }
        }).register(this);
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }
    
    @Override
    public void play() {
        this.audioPlayer.play();
        new BroadcastIntent(ACTION_PLAYING).send(this);
    }

    @Override
    public void pause() {
        this.audioPlayer.pause();
        new BroadcastIntent(ACTION_PAUSED).send(this);
    }

    @Override
    public void skip() {
        this.audioPlayer.skip();
        new BroadcastIntent(ACTION_SKIPPING).send(this);
    }

    public void setSongByPath(String filePath) {
        this.audioPlayer.setSongByPath(filePath);
    }

}
