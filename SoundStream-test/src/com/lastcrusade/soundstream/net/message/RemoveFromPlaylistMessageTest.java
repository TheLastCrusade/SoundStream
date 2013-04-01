package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RemoveFromPlaylistMessageTest
extends APlaylistEntrySerializationTest<RemoveFromPlaylistMessage>{

    @Test
    public void testSerializeRemoveFromPlaylistMessage() throws Exception {
        super.testSerializeMessage(
                new RemoveFromPlaylistMessage("Test", 1234));
    }
}
