package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PlayStatusMessage extends APlaylistEntryMessage {

    private final String TAG = PlayStatusMessage.class.getSimpleName();
    private boolean playing;

    public PlayStatusMessage() {}

    public PlayStatusMessage(String macAddress, long id, int entryId, boolean playing) {
        super(macAddress, id, entryId);
        this.playing = playing;
    }

    public void deserialize(InputStream input) throws IOException {
        super.deserialize(input);
        playing = readBoolean(input);
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        super.serialize(output);
        writeBoolean(playing, output);
    }
    
    public boolean isPlaying(){
        return playing;
    }
    
    public void setPlaying(boolean isPlaying){
        this.playing = isPlaying;
    }
}
