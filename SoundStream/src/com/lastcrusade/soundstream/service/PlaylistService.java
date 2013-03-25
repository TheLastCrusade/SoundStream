package com.lastcrusade.soundstream.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.audio.AudioPlayerWithEvents;
import com.lastcrusade.soundstream.audio.IPlayer;
import com.lastcrusade.soundstream.audio.RemoteAudioPlayer;
import com.lastcrusade.soundstream.audio.SingleFileAudioPlayer;
import com.lastcrusade.soundstream.manager.PlaylistDataManager;
import com.lastcrusade.soundstream.model.Playlist;
import com.lastcrusade.soundstream.model.PlaylistEntry;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.util.BroadcastIntent;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.Toaster;

/**
 * This service is responsible for holding the play queue and sending songs to
 * the SingleFileAudioPlayer
 */
public class PlaylistService extends Service {

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

    public static final String ACTION_SONG_PLAYING     = PlaylistService.class + ".action.SongPlaying";
    public static final String EXTRA_SONG              = PlaylistService.class + ".extra.Song";

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
    private IPlayer               mThePlayer;
    private SingleFileAudioPlayer mAudioPlayer; //We might want to remove this
    private Playlist              mPlaylist;

    private Thread                mDataManagerThread;
    private PlaylistDataManager   mDataManager;

    private PlaylistEntry currentSong;
    private boolean localPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        //create the local player in a separate variable, and use that
        // as the player until we see a host connected
        this.mAudioPlayer  = new SingleFileAudioPlayer(this, (CustomApp)this.getApplication());
        //Assume we are local untill we connect to a host
        localPlayer = true;
        this.mThePlayer    = new AudioPlayerWithEvents(this.mAudioPlayer, this);
        this.mPlaylist     = new Playlist();
        
        registerReceivers();
        
        //start the data manager by default...it is disabled when
        // a host is connected
        startDataManager();
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
                //NOTE: this is an indicator that the song data can be deleted...therefore, we don't
                //want to set the flag until after the song has been played
                if (currentSong != null) {
                    currentSong.setPlayed(true);
                    currentSong = null;
                }
                // automatically play the next song, but only if we're not paused
                if (!mThePlayer.isPaused()) {
                    play();
                }
                new BroadcastIntent(ACTION_PLAYLIST_UPDATED).send(PlaylistService.this);
            }
        })
        .addAction(ConnectionService.ACTION_HOST_CONNECTED, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                mThePlayer = new AudioPlayerWithEvents(new RemoteAudioPlayer((CustomApp) getApplication()), context);
                localPlayer = false;
                stopDataManager();
            }
        })
        .addAction(ConnectionService.ACTION_HOST_DISCONNECTED, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                mThePlayer = mAudioPlayer;
                startDataManager();
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
        .addAction(MessagingService.ACTION_PLAYLIST_UPDATED_MESSAGE, new IBroadcastActionHandler() {

            @Override
            public void onReceiveAction(Context context, Intent intent) {
                List<SongMetadata> newList = intent.getParcelableArrayListExtra(MessagingService.EXTRA_SONG_METADATA);
                mPlaylist.clear();
                for (SongMetadata metadata : newList) {
                    PlaylistEntry entry = new PlaylistEntry(metadata);
                    //for the UI, assume that the song is "loaded" when displayed on the remote guest
                    //TODO: this should actually reflect the real status on the host, which requires
                    // modifying the message to send PlaylistEntry objects
                    entry.setLoaded(mDataManager == null);
                    mPlaylist.add(entry);
                }
                new BroadcastIntent(ACTION_PLAYLIST_UPDATED).send(PlaylistService.this);
            }
        })
        .register(this);
    }

    protected void startDataManager() {
        if (mDataManager == null) {
            mDataManager       = new PlaylistDataManager(PlaylistService.this, (CustomApp) getApplication());
            mDataManagerThread = new Thread(mDataManager, PlaylistDataManager.class.getSimpleName() + " Thread");
            mDataManagerThread.start();
        }
    }

    protected void stopDataManager() {
        if (mDataManager != null) {
            mDataManager.stopLoading();
            mDataManager = null;
            mDataManagerThread = null;
        }
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }
  
    public boolean isPlaying() {
        return this.mThePlayer.isPlaying();
    }

    public void play() {
        if (this.mThePlayer.isPaused()) {
            this.mThePlayer.resume();
        } else {
            boolean play = true;
            if(isLocalPlayer()) {
                play = setNextSong();
            }
            //we have stuff to play...play it and send a notification
            if (play) {
                this.mThePlayer.play();
            }
        }
    }

    /**
     * Helper method to manage all of the things we need to do to set a song
     * to play locally (e.g. on the host).
     */
    private boolean setNextSong() {
        if (!isLocalPlayer()) {
            throw new IllegalStateException("Cannot call setSong when using a remote player");
        }
        boolean songSet = false;
        //only 
        if (mPlaylist.size() > 0) {
            PlaylistEntry song = mPlaylist.getNextAvailableSong();
            //we've reached the end of the playlist...reset it to the beginning and try again
            if (song == null) {
                resetPlaylist();
                song = mPlaylist.getNextAvailableSong();
                Toaster.iToast(this, getString(R.string.playlist_finished));
            }
            //still no available music..this means we're waiting for data to come in
            //...display a warning, but don't play.
            if (song == null) {
                //TODO: instead of this, we may want to repost a message to wait for the next song to be available
                //stop the player
                this.mAudioPlayer.stop();
                //pop up the notice
                Toaster.iToast(this, getString(R.string.no_available_songs));
            } else {
                //we have a song available to play...play it!
                this.currentSong = song;
                this.mAudioPlayer.setSong(song.getFilePath(), song);
                //the song has been set...indicate this in the return value
                songSet = true;
            }
        } else {
            Toaster.iToast(this, getString(R.string.playlist_empty));
        }
        return songSet;
    }

    /**
     * 
     */
    private void resetPlaylist() {
        mPlaylist.reset();
        if (isLocalPlayer()) {
            //we may need to re-add entries to the data manager, for remote
            // loading
            for (PlaylistEntry entry : mPlaylist.getSongsToPlay()) {
                //add all of the entries to the load queue
                mDataManager.addToLoadQueue(entry);
            }
        }
    }

    public void pause() {
        this.mThePlayer.pause();
    }

    public void skip() {
        this.mThePlayer.skip();
    }

    private boolean isLocalPlayer() {
        return localPlayer;
    }

    public void addSong(SongMetadata metadata) {
        addSong(new PlaylistEntry(metadata));
    }

    public void addSong(PlaylistEntry entry) {
        //NOTE: the entries are shared between the playlist and the data loader...the loader
        // will load data into the same objects that are held in the playlist
        mPlaylist.add(entry);
        if (isLocalPlayer()) {
            mDataManager.addToLoadQueue(entry);
        }
        new BroadcastIntent(ACTION_PLAYLIST_UPDATED).send(this);
        ((CustomApp)this.getApplication()).getMessagingService().sendPlaylistMessage(mPlaylist.getSongsToPlay());
    }

    public List<PlaylistEntry> getPlaylistEntries() {
        return Collections.unmodifiableList(new ArrayList<PlaylistEntry>(mPlaylist.getSongsToPlay()));
    }
}
