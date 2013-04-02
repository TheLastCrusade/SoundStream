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

package com.lastcrusade.soundstream.util;

import static org.junit.Assert.*;

import com.lastcrusade.soundstream.model.PlaylistEntry;
import com.lastcrusade.soundstream.model.SongMetadata;

public class CustomAssert {

    /**
     * Test to ensure SongMetadata objects are equal.
     * 
     * @param expected
     * @param actual
     */
    public static void assertSongMetaEquals(SongMetadata expected,
            SongMetadata actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getArtist(), actual.getArtist());
        assertEquals(expected.getAlbum(), actual.getAlbum());
        assertEquals(expected.getFileSize(), actual.getFileSize());
        assertEquals(expected.getMacAddress(), actual.getMacAddress());
    }
    
    public static void assertPlaylistEntry(PlaylistEntry expected,
            PlaylistEntry actual) {
        assertSongMetaEquals(expected, actual);
        assertEquals(expected.isLoaded(), actual.isLoaded());
        assertEquals(expected.isPlayed(), actual.isPlayed());
        assertEquals(expected.getFilePath(), actual.getFilePath());
    }
}
