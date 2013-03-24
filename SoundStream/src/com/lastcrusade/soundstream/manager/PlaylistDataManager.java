package com.lastcrusade.soundstream.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.library.MediaStoreWrapper;
import com.lastcrusade.soundstream.library.SongNotFoundException;
import com.lastcrusade.soundstream.model.PlaylistEntry;
import com.lastcrusade.soundstream.service.MessagingService;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.util.BroadcastIntent;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.SongMetadataUtils;

public class PlaylistDataManager implements Runnable {

    private static final String TAG = PlaylistDataManager.class.getName();

    private PlaylistService playlistService;
    private CustomApp application;
    private Queue<PlaylistEntry> loadQueue = new LinkedList<PlaylistEntry>();
    private Set<PlaylistEntry> remotelyLoaded = new HashSet<PlaylistEntry>();
    private Thread stoppingThread;
    private boolean running;
    private BroadcastRegistrar registrar;

    private long maxBytesToLoad = 512 * 1024 * 1024; //512MB default max bytes
    private long bytesRequested = 0;
    private float loadFactor    = 0.5f;
    
    private final Object entryMutex = new Object();

    public PlaylistDataManager(PlaylistService playlistService, CustomApp application) {
        this.playlistService = playlistService;
        this.application     = application;
    }

    @Override
    public void run() {
        registerReceivers();
        running = true;
        try {
            while (running) {
                //first, clean up any already played remote files...this frees up space
                // to request new files.
                clearOldLoadedFiles();

                //next, see if we can start loading any additional files
                while (!loadQueue.isEmpty() &&
                        loadQueue.peek().getFileSize() < (maxBytesToLoad - bytesRequested)) {
                    PlaylistEntry entry = loadQueue.poll();
                    if (entry.isLocalFile()) {
                        //if its local, just load the file path and remove the entry
                        loadLocal(entry);
                    } else {
                        //for remote entries, we need to request the remote file, and also
                        // keep track of the bytes requested, and the remote entries, so we
                        // can avoid overloading the host, and we can clean up after ourselves
                        loadRemote(entry);
                        bytesRequested += entry.getFileSize();
                        remotelyLoaded.add(entry);
                    }
                }
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        } finally {
            //clean up on the way out
            unregisterReceivers();
            if (this.stoppingThread != null) {
                synchronized(this.stoppingThread) {
                    this.stoppingThread.notify();
                }
            }
        }
    }

    /**
     * 
     */
    private void clearOldLoadedFiles() {
        //NOTE: only do this if we need to...to minimize network traffic/playback issues
        if (bytesRequested > maxBytesToLoad * loadFactor) {
            Set<PlaylistEntry> toRemove = new HashSet<PlaylistEntry>();
            long toRemoveBytes = 0;
            for (PlaylistEntry entry : remotelyLoaded) {
                synchronized(entryMutex) {
                    if (entry.isPlayed()) {
                        //indicate the entry isnt loaded, so the playlist wont try and play it
                        entry.setLoaded(false);
                        toRemove.add(entry);
                        toRemoveBytes += entry.getFileSize();
                    }
                }
            }
            
            deleteTempFileData(toRemove);
            remotelyLoaded.removeAll(toRemove);
            bytesRequested -= toRemoveBytes;
        }
    }

    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar
            .addAction(MessagingService.ACTION_TRANSFER_SONG_MESSAGE, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    String fromAddr = intent.getStringExtra(MessagingService.EXTRA_ADDRESS);
                    long   songId   = intent.getLongExtra(  MessagingService.EXTRA_SONG_ID, -1 /*SongMetadata.UNKNOWN_SONG*/);
                    String fileName = intent.getStringExtra(MessagingService.EXTRA_SONG_FILE_NAME);
                    byte[] fileData = intent.getByteArrayExtra(MessagingService.EXTRA_SONG_DATA);
                    saveTempFileData(fromAddr, songId, fileName, fileData);
                    new BroadcastIntent(PlaylistService.ACTION_PLAYLIST_UPDATED).send(playlistService);
                }
            })
            .register(this.playlistService);
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }

    private void deleteTempFileData(Collection<PlaylistEntry> entries) {
        for (PlaylistEntry entry : entries) {
            Log.i(TAG, "Deleting data for entry " + entry);
            File file = new File(entry.getFilePath());
            this.application.deleteFile(file.getName());
            entry.setFilePath(null);
        }
    }

    protected void saveTempFileData(String fromAddr, long songId, String fileName, byte[] fileData) {
        PlaylistEntry entry = findSongByAddressAndId(fromAddr, songId);
        if (entry == null) {
            throw new IllegalStateException("Unable to save data for a song entry that doesnt exist");
        }
        //build a composite name from the macAddress
        String compositeFileName = String.format("%s_%s", SongMetadataUtils.getUniqueKey(fromAddr, songId), fileName);
        try {
            FileOutputStream fos = this.application.openFileOutput(compositeFileName, Context.MODE_PRIVATE);
            fos.write(fileData);
            fos.close();
            String filePath = this.application.getFileStreamPath(compositeFileName).getCanonicalPath();
            entry.setFilePath(filePath);
        } catch (IOException e) {
            this.application.deleteFile(compositeFileName);
            //TODO: set flag to indicate file is broken
        }
    }

    private PlaylistEntry findSongByAddressAndId(String fromAddr, long songId) {
        PlaylistEntry found = null;
        for (PlaylistEntry entry : this.remotelyLoaded) {
            if (entry.getMacAddress().equals(fromAddr) && entry.getId() == songId) {
                found = entry;
            }
        }
        return found;
    }

    public void addToLoadQueue(PlaylistEntry entry) {
        //if the entry isnt already loaded, add it to the load queue
        //NOTE: if it is loaded, we can assume its already in remotelyLoaded
        synchronized(entryMutex) {
            if (!entry.isLoaded()) {
//                if (this.remotelyLoaded.contains(entry)) {
//                    PlaylistEntry existing = findSongByAddressAndId(entry.getMacAddress(), entry.getId());
//                    
//                }
                if (!this.loadQueue.contains(entry) && !this.remotelyLoaded.contains(entry)) {
                    this.loadQueue.add(entry);
                }
            } else {
                if (!remotelyLoaded.contains(entry)) {
                    Log.wtf(TAG, "Entry " + entry + " loaded but not managed.  WTF");
                }
            }
        }
    }
    
    private void loadLocal(PlaylistEntry entry) {
        MediaStoreWrapper msw = new  MediaStoreWrapper(this.playlistService);
        try {
            String filePath = msw.getSongFilePath(entry);
            entry.setFilePath(filePath);
        } catch (SongNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void loadRemote(PlaylistEntry entry) {
        this.application.getMessagingService().sendRequestSongMessage(entry.getMacAddress(), entry.getId());
    }

    public void stopLoading() {
        this.stoppingThread = Thread.currentThread();
        this.running = false;
        synchronized(this.stoppingThread) {
            try {
                this.stoppingThread.wait(1000);
            } catch (InterruptedException e) {
                //fall thru, nothing to do
            }
        }
    }
}
