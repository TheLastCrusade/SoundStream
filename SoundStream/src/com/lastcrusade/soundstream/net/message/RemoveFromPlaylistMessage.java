package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RemoveFromPlaylistMessage extends ADataMessage {

    private String macAddress;
    private long id; // TODO: when we deal with duplicates, we may change this
                     // to index in the music library list or something

    RemoveFromPlaylistMessage() {
    }

    public RemoveFromPlaylistMessage(String macAddress, long id) {
        this.macAddress = macAddress;
        this.id = id;
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        this.macAddress = readString(input);
        this.id = readLong(input);
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        writeString(this.macAddress, output);
        writeLong(this.id, output);
    }

    public long getId() {
        return id;
    }

    public String getMacAddress() {
        return macAddress;
    }
}
