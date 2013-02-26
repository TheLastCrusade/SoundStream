package com.lastcrusade.fanclub.service;

import java.util.ArrayList;
import java.util.List;

import com.lastcrusade.fanclub.library.MediaStoreWrapper;
import com.lastcrusade.fanclub.model.SongMetadata;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

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
        //TODO implement a background process to keep the metadataList up to date
        MediaStoreWrapper msw = new MediaStoreWrapper(this);
        return msw.list();
    }

}
