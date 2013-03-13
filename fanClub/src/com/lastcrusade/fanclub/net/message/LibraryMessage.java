package com.lastcrusade.fanclub.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.lastcrusade.fanclub.model.SongMetadata;

public class LibraryMessage extends ADataMessage {
	private final String TAG = LibraryMessage.class.getName();
	//JR, 03/13/13, I know this should be List, but Android intents allow you to
	// bundle an ArrayList of parcelables and that functionality doesnt extend
	// to the interface.
	private ArrayList<SongMetadata> library = new ArrayList<SongMetadata>();
	
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
			long id = readLong(input);
			String title      = readString(input);
			String artist     = readString(input);
			String album      = readString(input);
			String macAddress = readString(input);
			
			library.add(new SongMetadata(id, title, artist, album, macAddress));
		}
	}

	@Override
	public void serialize(OutputStream output) throws IOException {
		writeInteger(library.size(), output);
		for(SongMetadata metadata : library) {
			writeLong(metadata.getId(), output);
			writeString(metadata.getTitle(), output);
			writeString(metadata.getArtist(), output);
			writeString(metadata.getAlbum(), output);
			writeString(metadata.getMacAddress(), output);
		}
	}

	public ArrayList<SongMetadata> getLibrary() {
		return library;
	}
}
