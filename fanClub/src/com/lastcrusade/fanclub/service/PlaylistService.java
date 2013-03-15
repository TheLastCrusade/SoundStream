package com.lastcrusade.fanclub.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.lastcrusade.fanclub.audio.IPlayer;
import com.lastcrusade.fanclub.audio.SingleFileAudioPlayer;
import com.lastcrusade.fanclub.library.MediaStoreWrapper;
import com.lastcrusade.fanclub.library.SongNotFoundException;
import com.lastcrusade.fanclub.model.Playlist;
import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.util.BroadcastIntent;
import com.lastcrusade.fanclub.util.BroadcastRegistrar;
import com.lastcrusade.fanclub.util.IBroadcastActionHandler;
import com.lastcrusade.fanclub.util.Toaster;
import com.lastcrusade.fanclub.R;

/**
 * This service is responsible for holding the play queue and sending songs to
 * the SingleFileAudioPlayer
 */
public class PlaylistService extends Service implements IPlayer {

    /**
     * Broadcast action sent when the Audio Player service is paused.
     */
    public static final String ACTION_PAUSED_AUDIO = PlaylistService.class
            .getName() + ".action.PausedAudio";

    /**
     * Broadcast action sent when the Audio Player service starts playing.
     */
    public static final String ACTION_PLAYING_AUDIO = PlaylistService.class
            .getName() + ".action.PlayingAudio";

    /**
     * Broadcast action sent when the Audio Player service is asked to skip a
     * song.
     */
    public static final String ACTION_SKIPPING_AUDIO = PlaylistService.class
            .getName() + ".action.SkippingAudio";

    /**
     * Broadcast action sent when the playlist gets updated
     */
    public static final String ACTION_PLAYLIST_UPDATED = PlaylistService.class + ".action.PlaylistUpdated";

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
    private Playlist mPlaylist;

    @Override
    public IBinder onBind(Intent intent) {
        this.audioPlayer = new SingleFileAudioPlayer(this);

        this.mPlaylist = new Playlist();
        // TODO: kick off a thread to feed the monster that is the audio service
        registerReceivers();
        return new PlaylistServiceBinder();
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

        this.registrar.addAction(SingleFileAudioPlayer.ACTION_SONG_FINISHED, new IBroadcastActionHandler() {

            @Override
            public void onReceiveAction(Context context, Intent intent) {
                mPlaylist.moveNext();
                new BroadcastIntent(ACTION_PLAYLIST_UPDATED).send(PlaylistService.this);
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
        if(mPlaylist.size() > 0){
            setSong(mPlaylist.getNextSong());
            this.audioPlayer.play();
            new BroadcastIntent(ACTION_PLAYING_AUDIO).send(this);
        } else {
            Toaster.iToast(this, getString(R.string.playlist_empty));
        }
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
        new BroadcastIntent(SingleFileAudioPlayer.ACTION_SONG_FINISHED).send(this);
    }

    private void setSong(SongMetadata songData) {
        MediaStoreWrapper msw = new  MediaStoreWrapper(this);
        try {
            Song s = msw.loadSongData(songData);
            this.audioPlayer.setSongByPath(s.getFilePath());
        } catch (SongNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addSong(SongMetadata metadata) {
        mPlaylist.add(metadata);
        new BroadcastIntent(ACTION_PLAYLIST_UPDATED).send(this);
    }

    public Playlist getPlaylist() {
        return mPlaylist;
    }
}
