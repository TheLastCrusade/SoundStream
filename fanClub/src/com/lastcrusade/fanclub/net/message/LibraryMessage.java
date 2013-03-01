package com.lastcrusade.fanclub.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.lastcrusade.fanclub.model.SongMetadata;

public class LibraryMessage extends ADataMessage {
	private final String TAG = LibraryMessage.class.getName();
	private List<SongMetadata> library = new ArrayList<SongMetadata>();
	
	/**
	 * Default constructor required for Messenger, use the other one though
	 */
	public LibraryMessage() {}
	
	public LibraryMessage(List<SongMetadata> metadata) {
		this.library.addAll(metadata);
	}

	@Override
	public void deserialize(InputStream input) throws IOException {
		int librarySize = readInteger(input);
		for(int i = 0; i < librarySize; i++) {
			long id = Long.parseLong(readString(input));
			String title = readString(input);
			String artist = readString(input);
			String album = readString(input);
			String username = readString(input);
			
			library.add(new SongMetadata(id, title, artist, album, username));
		}
	}

	@Override
	public void serialize(OutputStream output) throws IOException {
		writeInteger(library.size(), output);
		for(SongMetadata metadata : library) {
			writeString(String.valueOf(metadata.getId()), output);
			writeString(metadata.getTitle(), output);
			writeString(metadata.getArtist(), output);
			writeString(metadata.getAlbum(), output);
			writeString(metadata.getUsername(), output);
		}
	}

	public List<SongMetadata> getLibrary() {
		return library;
	}

	public void setLibrary(List<SongMetadata> library) {
		this.library = library;
	}
}
