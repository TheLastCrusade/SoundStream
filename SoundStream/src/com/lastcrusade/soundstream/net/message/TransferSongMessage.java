package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TransferSongMessage extends ADataMessage {

    private long   songId;
    private String songFileName;
    private byte[] songData;

    /**
     * Default constructor, required for Messenger.  All other users should use
     * the other constructor.
     * 
     */
    TransferSongMessage() {
    }

    public TransferSongMessage(long songId, String songFileName, byte[] songData) {
        this.songId       = songId;
        this.songFileName = songFileName;
        this.songData     = songData;
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        this.songId       = super.readLong(input);
        this.songFileName = super.readString(input);
        this.songData     = super.readBytes(input);
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        super.writeLong(this.songId, output);
        super.writeString(this.songFileName, output);
        super.writeBytes(this.songData, output);
    }
    
    public long getSongId() {
        return songId;
    }
    
    public String getSongFileName() {
        return songFileName;
    }
    
    public byte[] getSongData() {
        return songData;
    }
}
