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

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class BumpSongOnPlaylistMessageTest extends SerializationTest<BumpSongOnPlaylistMessage>{

    @Test
    public void testSerializeSongStatusMessage() throws IOException {
        BumpSongOnPlaylistMessage preSer  = new BumpSongOnPlaylistMessage("Test", 1234);
        BumpSongOnPlaylistMessage postSer = super.testSerializeMessage(preSer);
        assertEquals(preSer.getMacAddress(), postSer.getMacAddress());
        assertEquals(preSer.getId(),         postSer.getId());
    }
}
