package com.lastcrusade.soundstream.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

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
import com.lastcrusade.soundstream.net.message.PlayStatusMessage;
import com.lastcrusade.soundstream.service.MusicLibraryService.MusicLibraryServiceBinder;
import com.lastcrusade.soundstream.service.MessagingService.MessagingServiceBinder;
import com.lastcrusade.soundstream.util.BroadcastIntent;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.SongMetadataUtils;
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
    
    public static final String ACTION_SONG_REMOVED     = PlaylistService.class + ".action.SongRemoved";
    
    public static final String ACTION_SONG_ADDED     = PlaylistService.class + ".action.SongAdded";
    

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
    private SingleFileAudioPlayer mAudioPlayer; //TODO remove this when we add stop to IPlayer
    private Playlist              mPlaylist;

    private Thread                mDataManagerThread;
    private PlaylistDataManager   mDataManager;

    private PlaylistEntry currentSong;
    private boolean isLocalPlayer;

    private ServiceLocator<MusicLibraryService> musicLibraryLocator;
    private ServiceLocator<MessagingService> messagingServiceLocator;

    @Override
    public IBinder onBind(Intent intent) {
        messagingServiceLocator = new ServiceLocator<MessagingService>(
                this, MessagingService.class, MessagingServiceBinder.class);

        //create the local player in a separate variable, and use that
        // as the player until we see a host connected
        this.mAudioPlayer  = new SingleFileAudioPlayer(this, messagingServiceLocator);
        //Assume we are local until we connect to a host
        isLocalPlayer      = true;
        this.mThePlayer    = new AudioPlayerWithEvents(this.mAudioPlayer, this);
        this.mPlaylist     = new Playlist();
        
        musicLibraryLocator = new ServiceLocator<MusicLibraryService>(
                this, MusicLibraryService.class, MusicLibraryServiceBinder.class);

        registerReceivers();
        
        //start the data manager by default...it is disabled when
        // a host is connected
        startDataManager();
        return new PlaylistServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceivers();
        messagingServiceLocator.unbind();
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
                    getMessagingService()
                        .sendSongStatusMessage(currentSong);
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
                mThePlayer = new AudioPlayerWithEvents(
                        new RemoteAudioPlayer(
                                PlaylistService.this,
                                messagingServiceLocator),
                        context
                );
                isLocalPlayer = false;
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
        .addAction(ConnectionService.ACTION_GUEST_CONNECTED, new IBroadcastActionHandler() {
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                getMessagingService().sendPlaylistMessage(mPlaylist.getSongsToPlay());
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
        .addAction(MessagingService.ACTION_ADD_TO_PLAYLIST_MESSAGE, new IBroadcastActionHandler() {

            @Override
            public void onReceiveAction(Context context, Intent intent) {
                if (!isLocalPlayer) {
                    Log.wtf(TAG, "Received AddToPlaylistMessage on guest...these messages are only for hosts");
                }
                String macAddress = intent.getStringExtra(MessagingService.EXTRA_ADDRESS);
                long   songId     = intent.getLongExtra(  MessagingService.EXTRA_SONG_ID, SongMetadata.UNKNOWN_SONG);
                
                SongMetadata song = getMusicLibraryService().lookupSongByAddressAndId(macAddress, songId);
                addSong(song);
            }
        })
        .addAction(MessagingService.ACTION_BUMP_SONG_ON_PLAYLIST_MESSAGE, new IBroadcastActionHandler() {

            @Override
            public void onReceiveAction(Context context, Intent intent) {
                if (!isLocalPlayer) {
                    Log.wtf(TAG, "Received BumpSongOnPlaylist on guest...these messages are only for hosts");
                }
                String macAddress = intent.getStringExtra(MessagingService.EXTRA_ADDRESS);
                long   songId     = intent.getLongExtra(  MessagingService.EXTRA_SONG_ID, SongMetadata.UNKNOWN_SONG);
                
                SongMetadata song = getMusicLibraryService().lookupSongByAddressAndId(macAddress, songId);
                PlaylistEntry entry = mPlaylist.findEntryForSong(song);
                if (entry != null) {
                    bumpSong(entry);
                } else {
                    Log.e(TAG, "Attempting to bump a song that is not in our playlist: " + song);
                }
            }
        })
        .addAction(MessagingService.ACTION_REMOVE_FROM_PLAYLIST_MESSAGE, new IBroadcastActionHandler() {

            @Override
            public void onReceiveAction(Context context, Intent intent) {
                if (!isLocalPlayer) {
                    Log.wtf(TAG, "Received AddToPlaylistMessage on guest...these messages are only for hosts");
                }
                String macAddress = intent.getStringExtra(MessagingService.EXTRA_ADDRESS);
                long   songId     = intent.getLongExtra(  MessagingService.EXTRA_SONG_ID, SongMetadata.UNKNOWN_SONG);
                
                //NOTE: only remove if its not the currently playing song.
                //TODO: may need a better message back to the remote fan
                SongMetadata song = getMusicLibraryService().lookupSongByAddressAndId(macAddress, songId);
                if (currentSong == null || !SongMetadataUtils.isTheSameSong(song, currentSong)) {
                    removeSong(song);
                }
                getMessagingService().sendPlaylistMessage(mPlaylist.getSongsToPlay());
            }
        })
        .addAction(MessagingService.ACTION_PLAYLIST_UPDATED_MESSAGE, new IBroadcastActionHandler() {

            @Override
            public void onReceiveAction(Context context, Intent intent) {
                if (isLocalPlayer) {
                    Log.wtf(TAG, "Received PlaylistUpdateMessage as host...these messages are only for guests");
                }
                List<PlaylistEntry> newList =
                        intent.getParcelableArrayListExtra(MessagingService.EXTRA_PLAYLIST_ENTRY);
                mPlaylist.clear();
                for (PlaylistEntry entry : newList) {
                    mPlaylist.add(entry);
                }
                new BroadcastIntent(ACTION_PLAYLIST_UPDATED).send(PlaylistService.this);
            }
        })
        .addAction(MessagingService.ACTION_SONG_STATUS_MESSAGE, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                if (isLocalPlayer) {
                    Log.wtf(TAG, "Received SongStatusMessage as host...these messages are only for guests");
                }
                String macAddress = intent.getStringExtra(MessagingService.EXTRA_ADDRESS);
                long   songId     = intent.getLongExtra(  MessagingService.EXTRA_SONG_ID, SongMetadata.UNKNOWN_SONG);
                boolean loaded    = intent.getBooleanExtra(MessagingService.EXTRA_LOADED, false);
                boolean played    = intent.getBooleanExtra(MessagingService.EXTRA_PLAYED, false);

                SongMetadata song = getMusicLibraryService().lookupSongByAddressAndId(macAddress, songId);
                PlaylistEntry entry = mPlaylist.findEntryForSong(song);
                if (entry != null) {
                    entry.setLoaded(loaded);
                    entry.setPlayed(played);
                } else {
                    Log.e(TAG, "Attempting to bump a song that is not in our playlist: " + song);
                }
            }
        })
        .register(this);
    }

    protected void startDataManager() {
        if (mDataManager == null) {
            mDataManager       = new PlaylistDataManager(PlaylistService.this, messagingServiceLocator);
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
            if(isLocalPlayer) {
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
        if (!isLocalPlayer) {
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
        if (isLocalPlayer) {
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

    public void addSong(SongMetadata metadata) {
        addSong(new PlaylistEntry(metadata));
    }

    public void addSong(PlaylistEntry entry) {
        //NOTE: the entries are shared between the playlist and the data loader...the loader
        // will load data into the same objects that are held in the playlist
        mPlaylist.add(entry);
        if (isLocalPlayer) {
            mDataManager.addToLoadQueue(entry);
        }
        new BroadcastIntent(ACTION_SONG_ADDED)
            .putExtra(EXTRA_SONG, entry)
            .send(this);
        // send an intent to the fragments that the playlist is updated
        new BroadcastIntent(ACTION_PLAYLIST_UPDATED).send(this);

        if (isLocalPlayer) {
            //send a message to the guests with the new playlist
            getMessagingService().sendPlaylistMessage(mPlaylist.getSongsToPlay());
        } else {
            //send a message to the host to add this song
            getMessagingService().sendAddToPlaylistMessage(entry);
        }
    }
    
    public void removeSong(SongMetadata song) {
        PlaylistEntry entry = mPlaylist.findEntryForSong(song);
        if (entry != null) {
            mPlaylist.remove(entry);
            //broadcast the fact that a song has been removed
            new BroadcastIntent(ACTION_SONG_REMOVED)
                .putExtra(EXTRA_SONG, entry)
                .send(this);
            
            //broadcast the fact that the playlist has been updated
            new BroadcastIntent(ACTION_PLAYLIST_UPDATED).send(this);
    
            if (isLocalPlayer) {
                //send a message to the guests with the new playlist
                getMessagingService().sendPlaylistMessage(mPlaylist.getSongsToPlay());
            } else {
                //send a message to the host to remove this song
                getMessagingService().sendRemoveFromPlaylistMessage(entry);
            }
        } else {
            Log.e(TAG, "Attempting to remove a song that is not in our playlist: " + song);
        }
    }

    public List<PlaylistEntry> getPlaylistEntries() {
        return Collections.unmodifiableList(new ArrayList<PlaylistEntry>(mPlaylist.getSongsToPlay()));
    }

    private IMessagingService getMessagingService() {
        MessagingService messagingService = null;
        try {
            messagingService = this.messagingServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return messagingService;
    }

    public void bumpSong(PlaylistEntry entry){
        mPlaylist.bumpSong(entry);
        
        new BroadcastIntent(ACTION_PLAYLIST_UPDATED).send(this);
        
        if (isLocalPlayer) {
            //send a message to the guests with the new playlist
            getMessagingService().sendPlaylistMessage(mPlaylist.getSongsToPlay());
        } else {
            //send a message to the host to remove this song
            getMessagingService().sendBumpSongOnPlaylistMessage(entry);
        }
    }
    
    public PlaylistEntry getCurrentSong(){
        return currentSong;
    }
    
    public MusicLibraryService getMusicLibraryService() {
        MusicLibraryService musicLibraryService = null;
        try {
            musicLibraryService = this.musicLibraryLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return musicLibraryService;
    }

}
