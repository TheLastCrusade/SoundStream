package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class BumpSongOnPlaylistMessageTest
extends APlaylistEntrySerializationTest<BumpSongOnPlaylistMessage>{

    @Test
    public void testSerializeBumpSongOnPlaylistMessage() throws Exception {
        super.testSerializeMessage(
                new BumpSongOnPlaylistMessage("Test", 1234));
    }
}
