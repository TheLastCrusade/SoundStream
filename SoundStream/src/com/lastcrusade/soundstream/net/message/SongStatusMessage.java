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

public class SongStatusMessage extends APlaylistEntryMessage {

    private boolean loaded;
    private boolean played;

    SongStatusMessage() {
    }

    public SongStatusMessage(String macAddress, long songId, boolean loaded,
            boolean played) {
        super(macAddress, songId);
        this.loaded = loaded;
        this.played = played;
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        super.deserialize(input);
        this.loaded = readBoolean(input);
        this.played = readBoolean(input);
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        super.serialize(output);
        writeBoolean(this.loaded, output);
        writeBoolean(this.played, output);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isPlayed() {
        return played;
    }
}
