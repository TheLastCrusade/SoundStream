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
 * This service is responsible for holding the play queue, and feeding songs to
 * the Audio player service.
 * 
 * @author Jesse Rosalia
 * 
 */
public class PlaylistService extends Service implements IPlayer {

    /**
     * Broadcast action sent when the Audio Player service is paused.
     * 
     */
    public static final String ACTION_PAUSED_AUDIO = PlaylistService.class
            .getName() + ".action.PausedAudio";

    /**
     * Broadcast action sent when the Audio Player service starts playing.
     * 
     */
    public static final String ACTION_PLAYING_AUDIO = PlaylistService.class
            .getName() + ".action.PlayingAudio";

    /**
     * Broadcast action sent when the Audio Player service is asked to skip a
     * song.
     * 
     */
    public static final String ACTION_SKIPPING_AUDIO = PlaylistService.class
            .getName() + ".action.SkippingAudio";

    private static final String TAG = PlaylistService.class.getName();

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class PlaylistServiceBinder extends Binder implements
            ILocalBinder<PlaylistService> {
        public PlaylistService getService() {
            return PlaylistService.this;
        }
    }

    private BroadcastRegistrar    registrar;
    private SingleFileAudioPlayer audioPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        this.audioPlayer = new SingleFileAudioPlayer();
        // TODO: kick off a thread to feed the monster that is the audio service
        return new PlaylistServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
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
    public boolean isPlaying() {
        return this.audioPlayer.isPlaying();
    }

    @Override
    public void play() {
        this.audioPlayer.play();
        new BroadcastIntent(ACTION_PLAYING_AUDIO).send(this);
    }

    @Override
    public void pause() {
        this.audioPlayer.pause();
        new BroadcastIntent(ACTION_PAUSED_AUDIO).send(this);
    }

    @Override
    public void skip() {
        this.audioPlayer.skip();
        new BroadcastIntent(ACTION_SKIPPING_AUDIO).send(this);
    }

    public void setSongByPath(String filePath) {
        this.audioPlayer.setSongByPath(filePath);
    }
}
