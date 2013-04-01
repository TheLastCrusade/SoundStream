package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class AddToPlaylistMessageTest extends SerializationTest<AddToPlaylistMessage>{

    @Test
    public void testSerializeSongStatusMessage() throws Exception {
        AddToPlaylistMessage preSer  = new AddToPlaylistMessage("Test", 1234);
        AddToPlaylistMessage postSer = super.testSerializeMessage(preSer);
        assertEquals(preSer.getMacAddress(), postSer.getMacAddress());
        assertEquals(preSer.getId(),         postSer.getId());
    }
}
