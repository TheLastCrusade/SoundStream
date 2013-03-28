package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AddToPlaylistMessage extends APlaylistEntryMessage {

    AddToPlaylistMessage() {
    }

    public AddToPlaylistMessage(String macAddress, long id) {
        super(macAddress, id);
    }
}
