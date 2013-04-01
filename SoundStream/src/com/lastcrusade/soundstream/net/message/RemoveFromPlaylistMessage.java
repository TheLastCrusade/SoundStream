package com.lastcrusade.soundstream.net.message;


public class RemoveFromPlaylistMessage extends APlaylistEntryMessage {

    RemoveFromPlaylistMessage() {
    }

    public RemoveFromPlaylistMessage(String macAddress, long id, int entryId) {
        super(macAddress, id, entryId);
    }
}
