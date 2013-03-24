package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class RequestSongMessageTest extends SerializationTest<RequestSongMessage> {
    
    @Test
    public void testSerializePlayMessage() throws IOException {
        long songId = 8675309L;
        RequestSongMessage oldMessage = new RequestSongMessage(songId);
        RequestSongMessage newMessage = super.testSerializeMessage(oldMessage);
        
        assertEquals(songId, newMessage.getSongId());
    }
}
