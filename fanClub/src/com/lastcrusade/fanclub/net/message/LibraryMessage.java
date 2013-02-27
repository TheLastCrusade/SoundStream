package com.lastcrusade.fanclub.net.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.lastcrusade.fanclub.PlaylistFragment;
//import com.lastcrusade.fanclub.service.MusicLibraryService;
import com.lastcrusade.fanclub.model.SongMetadata;

public class LibraryMessage implements IMessage {
	private final String TAG = LibraryMessage.class.getName();
	private List<SongMetadata> library;

	@Override
	public void deserialize(InputStream input) throws IOException {
		byte[] bytes = new byte[1024];
		int read = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while((read = input.read(bytes)) > 0) {
			out.write(bytes, 0, read);
		}
		//this.setLibrary(out); // wait for Jesse's changes to be made 
	}

	@Override
	public void serialize(OutputStream output) throws IOException {
		output.write(this.getLibrary().toString().getBytes());
	}

	public List<SongMetadata> getLibrary() {
		return library;
	}

	public void setLibrary(List<SongMetadata> library) {
		this.library = library;
	}
}
