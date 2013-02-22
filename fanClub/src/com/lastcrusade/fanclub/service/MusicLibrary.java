package com.lastcrusade.fanclub.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MusicLibrary extends Service {

    private final IBinder mBinder = new MusicLibraryBinder();

    public class MusicLibraryBinder extends Binder {
        public MusicLibrary getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicLibrary.this;
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
