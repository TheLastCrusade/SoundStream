package com.lastcrusade.soundstream.net.message;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.lastcrusade.soundstream.net.message.PauseMessage;

public class PauseMessageTest extends SerializationTest<PauseMessage> {
    
    @Test
    public void testSerializePlayMessage() throws IOException {
        PauseMessage oldMessage = new PauseMessage();
        PauseMessage newMessage = super.testSerializeMessage(oldMessage);
        
        assertEquals("Pause", newMessage.getPauseMessage());
    }
}
