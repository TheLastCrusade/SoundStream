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
