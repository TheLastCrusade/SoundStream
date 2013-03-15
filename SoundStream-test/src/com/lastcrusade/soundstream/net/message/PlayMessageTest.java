package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.lastcrusade.soundstream.net.message.PlayMessage;

public class PlayMessageTest extends SerializationTest<PlayMessage> {
    
    @Test
    public void testSerializePlayMessage() throws IOException {
        PlayMessage oldMessage = new PlayMessage();
        PlayMessage newMessage = super.testSerializeMessage(oldMessage);
        
        assertEquals("Play", newMessage.getPlayMessage());
    }
}
