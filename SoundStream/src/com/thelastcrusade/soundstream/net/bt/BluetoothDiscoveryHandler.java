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

package com.thelastcrusade.soundstream.net.bt;

import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.thelastcrusade.soundstream.model.FoundGuest;
import com.thelastcrusade.soundstream.service.ConnectionService;
import com.thelastcrusade.soundstream.util.LocalBroadcastIntent;

/**
 * A generic handler for discovering devices.  This handler will accumulate discovered devices and
 * pop up a dialog to allow the user to pick the device or devices to connect to.
 * 
 * @author Jesse Rosalia
 *
 */
public class BluetoothDiscoveryHandler {

    private static final String TAG = "BluetoothDiscoveryHandler";
    
    private final Context context;
    private final BluetoothAdapter adapter;

    private ArrayList<FoundGuest> discoveredGuests;

    private String remoteInitiatorAddress;

    private boolean discoveryStarted;

    public BluetoothDiscoveryHandler(Context context, BluetoothAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    /**
     * @return
     */
    public boolean isDiscoveryStarted() {
        return this.discoveryStarted;
    }

    /**
     * Call to indicate the start of discovery.  This MUST be called before devices are discovered.
     * 
     */
    public void onDiscoveryStarted(String remoteInitiator) {
        Log.w(TAG, "Discovery started");
        this.discoveryStarted = true;
        this.remoteInitiatorAddress = remoteInitiator;
        this.discoveredGuests = new ArrayList<FoundGuest>();
    }

    /**
     * Call to indicate the end of discovery.  This MUST be called to pop up the dialog box.
     * 
     */
    public void onDiscoveryFinished() {
        Log.w(TAG, "Discovery finished");
        //if its remote initiated, we want to send a different action
        String action = this.remoteInitiatorAddress != null
                          ? ConnectionService.ACTION_REMOTE_FIND_FINISHED
                          : ConnectionService.ACTION_FIND_FINISHED;
        new LocalBroadcastIntent(action)
            .putParcelableArrayListExtra(ConnectionService.EXTRA_GUESTS, this.discoveredGuests)
            .send(this.context);
        
        //reinit the handler, for the next time
        this.discoveredGuests = new ArrayList<FoundGuest>();
        this.discoveryStarted = false;
    }

    /**
     * Call to hold a discovered device for selection by the user.
     * 
     * @param device
     */
    public void onDiscoveryFound(BluetoothDevice device) {
        Log.w(TAG,
                "Device found: " + device.getName() + "(" + device.getAddress()
                        + ")");
        boolean known = false;
        //only connect to devices that can support our service
        for (BluetoothDevice bonded : adapter.getBondedDevices()) {
            if (bonded.getAddress().equals(device.getAddress())) {
                Log.w(TAG, "Already paired!  Using paired device");
                known = true;
                device = adapter.getRemoteDevice(bonded.getAddress());
            }
        }
        //only add the remote if they were not the initiator
        //NOTE: handles null check inside equals
        if (!device.getAddress().equals(this.remoteInitiatorAddress)) {
            this.discoveredGuests.add(new FoundGuest(device.getName(), device.getAddress(), known));
        }
    }
}
