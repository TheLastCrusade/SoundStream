package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class BumpSongOnPlaylistMessageTest extends SerializationTest<BumpSongOnPlaylistMessage>{

    @Test
    public void testSerializeSongStatusMessage() throws Exception {
        BumpSongOnPlaylistMessage preSer  = new BumpSongOnPlaylistMessage("Test", 1234);
        BumpSongOnPlaylistMessage postSer = super.testSerializeMessage(preSer);
        assertEquals(preSer.getMacAddress(), postSer.getMacAddress());
        assertEquals(preSer.getId(),         postSer.getId());
    }
}
