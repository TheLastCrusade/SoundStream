package com.lastcrusade.soundstream.service;

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

import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.library.MediaStoreWrapper;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.util.BluetoothUtils;
import com.lastcrusade.soundstream.util.BroadcastIntent;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;

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
     * This is also the solution to a common interview question :-)
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
        updateLibrary(metadataList, false);

        registerReceivers();
    }

    @Override
    public void onDestroy() {
        unregisterReceivers();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicLibraryServiceBinder();
    }
    
    /**
     * 
     */
    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar
            .addAction(MessagingService.ACTION_LIBRARY_MESSAGE, new IBroadcastActionHandler() {
                
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    List<SongMetadata> remoteMetas = intent.getParcelableArrayListExtra(MessagingService.EXTRA_SONG_METADATA);
                    updateLibrary(remoteMetas, true);
                }
            })
            .addAction(ConnectionService.ACTION_FAN_DISCONNECTED, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    String macAddress = intent.getStringExtra(ConnectionService.EXTRA_FAN_ADDRESS);
                    removeLibraryForAddress(macAddress, true);
                }
            })
            .register(this);
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }
    /** Methods for clients */

    public List<SongMetadata> getLibrary() {
        synchronized(metadataMutex) {
            
            //sorts the list before returning it - for now,
            //order is simply alphabetical
            ArrayList<SongMetadata> music = new ArrayList<SongMetadata>(metadataList);
            Collections.sort(music);
            //unmodifiable copy, for safety
            return Collections.unmodifiableList(music);
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

    /**
     * Update the library with the additional songs passed in.
     * 
     * NOTE: This should not be called by an outside user.  It is package protected to allow us to unit test
     * it, but generally speaking, the library gets updated from network messages and the onCreate method.
     * 
     * @param additionalSongs
     * @param notify
     */
    void updateLibrary(Collection<SongMetadata> additionalSongs, boolean notify) {
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
        if (notify) {
            notifyLibraryUpdated();
        }
    }

    /**
     * Notify that the library was updated.  This includes
     * sending an intent to the system, and sending the library out
     * to the fans.
     */
    private void notifyLibraryUpdated() {
        new BroadcastIntent(ACTION_LIBRARY_UPDATED).send(this);
        //send the updated library to all the fans out there
        if (((CustomApp)getApplication()).getMessagingService() != null) {
            ((CustomApp)getApplication()).getMessagingService().sendLibraryMessageToFans(getLibrary());
        }
    }

    /**
     * Remove all songs that belong to the specified mac address.
     * 
     * NOTE: This should not be called by an outside user.  It is package protected to allow us to unit test
     * it, but generally speaking, the library gets updated from network messages and the onCreate method.
     * 
     * @param additionalSongs
     * @param notify
     */
    void removeLibraryForAddress(String macAddress, boolean notify) {
        synchronized(metadataMutex) {
            //remove the songs for the specified address by assembling a new list
            // and map with all songs except those for that address
            List<SongMetadata>   newList = new ArrayList<SongMetadata>();
            Map<String, Integer> newMap  = new HashMap<String, Integer>();
            for (SongMetadata song : metadataList) {
                if (!song.getMacAddress().equals(macAddress)) {
                    int nextInx = newList.size();
                    newList.add(song);
                    String key = createSongKey(song);
                    newMap.put(key, nextInx);
                }
            }
            //replace THE list and map with the new structures
            metadataList = newList;
            metadataMap = newMap;
        }
        if (notify) {
            notifyLibraryUpdated();
        }
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
