package com.lastcrusade.fanclub.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MusicLibraryService extends Service {

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

    public int getRandomNumber() {
      return 101; //HAHA its not random
    }

}
