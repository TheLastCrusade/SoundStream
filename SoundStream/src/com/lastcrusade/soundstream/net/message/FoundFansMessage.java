package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;

public class FoundFansMessage extends ADataMessage {

    private ArrayList<FoundFan> foundFans = new ArrayList<FoundFan>();
    
    /**
     * Default constructor, required for Messenger.  All other users should use
     * the other constructor.
     * 
     */
    FoundFansMessage() {
    }

    public FoundFansMessage(List<FoundFan> foundFans) {
        this.foundFans.addAll(foundFans);
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        int fanCount = readInteger(input);
        for (int ii = 0; ii < fanCount; ii++) {
            String name = readString(input);
            String addr = readString(input);
            foundFans.add(new FoundFan(name, addr));
        }
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        writeInteger(foundFans.size(), output);
        for (FoundFan fan : foundFans) {
            writeString(fan.getName(),    output);
            writeString(fan.getAddress(), output);
        }
    }
    
    public ArrayList<FoundFan> getFoundFans() {
        return foundFans;
    }
}
