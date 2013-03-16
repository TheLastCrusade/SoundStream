package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.lastcrusade.soundstream.model.Playlist;
import com.lastcrusade.soundstream.model.SongMetadata;

public class PlaylistMessage extends ADataMessage {
	private final String TAG = PlaylistMessage.class.getName();
	
	private Playlist playlist = new Playlist();

    /**
     * Default constructor, required for Messenger.  All other users should use
     * the other constructor.
     * 
     */
	public PlaylistMessage() {}
	
	public PlaylistMessage(Playlist playlist) {
	    this.playlist = playlist;
	}
	
	@Override
	public void deserialize(InputStream input) throws IOException {
		int playlistSize = readInteger(input);
		for(int i = 0; i < playlistSize; i++) {
		    SongMetadata song = readSongMetadata(input);
			playlist.add(song);
		}
	}
	
	@Override
	public void serialize(OutputStream output) throws IOException {
		List<SongMetadata> songs = playlist.getSongsToPlay();

		writeInteger(playlist.size(), output);
		for(int i = 0; i < songs.size(); i++) {
		    writeSongMetadata(songs.get(i), output);
		}
	}

	public Playlist getPlaylist(){
	    return playlist;
	}
}
