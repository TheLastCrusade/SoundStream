package com.lastcrusade.fanclub.message;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SkipMessageTest extends SerializationTest<SkipMessage> {
    
    @Test
    public void testSerializeSkipMessage() throws IOException {
        SkipMessage oldMessage = new SkipMessage();
        SkipMessage newMessage = super.testSerializeMessage(oldMessage);
        
        assertEquals("Skip", newMessage.getSkipMessage());
    }

}
