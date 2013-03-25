package com.lastcrusade.soundstream.net.message;

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
