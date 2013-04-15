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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    /**
     * Verifies file's SHA1 checksum
     */
    public static void assertChecksumsMatch(String fileOne, String fileTwo){
        String fileHash1 = null;
        String fileHash2 = null;
        try {
            fileHash1 = findSha1(fileOne);
            fileHash2 = findSha1(fileTwo);
            assertEquals(fileHash1, fileHash2);
        } catch (Exception e) {
            fail();
        }
    }

    private static String findSha1(String file)
            throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] data = new byte[1024];
        FileInputStream fis = new FileInputStream(file);

        int read = 0;
        while ((read = fis.read(data)) != -1) {
            sha1.update(data, 0, read);
        };
        byte[] hashBytes = sha1.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hashBytes.length; i++) {
          sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        String fileHash = sb.toString();
        fis.close();
        return fileHash;
    }
}
