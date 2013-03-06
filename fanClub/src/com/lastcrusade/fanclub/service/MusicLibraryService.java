package com.lastcrusade.fanclub.service;

import java.util.ArrayList;
import java.util.List;

import com.lastcrusade.fanclub.library.MediaStoreWrapper;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.util.BluetoothUtils;
import com.lastcrusade.fanclub.util.BroadcastIntent;
import com.lastcrusade.fanclub.util.BroadcastRegistrar;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MusicLibraryService extends Service {
    /**
     * Broadcast action sent when the MusicLibrary gets or loses music
     *
     */
    public static final String ACTION_LIBRARY_UPDATED = MusicLibraryService.class
            .getName() + ".action.LibraryUpdated";

    private List<SongMetadata> metadataList = new ArrayList<SongMetadata>();

    public class MusicLibraryServiceBinder extends Binder implements
        ILocalBinder<MusicLibraryService> {
        public MusicLibraryService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicLibraryService.this;
        }
    }

    @Override
    public void onCreate() {
        metadataList = (new MediaStoreWrapper(this)).list();
        String bluetoothName = BluetoothUtils.getLocalBluetoothName();
        for (SongMetadata song : metadataList) {
            // Should we store the username/bluetooth name somewhere
            song.setMacAddress(bluetoothName);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicLibraryServiceBinder();
    }
    
    /** Methods for clients */

    public List<SongMetadata> getLibrary(){
        //TODO implement a way to get updates with library Message
        return metadataList;
    }

    //untested
    private void updateLibrary(List<SongMetadata> additionalSongs){
        for (SongMetadata song : additionalSongs) {
            metadataList.add(song);
        }
        new BroadcastIntent(ACTION_LIBRARY_UPDATED).send(this);
    }

}
