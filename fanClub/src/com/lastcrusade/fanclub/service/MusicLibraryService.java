package com.lastcrusade.fanclub.service;

import java.util.ArrayList;
import java.util.List;

import com.lastcrusade.fanclub.library.MediaStoreWrapper;
import com.lastcrusade.fanclub.model.SongMetadata;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MusicLibraryService extends Service {
    List<SongMetadata> metadataList = new ArrayList<SongMetadata>();

    private final IBinder mBinder = new MusicLibraryServiceBinder();

    public class MusicLibraryServiceBinder extends Binder {
        public MusicLibraryService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicLibraryService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    /** Methods for clients */

    public List<SongMetadata> getLibrary(){
        //TODO implement a way to get updates with library Message
        return (new MediaStoreWrapper(this)).list();
    }

    //untested
    private void updateLibrary(List<SongMetadata> additionalSongs){
        for (SongMetadata song : additionalSongs) {
            metadataList.add(song);
        }
    }

}
