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

import com.lastcrusade.soundstream.model.PlaylistEntry;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.net.core.AComplexDataType;

/**
 * An abstract message class for messages that send and receive data.  This class contains
 * helper methods to read and write basic data types that may be sent.
 * 
 * @author Jesse Rosalia
 *
 */
public abstract class ADataMessage extends AComplexDataType implements IMessage {

    
    protected void writeBoolean(boolean known, OutputStream output) throws IOException {
        output.write(known ? 1 : 0);
    }

    protected boolean readBoolean(InputStream input) throws IOException {
        return input.read() != 0;
    }

    protected void writeString(String string, OutputStream output) throws IOException {
        if(string == null) {
            writeInteger(0, output);
        }
        else {
            writeBytes(string.getBytes(), output);
        }
    }

    protected String readString(InputStream input) throws IOException {
        byte[] bytes = readBytes(input);
        if (bytes != null) {
            return new String(bytes);
        } else {
            return null;
        }
    }
    
    //TODO: move these into AComplexDataType and use ByteBuffer

    protected void writeLong(long value, OutputStream output) throws IOException {
        output.write((int)(value         & 0xFF));
        output.write((int)((value >> 8)  & 0xFF));
        output.write((int)((value >> 16) & 0xFF));
        output.write((int)((value >> 24) & 0xFF));
        output.write((int)((value >> 32) & 0xFF));
        output.write((int)((value >> 40) & 0xFF));
        output.write((int)((value >> 48) & 0xFF));
        output.write((int)((value >> 56) & 0xFF));
    }

    protected long readLong(InputStream input) throws IOException {
    	long value = toInt(input.read());
    	value 	  |= (toInt(input.read()) << 8);
    	value 	  |= (toInt(input.read()) << 16);
    	value 	  |= (toInt(input.read()) << 24);
    	value 	  |= (toInt(input.read()) << 32);
    	value 	  |= (toInt(input.read()) << 40);
    	value 	  |= (toInt(input.read()) << 48);
    	value 	  |= (toInt(input.read()) << 56);
    	
    	return value;
    }

    /**
     * Convert an unsigned byte read from an input stream (held in an int) into
     * an unsigned number.
     * 
     * This is because Java does not have unsigned types, and so bytes that
     * are > 127 will come in as negative and F everything up.
     * 
     * @param read
     * @return
     */
    private int toInt(int read) {
        return (read & 0xFF);
    }

    protected void writeSongMetadata(SongMetadata metadata, OutputStream output) throws IOException{
        writeLong(  metadata.getId(),         output);
        writeString(metadata.getTitle(),      output);
        writeString(metadata.getArtist(),     output);
        writeString(metadata.getAlbum(),      output);
        writeLong(  metadata.getFileSize(),   output);
        writeString(metadata.getMacAddress(), output);
    }

    protected SongMetadata readSongMetadata(InputStream input) throws IOException{
        long id           = readLong(input);
        String title      = readString(input);
        String artist     = readString(input);
        String album      = readString(input);
        long fileSize     = readLong(input);
        String macAddress = readString(input);
        return new SongMetadata(id, title, artist, album, fileSize, macAddress);
    }
    
    protected void writePlaylistEntry(PlaylistEntry entrydata, OutputStream output) throws IOException{
        writeSongMetadata(entrydata, output);
        writeBoolean(     entrydata.isLoaded(),    output);
        writeBoolean(     entrydata.isPlayed(),    output);
        writeString(      entrydata.getFilePath(), output);
        writeInteger(     entrydata.getEntryId(),    output);
    }
    
    protected PlaylistEntry readPlaylistEntry(InputStream input) throws IOException{
        SongMetadata song = readSongMetadata(input);
        PlaylistEntry entry = new PlaylistEntry(
                song,
                readBoolean(input),
                readBoolean(input),
                readString(input),
                readInteger(input)
        );
        return entry;
    }

    protected void writeBytes(byte[] bytes, OutputStream output) throws IOException {
        if(bytes == null) {
            writeInteger(0, output);
        }
        else {
            writeInteger(bytes.length, output);
            output.write(bytes);
        }
    }

    protected byte[] readBytes(InputStream input) throws IOException {
        int length = readInteger(input);
        //TODO: should put an upper bound here, and use a ByteArrayOutputStream to accumulate bytes
        if(length > 0) {
            byte[] buffer = new byte[length];
            input.read(buffer, 0, length);
            return buffer;
        }
        else {
            return null;
        }
    }
}
