package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.lastcrusade.soundstream.model.Playlist;
import com.lastcrusade.soundstream.model.PlaylistEntry;
import com.lastcrusade.soundstream.model.SongMetadata;

public class PlaylistMessage extends ADataMessage {
    private final String TAG = PlaylistMessage.class.getName();
	
    private ArrayList<PlaylistEntry> songsToPlay = new ArrayList<PlaylistEntry>();

    /**
     * Default constructor, required for Messenger.  All other users should use
     * the other constructor.
     * 
     */
    public PlaylistMessage() {}

    public PlaylistMessage(List<? extends PlaylistEntry> songsToPlay) {
        this.songsToPlay = new ArrayList<PlaylistEntry>(songsToPlay);
    }
	
	@Override
	public void deserialize(InputStream input) throws IOException {
	    int playlistSize = readInteger(input);
	    for(int i = 0; i < playlistSize; i++) {
	        PlaylistEntry entry = readPlaylistEntry(input);
	        songsToPlay.add(entry);
        }
	}
	
	@Override
    public void serialize(OutputStream output) throws IOException {
	    writeInteger(songsToPlay.size(), output);
		
	    for(PlaylistEntry entry: songsToPlay) {
	        writePlaylistEntry(entry, output);
	    }
	}

    //This is because you can pass an ArrayList of parseables but not a List
	public ArrayList<PlaylistEntry> getSongsToPlay() {
        return songsToPlay;
    }
}
