/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.lastcrusade.soundstream.model.SongMetadata;

public class LibraryMessage extends ADataMessage {
//	private final String TAG = LibraryMessage.class.getName();
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
			library.add(readSongMetadata(input));
		}
	}

	@Override
	public void serialize(OutputStream output) throws IOException {
		writeInteger(library.size(), output);
		for(SongMetadata metadata : library) {
		    writeSongMetadata(metadata, output);
		}
	}

	public ArrayList<SongMetadata> getLibrary() {
		return library;
	}
}
