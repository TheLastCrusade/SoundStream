/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thelastcrusade.soundstream.net;

import java.io.IOException;

import com.thelastcrusade.soundstream.net.bt.BluetoothConnection;

import android.util.Log;

/**
 * @author theJenix
 *
 */
public class ConnectionWriteThread extends Thread {
    private final String TAG = ConnectionWriteThread.class.getSimpleName();

    protected boolean writeThreadRunning;
    private ConnectionWriter writer;
    private Thread stoppingThread;

    /**
     * @param name
     */
    public ConnectionWriteThread(String name, ConnectionWriter writer) {
        super(name + " Writer");
        this.writer = writer;
    }
    
    @Override
    public void run() {
        try {
            while (writeThreadRunning) {
                try {
                    writer.writeOne();
                    if (!writer.canWrite()) {
                        Thread.sleep(10); //give the system a chance to breath
                    }
                } catch (IOException e) {
                    //we've probably closed our socket...quit the thread
                    writeThreadRunning = false;
                    //log an error
                    Log.wtf(TAG, e);
                } catch (InterruptedException e) {
                    //nothing to do
                }
            }
        } finally {
            if (stoppingThread != null) {
                synchronized(stoppingThread) {
                    stoppingThread.notify();
                }
            }
        }
    }
    
    @Override
    public synchronized void start() {
        writeThreadRunning = true;
        super.start();
    }
    
    public void stopAndWait() {
        writeThreadRunning = false;
        stoppingThread = Thread.currentThread();
        synchronized(stoppingThread) {
            try {
                //wait for the thread to stop, or for 1 second.  This number may have to be adjusted
                // as we test with larger and larger files.
                int waitTimeInMS = 1000;
                stoppingThread.wait(waitTimeInMS);
            } catch (InterruptedException e) {
                //fall thru, nothing to do
            }
            if (this.isAlive()) {
                Log.w(TAG, "Write thread is still alive...");
            }
        }
    }
}
