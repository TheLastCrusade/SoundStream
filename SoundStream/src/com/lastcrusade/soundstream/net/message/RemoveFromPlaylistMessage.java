package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RemoveFromPlaylistMessage extends APlaylistEntryMessage {

    RemoveFromPlaylistMessage() {
    }

    public RemoveFromPlaylistMessage(String macAddress, long id) {
        super(macAddress, id);
    }
}
