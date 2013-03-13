package com.lastcrusade.fanclub.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.lastcrusade.fanclub.library.MediaStoreWrapper;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.util.BluetoothUtils;
import com.lastcrusade.fanclub.util.BroadcastIntent;
import com.lastcrusade.fanclub.util.BroadcastRegistrar;
import com.lastcrusade.fanclub.util.IBroadcastActionHandler;

public class MusicLibraryService extends Service {
    /**
     * Broadcast action sent when the MusicLibrary gets or loses music
     *
     */
    public static final String ACTION_LIBRARY_UPDATED = MusicLibraryService.class
            .getName() + ".action.LibraryUpdated";

    /**
     * A map of keys to array positions, and an array of song metadata.
     * 
     * This lets us maintain the order in which things are added, but also allows
     * us to quickly account for duplicates/replace with updated data.
     * 
     * This is also the solution to a common interview quesiton :-)
     * 
     */
    private Map<String, Integer> metadataMap  = new HashMap<String, Integer>();
    private List<SongMetadata>   metadataList = new ArrayList<SongMetadata>();
    
    private final Object metadataMutex = new Object();
    
    private BroadcastRegistrar registrar;

    private String myMacAddress;

    public class MusicLibraryServiceBinder extends Binder implements
        ILocalBinder<MusicLibraryService> {
        public MusicLibraryService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicLibraryService.this;
        }
    }

    @Override
    public void onCreate() {
        //load the local songs and set the mac address, so the metadata objects
        // can live in the library
        List<SongMetadata> metadataList = (new MediaStoreWrapper(this)).list();
        this.myMacAddress = BluetoothUtils.getLocalBluetoothMAC();
        for (SongMetadata song : metadataList) {
            song.setMacAddress(this.myMacAddress);
        }
        
        //update the library with the local songs
        updateLibrary(metadataList);

        this.registrar = new BroadcastRegistrar();
        this.registrar.addAction(MessagingService.ACTION_LIBRARY_MESSAGE, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                try {
                    List<SongMetadata> remoteMetas = intent.getParcelableArrayListExtra(MessagingService.EXTRA_SONG_METADATA);
                    updateLibrary(remoteMetas);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }).register(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicLibraryServiceBinder();
    }
    
    /** Methods for clients */

    public List<SongMetadata> getLibrary() {
        synchronized(metadataMutex) {
            //unmodifiable copy, for safety
            return Collections.unmodifiableList(new ArrayList<SongMetadata>(metadataList));
        }
    }

    public List<SongMetadata> getMyLibrary() {
        synchronized(metadataMutex) {
            List<SongMetadata> myLibrary = new ArrayList<SongMetadata>();
            //look thru the library, and pull out songs with "my" mac address
            for (SongMetadata meta : metadataList) {
                if (meta.getMacAddress().equals(this.myMacAddress)) {
                    myLibrary.add(meta);
                }
            }
            //unmodifiable copy, for safety
            return Collections.unmodifiableList(myLibrary);
        }
    }

    public void updateLibrary(Collection<SongMetadata> additionalSongs) {
        synchronized(metadataMutex) {
            for (SongMetadata song : additionalSongs) {
                String key = createSongKey(song);
                if (metadataMap.containsKey(key)) {
                    //song already exists, replace the existing entry in the list with the new data.
                    metadataList.set(metadataMap.get(key), song);
                } else {
                    //new song to add to the list...add it, and store the position in the map
                    int nextInx = metadataList.size();
                    metadataList.add(song);
                    metadataMap.put(key, nextInx);
                }
            }
        }
        new BroadcastIntent(ACTION_LIBRARY_UPDATED).send(this);
    }

    /**
     * Create a unique key for this song.  This unique key consists of:
     *  Mac address (uniquely identifies a device)
     *  Song id (uniquely identifies a song on the device)
     * 
     * @param song
     * @return
     */
    private String createSongKey(SongMetadata song) {
        return song.getMacAddress() + "_" + song.getId();
    }
}
