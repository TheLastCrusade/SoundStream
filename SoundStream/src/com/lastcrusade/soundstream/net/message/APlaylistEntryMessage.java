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

package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class APlaylistEntryMessage extends ADataMessage {

    private String macAddress;
    private long id; // TODO: when we deal with duplicates, we may change this
                     // to index in the music library list or something
    private int entryId;
    
    APlaylistEntryMessage() {
    }

    public APlaylistEntryMessage(String macAddress, long id, int entryId) {
        this.macAddress = macAddress;
        this.id = id;
        this.entryId = entryId;
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        this.macAddress = readString(input);
        this.id = readLong(input);
        this.entryId = readInteger(input);
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        writeString(this.macAddress, output);
        writeLong(this.id, output);
        writeInteger(this.entryId, output);
    }

    public long getId() {
        return id;
    }

    public String getMacAddress() {
        return macAddress;
    }
    
    public int getEntryId(){
        return entryId;
    }
}
