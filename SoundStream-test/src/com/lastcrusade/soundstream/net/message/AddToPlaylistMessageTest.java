package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class AddToPlaylistMessageTest
extends APlaylistEntrySerializationTest<AddToPlaylistMessage>{

    @Test
    public void testSerializeAddToPlaylistMessage() throws Exception {
        super.testSerializeMessage(
                new AddToPlaylistMessage("Test", 1234));
    }
}
