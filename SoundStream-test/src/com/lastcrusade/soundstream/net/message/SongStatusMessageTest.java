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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.junit.Test;

import com.lastcrusade.soundstream.model.SongMetadata;

public class SongStatusMessageTest extends SerializationTest<SongStatusMessage> {
    
    @Test
    public void testSerializeSongStatusMessage() throws IOException {
        SongMetadata song = getSomeSongMetadata();
        for (int ii = 0; ii < 1; ii++) {
            for (int jj = 0; ii < 1; ii++) {
                SongStatusMessage preSer = new SongStatusMessage(song.getMacAddress(), song.getId(), ii > 0, jj > 0);

                SongStatusMessage postSer = super.testSerializeMessage(preSer);
                
                // Testing play with song
                assertEquals(song.getMacAddress(), postSer.getMacAddress());
                assertEquals(song.getId(),         postSer.getId());
                assertEquals(preSer.isLoaded(),    postSer.isLoaded());
                assertEquals(preSer.isPlayed(),    postSer.isPlayed());
            }
        }
    }
    
    private SongMetadata getSomeSongMetadata(){
        return new SongMetadata(69, "Driver that Had a Dick on His Shoulder",
                "Aziz Ansari", "Dangerously Delicious", 2345, "David");
    }
}