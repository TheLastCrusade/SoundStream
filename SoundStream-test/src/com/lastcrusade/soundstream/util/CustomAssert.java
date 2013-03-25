package com.lastcrusade.soundstream.util;

import com.lastcrusade.soundstream.model.SongMetadata;

import static org.junit.Assert.assertEquals;

public class CustomAssert {

    /**
     * Test to ensure SongMetadata objects are equal.
     * 
     * @param meta
     * @param libraryItem
     */
    public static void assertSongMetaEquals(SongMetadata meta,
            SongMetadata libraryItem) {
        assertEquals(meta.getTitle(), libraryItem.getTitle());
        assertEquals(meta.getArtist(), libraryItem.getArtist());
        assertEquals(meta.getAlbum(), libraryItem.getAlbum());
        assertEquals(meta.getMacAddress(), libraryItem.getMacAddress());
    }
}
