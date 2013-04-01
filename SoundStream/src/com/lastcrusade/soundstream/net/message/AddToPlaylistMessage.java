package com.lastcrusade.soundstream.net.message;


public class AddToPlaylistMessage extends APlaylistEntryMessage {

    AddToPlaylistMessage() {
    }

    public AddToPlaylistMessage(String macAddress, long id, int entryId) {
        super(macAddress, id, entryId);
    }
}
