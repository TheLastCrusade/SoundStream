package com.lastcrusade.fanclub.net.message;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PauseMessageTest extends SerializationTest<PauseMessage> {
    
    @Test
    public void testSerializePlayMessage() throws IOException {
        PauseMessage oldMessage = new PauseMessage();
        PauseMessage newMessage = super.testSerializeMessage(oldMessage);
        
        assertEquals("Pause", newMessage.getPauseMessage());
    }
}
