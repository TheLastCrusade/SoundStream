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

/**
 * This message is sent from a guest to the host to request the host
 * connect to new guests, identified by the addresses attribute.
 * 
 * @author Jesse Rosalia
 *
 */
public class ConnectGuestsMessage extends ADataMessage {

    private ArrayList<String> addresses = new ArrayList<String>();
    
    /**
     * Default constructor, required for Messenger.  All other users should use
     * the other constructor.
     * 
     */
    ConnectGuestsMessage() {
    }

    public ConnectGuestsMessage(List<String> addresses) {
        this.addresses.addAll(addresses);
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        int numAddresses = readInteger(input);
        for (int ii = 0; ii < numAddresses; ii++) {
            addresses.add(readString(input));
        }
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        writeInteger(addresses.size(), output);
        for (String address : addresses) {
            writeString(address, output);
        }
    }
    
    public ArrayList<String> getAddresses() {
        return addresses;
    }
}
