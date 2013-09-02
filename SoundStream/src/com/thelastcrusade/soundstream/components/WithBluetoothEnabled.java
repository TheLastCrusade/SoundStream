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
package com.thelastcrusade.soundstream.components;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

import com.thelastcrusade.soundstream.R;
import com.thelastcrusade.soundstream.service.ConnectionService;

/**
 * A simple wrapper class to run code with bluetooth enabled.  This class will ask for permission
 * to enable bluetooth if it is not already enabled, enable bluetooth if needed, and then run the
 * suppled code in the same thread as the caller.  It will only ask for permission if bluetooth
 * needs to be enabled; if it is already enabled, the code is run straight away.
 * 
 * NOTE: Currently, this does not disable bluetooth after the code is run, and does not attempt
 * to enable bluetooth multiple times or cycle bluetooth on and off.  It is therefore safe to
 * wrap around any code that requires bluetooth to be enabled.
 * 
 * If we want to implement functionality to allow us to cycle bluetooth on, run code, and then
 * turn it off, we can do so by creating an internal runnable that runs the supplied code, then
 * disables the bluetooth adapter.
 * 
 * @author Jesse Rosalia
 *
 */
public class WithBluetoothEnabled {

    private static String TAG = WithBluetoothEnabled.class.getSimpleName();
    private Context context;
    private ConnectionService connectionService;
    /**
     * 
     */
    public WithBluetoothEnabled(Context context, ConnectionService connectionService) {
        this.context = context;
        this.connectionService = connectionService;
    }

    /**
     * Note: must be called from a thread with an established Looper (e.g. main UI thread).
     * The supplied runnable will be run in that thread.
     * 
     * @param runnable
     */
    public void run(final Runnable runnable) {
        final Handler handler = new Handler();
        if (!connectionService.isNetworkEnabled()) {
            Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.enable_bluetooth)
                   .setMessage(R.string.ask_enable_bluetooth)
                   .setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
                    
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (connectionService.enableNetwork()) {
                                handler.post(runnable);
                            } else {
                                Log.wtf(TAG, "Error enabling bluetooth");
                            }
                        }
                    })
                    //NOTE: even though this is a NO OP, it is required to show a cancel button
                    // (as seen on a Nexus 4 running Jelly Bean)
                   .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //NO OP
                        }
                    })
                    .show();
        } else {
            handler.post(runnable);
        }
    }
}
