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

import com.lastcrusade.soundstream.model.PlaylistEntry;

public class PlaylistMessage extends ADataMessage {
//    private final String TAG = PlaylistMessage.class.getName();
	
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
