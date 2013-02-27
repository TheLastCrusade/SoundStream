package com.lastcrusade.fanclub.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This service is responsible for holding the play queue, and feeding songs to the Audio player service.
 * 
 * @author Jesse Rosalia
 *
 */
public class PlayService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
