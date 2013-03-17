package com.lastcrusade.soundstream.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.audio.IPlayer;
import com.lastcrusade.soundstream.audio.RemoteAudioPlayer;
import com.lastcrusade.soundstream.audio.SingleFileAudioPlayer;
import com.lastcrusade.soundstream.library.MediaStoreWrapper;
import com.lastcrusade.soundstream.library.SongNotFoundException;
import com.lastcrusade.soundstream.model.Playlist;
import com.lastcrusade.soundstream.model.Song;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.util.BroadcastIntent;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.Toaster;

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
    private IPlayer               thePlayer;
    private SingleFileAudioPlayer audioPlayer;
    private Playlist mPlaylist;

    @Override
    public IBinder onBind(Intent intent) {
        //create the local player in a separate variable, and use that
        // as the player until we see a host connected
        this.audioPlayer  = new SingleFileAudioPlayer(this);
        this.thePlayer    = audioPlayer;

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
        })
        .addAction(SingleFileAudioPlayer.ACTION_SONG_FINISHED, new IBroadcastActionHandler() {

            @Override
            public void onReceiveAction(Context context, Intent intent) {
                mPlaylist.moveNext();
                // TODO: Right now, we don't move automatically to the next song.
                // Until we make that change, this is appropriate
                ((CustomApp)getApplication()).getMessagingService().sendPlayStatusMessage("Pause");
                new BroadcastIntent(ACTION_PLAYLIST_UPDATED).send(PlaylistService.this);
            }
        })
        .addAction(ConnectionService.ACTION_HOST_CONNECTED, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                thePlayer = new RemoteAudioPlayer((CustomApp) getApplication());
            }
        })
        .addAction(ConnectionService.ACTION_HOST_DISCONNECTED, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                thePlayer = audioPlayer;
            }
        })
        .addAction(MessagingService.ACTION_PAUSE_MESSAGE, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                pause();
            }
        })
        .addAction(MessagingService.ACTION_PLAY_MESSAGE, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                play();
            }
        })
        .addAction(MessagingService.ACTION_SKIP_MESSAGE, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                skip();
            }
        })
        .register(this);
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }
  
    @Override
    public boolean isPlaying() {
        return this.thePlayer.isPlaying();
    }

    @Override
    public void play() {
        if(isLocalPlayer()) {
            if (mPlaylist.size() > 0){
                setSong(mPlaylist.getNextSong());
                ((CustomApp)getApplication()).getMessagingService().sendPlayStatusMessage("Play");
            } else {
                Toaster.iToast(this, getString(R.string.playlist_empty));
                return; //SECOND RETURN PATH that makes the code nicer
            }
        }
        //we have stuff to play...play it and send a notification
        this.thePlayer.play();
        new BroadcastIntent(ACTION_PLAYING_AUDIO).send(this);
    }

    @Override
    public void pause() {
        this.thePlayer.pause();
        ((CustomApp)getApplication()).getMessagingService().sendPlayStatusMessage("Pause");
        new BroadcastIntent(ACTION_PAUSED_AUDIO).send(this);
    }

    @Override
    public void skip() {
        this.thePlayer.skip();
        if (isLocalPlayer()) {
            new BroadcastIntent(SingleFileAudioPlayer.ACTION_SONG_FINISHED).send(this);
        }
        new BroadcastIntent(ACTION_SKIPPING_AUDIO).send(this);
    }

    private boolean isLocalPlayer() {
        return this.thePlayer == this.audioPlayer;
    }

    private void setSong(SongMetadata songData) {
        if (!isLocalPlayer()) {
            throw new IllegalStateException("Cannot call setSong when using a remote player");
        }
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
