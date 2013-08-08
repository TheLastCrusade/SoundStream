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

public class RequestSongMessage extends ADataMessage {

    private long songId;
    
    /**
     * Default constructor, required for Messenger.  All other users should use
     * the other constructor.
     * 
     */
    public RequestSongMessage() {
    }

    public RequestSongMessage(long songId) {
        this.songId = songId;
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        this.songId = readLong(input);
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        writeLong(this.songId, output);
    }

    public long getSongId() {
        return songId;
    }
}
