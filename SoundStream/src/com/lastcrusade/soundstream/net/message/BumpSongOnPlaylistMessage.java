package com.lastcrusade.soundstream.net.message;

public class BumpSongOnPlaylistMessage extends APlaylistEntryMessage {

    BumpSongOnPlaylistMessage() {
    }
    
    public BumpSongOnPlaylistMessage(String macAddress, long songId, int entryId) {
        super(macAddress, songId, entryId);
    }
}
