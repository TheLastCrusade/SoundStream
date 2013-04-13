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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.junit.Test;

import com.lastcrusade.soundstream.net.core.AComplexDataType;
import com.lastcrusade.soundstream.net.wire.FileReceiver;
import com.lastcrusade.soundstream.net.wire.Messenger;
import com.lastcrusade.soundstream.net.wire.PacketFormat;

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
        InputStream is = simulateSendAndReceive(baos);
        //attempt to deserialize it
        assertTrue(messenger.deserializeMessage(is));
        
        //and check the deserialized message
        IMessage message = messenger.getReceivedMessages().get(0);
        assertNotNull(message);
        assertTrue(message instanceof StringMessage);
        assertEquals(testMessage, ((StringMessage)message).getString());
        //make sure all bytes are consumed
        assertEquals(0, is.available());
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
        //FIXME: rewrite this so that it's one message per packet, but multiple packets in the input stream

        InputStream is = simulateSendAndReceive(baos);

        //attempt to deserialize the first message
        assertTrue(messenger.deserializeMessage(is));
        
        //and check the deserialized message
        IMessage message = messenger.getReceivedMessages().get(0);
        assertNotNull(message);
        assertTrue(message instanceof StringMessage);
        assertEquals(testMessage, ((StringMessage)message).getString());

        //attempt to deserialize the second message
        assertTrue(messenger.deserializeMessage(is));
        
        //and check the deserialized message
        message = messenger.getReceivedMessages().get(1);
        assertNotNull(message);
        assertTrue(message instanceof StringMessage);
        assertEquals(testMessage2, ((StringMessage)message).getString());

        //make sure all bytes are consumed
        assertEquals(0, is.available());
    }
    
    @Test
    public void testDeserializeFileMessage() throws Exception {

        File tempFile = getTempTestFile();
        //get the temp folder
        Messenger messenger = new Messenger(File.createTempFile("test", "").getParentFile());
        //build up a TestMessage object
        FileMessage testMessage = new FileMessage();
        testMessage.setFilePath(tempFile.getCanonicalPath());
        InputStream is = simulateSendAndReceive(testMessage);

        //attempt to deserialize the first message
        assertTrue(messenger.deserializeMessage(is));
        
        //and check the deserialized message
        IMessage message = messenger.getReceivedMessages().get(0);
        assertNotNull(message);
        assertTrue(message instanceof FileMessage);
        //different paths
        assertFalse(tempFile.getCanonicalPath().equals(((FileMessage)message).getFilePath()));
        assertFileEquals(tempFile, new File(((FileMessage)message).getFilePath()));
        //make sure all bytes are consumed
        assertEquals(0, is.available());
    }
    /**
     * @param tempFile
     * @param file
     * @throws IOException 
     */
    private void assertFileEquals(File expected, File actual) throws IOException {
        FileInputStream expectedS = new FileInputStream(expected);
        FileInputStream actualS   = new FileInputStream(actual);
        
        assertEquals(expectedS.available(), actualS.available());
        for (int ii = 0; ii < expectedS.available(); ii++) {
            assertEquals(expectedS.read(), actualS.read());
        }
    }

    /**
     * @return
     * @throws IOException 
     */
    private File getTempTestFile() throws IOException {
        File file = File.createTempFile("test", ".tst");
        FileWriter writer = new FileWriter(file);
        writer.write("This is a test file.");
        writer.close();
        return file;
    }

    @Test
    public void testSerializeMessage() throws Exception {
        
        Messenger messenger = new Messenger(new File(""));
        
        StringMessage message = new StringMessage();
        String testMessage = "This is a test of the messaging system";
        message.setString(testMessage);
        InputStream is = messenger.serializeMessage(message);
        is = simulateSendAndReceive(is);
        
        Messenger rcvMessenger = new Messenger(new File(""));
        //attempt to deserialize the second message
        assertTrue(rcvMessenger.deserializeMessage(is));
        
        //and check the deserialized message
        IMessage rcvMessage = rcvMessenger.getReceivedMessages().get(0);
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
        InputStream is = messenger.serializeMessage(message);
        is = simulateSendAndReceive(is);
        
        Messenger rcvMessenger = new Messenger(new File(""));
        //attempt to deserialize the second message
        assertTrue(rcvMessenger.deserializeMessage(is));
        
        //and check the deserialized message
        IMessage rcvMessage = rcvMessenger.getReceivedMessages().get(0);
        assertNotNull(rcvMessage);
        assertTrue(rcvMessage instanceof StringMessage);
        assertEquals(testMessage, ((StringMessage)rcvMessage).getString());

        //attempt to deserialize the second message
        assertTrue(rcvMessenger.deserializeMessage(is));
        
        //and check the deserialized message
        rcvMessage = rcvMessenger.getReceivedMessages().get(0);
        assertNotNull(rcvMessage);
        assertTrue(rcvMessage instanceof StringMessage);
        assertEquals(testMessage2, ((StringMessage)rcvMessage).getString());
        //make sure all bytes are consumed
        assertEquals(0, is.available());
    }
    
    private InputStream simulateSendAndReceive(
            InputStream is) throws IOException {
        return is;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        is.writeToOutputStream(baos);
//        return new ByteArrayInputStream(baos.toByteArray());
    }
    
    
    /**
     * @param baos
     * @return
     * @throws IOException 
     */
    private InputStream simulateSendAndReceive(ByteArrayOutputStream baos) throws IOException {
        byte[] bytes = baos.toByteArray();
        baos.reset();
        int testMessageNo = 1;
        PacketFormat format = new PacketFormat(testMessageNo, bytes);
        format.serialize(baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * @param baos
     * @return
     * @throws IOException 
     */
    private InputStream simulateSendAndReceive(IFileMessage message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessageFormat format = new MessageFormat(message);
        
        format.serialize(baos);
        FileReceiver fileFormat = new FileReceiver(message, null);
        ByteBuffer bb = ByteBuffer.allocate(4);
        InputStream fis = fileFormat.getInputStream();
        int length = fis.available();
        bb.putInt(length);
        baos.write(bb.array());
        byte[] temp = new byte[1024];
        fis.read(temp);
        baos.write(temp, 0, length);
        byte[] bytes = baos.toByteArray();
        baos.reset();
        int testMessageNo = 1;
        PacketFormat pf = new PacketFormat(testMessageNo, bytes);
        pf.serialize(baos);
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
        baos.write(new byte[AComplexDataType.SIZEOF_INTEGER]);
        baos.write(new byte[AComplexDataType.SIZEOF_INTEGER]);
        baos.write(className.getBytes());
        baos.write('\n');
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(testMessage.getBytes().length);
        baos.write(bb.array());
        baos.write(testMessage.getBytes());
        byte[] bytes = baos.toByteArray();
        bb = ByteBuffer.wrap(bytes, start, AComplexDataType.SIZEOF_INTEGER + AComplexDataType.SIZEOF_INTEGER);
        int len = baos.size() - start - AComplexDataType.SIZEOF_INTEGER;
        bb.putInt(len);
        bb.putInt(1);
        
        baos.reset();
        baos.write(bytes);
    }
}
