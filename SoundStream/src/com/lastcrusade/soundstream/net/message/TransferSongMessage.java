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

public class TransferSongMessage extends ADataMessage implements IFileMessage {

    private long   songId;
    private String songFileName;
    private String filePath;

    /**
     * Default constructor, required for Messenger.  All other users should use
     * the other constructor.
     * 
     */
    TransferSongMessage() {
    }

    public TransferSongMessage(long songId, String songFileName, String filePath) {
        this.songId       = songId;
        this.songFileName = songFileName;
        this.filePath     = filePath;
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        this.songId       = super.readLong(input);
        this.songFileName = super.readString(input);
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        super.writeLong(  this.songId, output);
        super.writeString(this.songFileName, output);
    }
    
    @Override
    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getSongId() {
        return songId;
    }
    
    public String getSongFileName() {
        return songFileName;
    }
    
}
