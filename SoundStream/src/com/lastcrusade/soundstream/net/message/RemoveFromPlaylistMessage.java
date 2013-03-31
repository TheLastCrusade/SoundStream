package com.lastcrusade.soundstream.net.message;


public class RemoveFromPlaylistMessage extends APlaylistEntryMessage {

    RemoveFromPlaylistMessage() {
    }

    public RemoveFromPlaylistMessage(String macAddress, long id, int count) {
        super(macAddress, id, count);
    }
}
