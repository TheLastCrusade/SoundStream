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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.soundstream.net.core.AComplexDataType;
import com.lastcrusade.soundstream.net.wire.FileReceiver;
import com.lastcrusade.soundstream.net.wire.Messenger;
import com.lastcrusade.soundstream.net.wire.PacketFormat;
import com.lastcrusade.soundstream.util.CustomAssert;
import com.lastcrusade.soundstream.util.InputBuffer;
import com.lastcrusade.soundstream.util.MessageTestUtil;

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
        InputStream received = simulateSendAndReceive(baos);
        //attempt to deserialize it
        assertTrue(messenger.deserializeMessage(received));
        
        //and check the deserialized message
        IMessage message = messenger.getReceivedMessages().get(0);
        assertNotNull(message);
        assertTrue(message instanceof StringMessage);
        assertEquals(testMessage, ((StringMessage)message).getString());
        //make sure all bytes are consumed
        assertEquals(0, received.available());
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
        int expectedMessages = 2;
        appendMessage(className, testMessage,  baos);
        appendMessage(className, testMessage2, baos);
        //FIXME: rewrite this so that it's one message per packet, but multiple packets in the input stream

        InputStream received = simulateSendAndReceive(baos);

        //attempt to deserialize the data...this should deserialize both messages
        assertTrue(messenger.deserializeMessage(received));
        
        assertEquals(expectedMessages, messenger.getReceivedMessages().size());
        //and check the first deserialized message
        IMessage message = messenger.getReceivedMessages().get(0);
        assertNotNull(message);
        assertTrue(message instanceof StringMessage);
        assertEquals(testMessage, ((StringMessage)message).getString());

        //and check the second deserialized message
        message = messenger.getReceivedMessages().get(1);
        assertNotNull(message);
        assertTrue(message instanceof StringMessage);
        assertEquals(testMessage2, ((StringMessage)message).getString());

        //make sure all bytes are consumed
        assertEquals(0, received.available());
    }
    
    @Test
    public void testDeserializeFileMessage() throws Exception {

        File tempFile = MessageTestUtil.getTempTestFile(20);
        //get the temp folder
        Messenger messenger = new Messenger(File.createTempFile("test", "").getParentFile());
        //build up a TestMessage object
        FileMessage testMessage = new FileMessage();
        testMessage.setFilePath(tempFile.getCanonicalPath());
        int packetSize = 1024;
        List<PacketFormat> packets = simulateSendAndReceive(testMessage, packetSize);

        boolean received = false;
        for (PacketFormat packet : packets) {
            assertFalse(received);
            InputBuffer buffer = new InputBuffer();
            packet.serialize(buffer);
            received = messenger.deserializeMessage(buffer.getInputStream());
        }
        //if this asserts false, it means there may be data stuck in the buffer
        // which means we're not processing all available packets
        assertTrue(received);
        
        //and check the deserialized message
        IMessage message = messenger.getReceivedMessages().get(0);
        assertNotNull(message);
        assertTrue(message instanceof FileMessage);
        //different paths
        assertFalse(tempFile.getCanonicalPath().equals(((FileMessage)message).getFilePath()));
        CustomAssert.assertChecksumsMatch(tempFile.getCanonicalPath(), ((FileMessage)message).getFilePath());
    }
    
    @Test
    public void testDeserializeFileMessageMultiplePackets() throws Exception {

        File tempFile = MessageTestUtil.getTempTestFile(1025);
        //get the temp folder
        Messenger messenger = new Messenger(File.createTempFile("test", "").getParentFile());
        //build up a TestMessage object
        FileMessage testMessage = new FileMessage();
        testMessage.setFilePath(tempFile.getCanonicalPath());
        int packetSize = 512;
        List<PacketFormat> packets = simulateSendAndReceive(testMessage, packetSize);

        boolean received = false;
        InputBuffer buffer = new InputBuffer();
        for (int ii = 0; ii < packets.size(); ii++) {
            assertFalse(received);
            PacketFormat packet = packets.get(ii);
            packet.serialize(buffer);
            //we're going to lump the last 2 packets together in the same stream
            //...if the code is right, it should process them just fine
            //...otherwise it may just continue through, and the assertTrue
            //   below this loop will assert false.
            if ((ii + 2) == packets.size()) {
                continue;                
            }
            received = messenger.deserializeMessage(buffer.getInputStream());
            buffer.consume();
        }
        //if this asserts false, it means there may be data stuck in the buffer
        // which means we're not processing all available packets
        assertTrue(received);
        
        //and check the deserialized message
        IMessage message = messenger.getReceivedMessages().get(0);
        assertNotNull(message);
        assertTrue(message instanceof FileMessage);
        //different paths
        assertFalse(tempFile.getCanonicalPath().equals(((FileMessage)message).getFilePath()));
        CustomAssert.assertChecksumsMatch(tempFile.getCanonicalPath(), ((FileMessage)message).getFilePath());
    }

    @Test
    public void testSerializeMessage() throws Exception {
        
        Messenger messenger = new Messenger(new File(""));
        
        StringMessage message = new StringMessage();
        String testMessage = "This is a test of the messaging system";
        message.setString(testMessage);
        InputStream send = messenger.serializeMessage(message);
        InputStream received = simulateSendAndReceive(send);
        
        Messenger rcvMessenger = new Messenger(new File(""));
        //attempt to deserialize the second message
        assertTrue(rcvMessenger.deserializeMessage(received));
        
        //and check the deserialized message
        IMessage rcvMessage = rcvMessenger.getReceivedMessages().get(0);
        assertNotNull(rcvMessage);
        assertTrue(rcvMessage instanceof StringMessage);
        assertEquals(testMessage, ((StringMessage)rcvMessage).getString());

        //make sure all bytes are consumed
        assertEquals(0, received.available());
    }

//TODO: unclear if we need this test...lets leave it alone for a bit
//    @Test
//    public void testSerializeMessageMultiple() throws Exception {
//        
//        //next test 2 messages in the stream, to make sure the messages are consumed properly
//        Messenger messenger = new Messenger(new File(""));
//        
//        StringMessage message = new StringMessage();
//        String testMessage = "This is a test of the messaging system";
//        message.setString(testMessage);
//        messenger.serializeMessage(message);
//        String testMessage2 = "This is another test of the messaging system";
//        message = new StringMessage();
//        message.setString(testMessage2);
//        InputStream is = messenger.serializeMessage(message);
//        is = simulateSendAndReceive(is);
//        
//        Messenger rcvMessenger = new Messenger(new File(""));
//        //attempt to deserialize the second message
//        assertTrue(rcvMessenger.deserializeMessage(is));
//        
//        //and check the deserialized message
//        IMessage rcvMessage = rcvMessenger.getReceivedMessages().get(0);
//        assertNotNull(rcvMessage);
//        assertTrue(rcvMessage instanceof StringMessage);
//        assertEquals(testMessage, ((StringMessage)rcvMessage).getString());
//
//        //attempt to deserialize the second message
//        assertTrue(rcvMessenger.deserializeMessage(is));
//        
//        //and check the deserialized message
//        rcvMessage = rcvMessenger.getReceivedMessages().get(0);
//        assertNotNull(rcvMessage);
//        assertTrue(rcvMessage instanceof StringMessage);
//        assertEquals(testMessage2, ((StringMessage)rcvMessage).getString());
//        //make sure all bytes are consumed
//        assertEquals(0, is.available());
//    }
    
    private InputStream simulateSendAndReceive(
            InputStream is) throws IOException {
        return is;
    }
    
    
    /**
     * @param baos
     * @return
     * @throws IOException 
     */
    private InputStream simulateSendAndReceive(ByteArrayOutputStream baos) throws IOException {
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * @param baos
     * @return
     * @throws IOException 
     */
    private List<PacketFormat> simulateSendAndReceive(IFileMessage message, int packetSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessageFormat format = new MessageFormat(message);
        
        format.serialize(baos);
        FileReceiver fileReceiver = new FileReceiver(message, null);
        ByteBuffer bb = ByteBuffer.allocate(AComplexDataType.SIZEOF_INTEGER);
        InputStream fis = fileReceiver.getInputStream();
        int length = fis.available();
        bb.putInt(length);
        baos.write(bb.array());
        byte[] temp = new byte[1024];
        int read = 0;
        while ((read = fis.read(temp)) > 0) {
            baos.write(temp, 0, read);
        }
        byte[] bytes = baos.toByteArray();
        baos.reset();
        
        int testMessageNo = 1;
        int payloadSize = packetSize - PacketFormat.getOverhead();
        List<PacketFormat> packets = new ArrayList<PacketFormat>();
        for (int ii = 0; ii < bytes.length; ii += payloadSize) {
            int toWrite = Math.min(payloadSize, bytes.length - ii);
            baos.write(bytes, ii, toWrite);
            PacketFormat pf = new PacketFormat(testMessageNo, baos.toByteArray());
            packets.add(pf);
            baos.reset();
        }
        return packets;

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
            String testMessage, ByteArrayOutputStream output) throws IOException {
        
        ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
        //only write the length bytes the first time through
        int start = messageStream.size();
        messageStream.write(new byte[AComplexDataType.SIZEOF_INTEGER]);
        messageStream.write(new byte[AComplexDataType.SIZEOF_INTEGER]);
        messageStream.write(className.getBytes());
        messageStream.write('\n');
        ByteBuffer bb = ByteBuffer.allocate(AComplexDataType.SIZEOF_INTEGER);
        bb.putInt(testMessage.getBytes().length);
        messageStream.write(bb.array());
        messageStream.write(testMessage.getBytes());
        byte[] bytes = messageStream.toByteArray();
        //add the message format header
        bb = ByteBuffer.wrap(bytes, start, AComplexDataType.SIZEOF_INTEGER + AComplexDataType.SIZEOF_INTEGER);
        int len = messageStream.size() - start - AComplexDataType.SIZEOF_INTEGER;
        bb.putInt(len);
        bb.putInt(1);

        //add the packet format header...this is done one per message
        PacketFormat format = new PacketFormat(1, bytes);
        format.serialize(output);
    }
}
