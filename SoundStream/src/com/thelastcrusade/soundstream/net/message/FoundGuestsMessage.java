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

package com.thelastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.thelastcrusade.soundstream.model.FoundGuest;

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
            String name   = readString(input);
            String addr   = readString(input);
            boolean known = readBoolean(input);
            foundGuests.add(new FoundGuest(name, addr, known));
        }
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        writeInteger(foundGuests.size(), output);
        for (FoundGuest guest : foundGuests) {
            writeString( guest.getName(),    output);
            writeString( guest.getAddress(), output);
            writeBoolean(guest.isKnown(),    output);
        }
    }

    public ArrayList<FoundGuest> getFoundGuests() {
        return foundGuests;
    }
}
