package com.lastcrusade.fanclub.net.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lastcrusade.fanclub.PlaylistFragment;
//import com.lastcrusade.fanclub.service.MusicLibraryService;

public class LibraryMessage implements IMessage {
	private final String TAG = "LibraryMessage";
	private String libraryString;

	@Override
	public void deserialize(InputStream input) throws IOException {
		byte[] bytes = new byte[1024];
		int read = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while((read = input.read(bytes)) > 0) {
			out.write(bytes, 0, read);
		}
		this.setLibraryString(out.toString());
	}

	@Override
	public void serialize(OutputStream output) throws IOException {
		output.write(this.getLibraryString().getBytes());
	}

	public String getLibraryString() {
		return libraryString;
	}

	public void setLibraryString(String string) {
		this.libraryString = string;
	}
}
