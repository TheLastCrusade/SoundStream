package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;

public class FoundGuestsMessage extends ADataMessage {

    private ArrayList<FoundGuest> foundGuests = new ArrayList<FoundGuest>();
    
    /**
     * Default constructor, required for Messenger.  All other users should use
     * the other constructor.
     * 
     */
    FoundGuestsMessage() {
    }

    public FoundGuestsMessage(List<FoundGuest> foundGuests) {
        this.foundGuests.addAll(foundGuests);
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        int guestCount = readInteger(input);
        for (int ii = 0; ii < guestCount; ii++) {
            String name = readString(input);
            String addr = readString(input);
            foundGuests.add(new FoundGuest(name, addr));
        }
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        writeInteger(foundGuests.size(), output);
        for (FoundGuest guest : foundGuests) {
            writeString(guest.getName(),    output);
            writeString(guest.getAddress(), output);
        }
    }
    
    public ArrayList<FoundGuest> getFoundGuests() {
        return foundGuests;
    }
}
