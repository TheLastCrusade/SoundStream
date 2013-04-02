/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * Doesn't inherit from SerializationTest and is separate from its generalized
 * test structure. Meaning, the code repetition between this class and
 * SerializationTest is necessary!
 */
public class MessengerTest {

    //TODO: add tests for partial messages, to make sure we handle the case where data isn't all there yet
    
    @Test
    public void testDeserializeMessage() throws Exception {
        
        //test the simple case (one message within the stream)
        Messenger messenger = new Messenger(new File(""));
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
    public void testDeserializeMessageMultiple() throws Exception {

        //test multiple complete messages in one stream
        Messenger messenger = new Messenger(new File(""));
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
    
    @Test
    public void testSerializeMessage() throws Exception {
        
        Messenger messenger = new Messenger(new File(""));
        
        StringMessage message = new StringMessage();
        String testMessage = "This is a test of the messaging system";
        message.setString(testMessage);
        messenger.serializeMessage(message);
        InputStream is = simulateSendAndReceive(messenger);
        
        Messenger rcvMessenger = new Messenger(new File(""));
        //attempt to deserialize the second message
        assertTrue(rcvMessenger.deserializeMessage(is));
        
        //and check the deserialized message
        IMessage rcvMessage = rcvMessenger.getReceivedMessage();
        assertNotNull(rcvMessage);
        assertTrue(rcvMessage instanceof StringMessage);
        assertEquals(testMessage, ((StringMessage)rcvMessage).getString());

        //make sure all bytes are consumed
        assertEquals(0, is.available());
    }

    @Test
    public void testSerializeMessageMultiple() throws Exception {
        
        //next test 2 messages in the stream, to make sure the messages are consumed properly
        Messenger messenger = new Messenger(new File(""));
        
        StringMessage message = new StringMessage();
        String testMessage = "This is a test of the messaging system";
        message.setString(testMessage);
        messenger.serializeMessage(message);
        String testMessage2 = "This is another test of the messaging system";
        message = new StringMessage();
        message.setString(testMessage2);
        messenger.serializeMessage(message);
        InputStream is = simulateSendAndReceive(messenger);
        
        Messenger rcvMessenger = new Messenger(new File(""));
        //attempt to deserialize the second message
        assertTrue(rcvMessenger.deserializeMessage(is));
        
        //and check the deserialized message
        IMessage rcvMessage = rcvMessenger.getReceivedMessage();
        assertNotNull(rcvMessage);
        assertTrue(rcvMessage instanceof StringMessage);
        assertEquals(testMessage, ((StringMessage)rcvMessage).getString());

        //attempt to deserialize the second message
        assertTrue(rcvMessenger.deserializeMessage(is));
        
        //and check the deserialized message
        rcvMessage = rcvMessenger.getReceivedMessage();
        assertNotNull(rcvMessage);
        assertTrue(rcvMessage instanceof StringMessage);
        assertEquals(testMessage2, ((StringMessage)rcvMessage).getString());
        //make sure all bytes are consumed
        assertEquals(0, is.available());
    }
    
    private InputStream simulateSendAndReceive(
            Messenger messenger) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        messenger.writeToOutputStream(baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }
    
    /**
     * NOTE: keep this separate, so we have independent verification of the Messenger.  This lets
     * us use the independently verified side to test the other side of the messenger.
     * @param className
     * @param testMessage
     * @param baos
     * @throws IOException
     */
    private void appendMessage(String className,
            String testMessage, ByteArrayOutputStream baos) throws IOException {
        //only write the length bytes the first time through
        int start = baos.size();
        baos.write(new byte[Messenger.SIZE_LEN]);
        baos.write(new byte[Messenger.VERSION_LEN]);
        baos.write(className.getBytes());
        baos.write('\n');
        baos.write(testMessage.getBytes());
        byte[] bytes = baos.toByteArray();
        ByteBuffer bb = ByteBuffer.wrap(bytes, start, Messenger.SIZE_LEN + Messenger.VERSION_LEN);
        int len = baos.size() - start - Messenger.SIZE_LEN;
        bb.putInt(len);
        bb.putInt(Messenger.MESSENGER_VERSION);
        
        baos.reset();
        baos.write(bytes);
    }
}
