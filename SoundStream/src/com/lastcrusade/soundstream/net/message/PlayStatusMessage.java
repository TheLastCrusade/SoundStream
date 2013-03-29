package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PlayStatusMessage extends APlaylistEntryMessage {

    private final String TAG = PlayStatusMessage.class.getSimpleName();
    private boolean isPlaying;

    public PlayStatusMessage() {}

    public PlayStatusMessage(String macAddress, long id, boolean isPlaying) {
        super(macAddress, id);
        this.isPlaying = isPlaying;
    }

    public void deserialize(InputStream input) throws IOException {
        super.deserialize(input);
        isPlaying = readBoolean(input);
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        super.serialize(output);
        writeBoolean(isPlaying, output);
    }
    
    public boolean getIsPlaying(){
        return isPlaying;
    }
    
    public void setIsPlaying(boolean isPlaying){
        this.isPlaying = isPlaying;
    }
}
