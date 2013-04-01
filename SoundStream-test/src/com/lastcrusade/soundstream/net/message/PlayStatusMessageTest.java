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

import static org.junit.Assert.fail;

import org.junit.Test;

import com.lastcrusade.soundstream.model.SongMetadata;

public class PlayStatusMessageTest extends SerializationTest<PlayStatusMessage> {
	
    @Test
    public void testSerializePlayStatusMessage() throws Exception {
        PlayStatusMessage preSerializationPlayStatusMsg = new PlayStatusMessage();

//        preSerializationPlayStatusMsg.setString(PlayStatusMessage.PLAY_MESSAGE);
//        preSerializationPlayStatusMsg.setCurrentSong(getSomeSongMetadata());
//        PlayStatusMessage postSerializationPlayStatusMsg = super.testSerializeMessage(preSerializationPlayStatusMsg);
//		
//        // Testing play with song
//        assertEquals(PlayStatusMessage.PLAY_MESSAGE, postSerializationPlayStatusMsg.getString());
//        assertSongMetaEquals(getSomeSongMetadata(), postSerializationPlayStatusMsg.getCurrentSong());
//
//        // Testing pause with song
//        preSerializationPlayStatusMsg.setString(PlayStatusMessage.PAUSE_MESSAGE);
//	    preSerializationPlayStatusMsg.setCurrentSong(getSomeSongMetadata());
//	    postSerializationPlayStatusMsg = super.testSerializeMessage(preSerializationPlayStatusMsg);
//	    assertEquals(PlayStatusMessage.PAUSE_MESSAGE, postSerializationPlayStatusMsg.getString());
//	    assertSongMetaEquals(getSomeSongMetadata(), postSerializationPlayStatusMsg.getCurrentSong());
        fail("Need to reimplement");
	}
	
	private SongMetadata getSomeSongMetadata(){
	    return new SongMetadata(69, "Driver that Had a Dick on His Shoulder",
	            "Aziz Ansari", "Dangerously Delicious", 2345, "David");
	}
}
