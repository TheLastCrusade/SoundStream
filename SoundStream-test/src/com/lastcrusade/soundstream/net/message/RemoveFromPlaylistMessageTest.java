package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class RemoveFromPlaylistMessageTest extends SerializationTest<RemoveFromPlaylistMessage>{

    @Test
    public void testSerializeSongStatusMessage() throws Exception {
        RemoveFromPlaylistMessage preSer  = new RemoveFromPlaylistMessage("Test", 1234);
        RemoveFromPlaylistMessage postSer = super.testSerializeMessage(preSer);
        assertEquals(preSer.getMacAddress(), postSer.getMacAddress());
        assertEquals(preSer.getId(),         postSer.getId());
    }
}
