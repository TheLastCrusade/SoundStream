package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class APlaylistEntryMessage extends ADataMessage {

    private String macAddress;
    private long id; // TODO: when we deal with duplicates, we may change this
                     // to index in the music library list or something
    private int count;
    
    APlaylistEntryMessage() {
    }

    public APlaylistEntryMessage(String macAddress, long id, int count) {
        this.macAddress = macAddress;
        this.id = id;
        this.count = count;
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        this.macAddress = readString(input);
        this.id = readLong(input);
        this.count = readInteger(input);
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        writeString(this.macAddress, output);
        writeLong(this.id, output);
        writeInteger(this.count, output);
    }

    public long getId() {
        return id;
    }

    public String getMacAddress() {
        return macAddress;
    }
    
    public int getCount(){
        return count;
    }
}
