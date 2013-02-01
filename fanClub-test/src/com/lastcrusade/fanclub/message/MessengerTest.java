package com.lastcrusade.fanclub.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

public class MessengerTest {

    private int bytesToLength(byte[] bytes) {
        return bytes[0] + bytes[1] << 8 + bytes[2] << 16 + bytes[3] << 24;
    }

    private byte[] lengthToBytes(int length) {
        byte[] lenBytes = new byte[4];
        lenBytes[0] = (byte)(length & 0xFF);
        lenBytes[1] = (byte)((length >>  8) & 0xFF);
        lenBytes[2] = (byte)((length >> 16) & 0xFF);
        lenBytes[3] = (byte)((length >> 24) & 0xFF);
        return lenBytes;
    }

    @Test
    public void testDeserializeMessage() throws IOException {
        
        //test the simple case (one message within the stream)
        Messenger messenger = new Messenger();
        //build up a TestMessage object
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String className   = StringMessage.class.getCanonicalName();
        String testMessage = "This is a test of the messaging system";
        appendMessage(className, testMessage, baos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        //attempt to deserialize it
        assertTrue(messenger.deserializeMessage(bais));
        
        //and check the deserialized message
        IMessage message = messenger.getReceivedMessage();
        assertNotNull(message);
        assertTrue(message instanceof StringMessage);
        assertEquals(testMessage, ((StringMessage)message).getString());
        //make sure all bytes are consumed
        assertEquals(0, bais.available());
    }
    
    @Test
    public void testDeserializeMessageMultiple() throws IOException {

        //test the simple case (one message within the stream)
        Messenger messenger = new Messenger();
        //build up a TestMessage object
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String className   = StringMessage.class.getCanonicalName();
        String testMessage = "This is a test of the messaging system";
        //next test 2 messages in the stream, to make sure the messages are consumed properly
        String testMessage2 = "This is another test of the messaging system";
        appendMessage(className, testMessage,  baos);
        appendMessage(className, testMessage2, baos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        //attempt to deserialize the first message
        assertTrue(messenger.deserializeMessage(bais));
        
        //and check the deserialized message
        IMessage message = messenger.getReceivedMessage();
        assertNotNull(message);
        assertTrue(message instanceof StringMessage);
        assertEquals(testMessage, ((StringMessage)message).getString());

        //attempt to deserialize the second message
        assertTrue(messenger.deserializeMessage(bais));
        
        //and check the deserialized message
        message = messenger.getReceivedMessage();
        assertNotNull(message);
        assertTrue(message instanceof StringMessage);
        assertEquals(testMessage2, ((StringMessage)message).getString());

        //make sure all bytes are consumed
        assertEquals(0, bais.available());
    }

    private void appendMessage(String className,
            String testMessage, ByteArrayOutputStream baos) throws IOException {
        //only write the length bytes the first time through
        int start = baos.size();
        baos.write(new byte[4]);
        baos.write(className.getBytes());
        baos.write('\n');
        baos.write(testMessage.getBytes());
        byte[] bytes = baos.toByteArray();
        ByteBuffer bb = ByteBuffer.wrap(bytes, start, 4);
        int len = baos.size() - start - 4;
        bb.putInt(len);
        
        baos.reset();
        baos.write(bytes);
    }
}
