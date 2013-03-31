package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SongStatusMessage extends APlaylistEntryMessage {

    private boolean loaded;
    private boolean played;

    SongStatusMessage() {
    }

    public SongStatusMessage(String macAddress, long songId, int count, boolean loaded,
            boolean played) {
        super(macAddress, songId, count);
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
