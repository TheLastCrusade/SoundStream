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
package com.lastcrusade.soundstream.net.wire;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.junit.Test;

import com.lastcrusade.soundstream.net.core.AComplexDataType;
import com.lastcrusade.soundstream.util.InputBuffer;

/**
 * @author thejenix
 *
 */
public class WireSendInputStreamTest {

    /**
     * @return
     * @throws IOException 
     */
    private File getTempTestFile(int size) throws IOException {
        File file = File.createTempFile("test", ".tst");
        FileWriter writer = new FileWriter(file);
        for (int ii = 0; ii < size; ii++) {
            writer.append("a");
        }
        writer.close();
        return file;
    }

    /**
     * @return
     */
    private InputStream getTestStream(int byteCount) {
        InputBuffer buffer = new InputBuffer();
        for (int ii = 0; ii < byteCount; ii++) {
            buffer.write(ii);
        }
        return buffer.getInputStream();
    }

    /**
     * Test method for {@link com.lastcrusade.soundstream.net.wire.WireSendInputStream#available()}.
     * @throws IOException 
     */
    @Test
    public void testAvailable() throws IOException {
        int packetSize = 100;
        int messageNo  = 1;
        InputStream test = getTestStream(packetSize * 2);
        WireSendInputStream is = new WireSendInputStream(packetSize, messageNo, test, null);
        assertEquals(computeExpectedSize(test.available(), packetSize), is.available());
    }

    /**
     * @param available
     * @param packetSize
     * @return
     */
    private int computeExpectedSize(int available, int packetSize) {
        int payloadSize = packetSize - PacketFormat.getOverhead();
        int packets = available > 0 ? 1 + (available / payloadSize) : 0;
        return available + packets * PacketFormat.getOverhead();
    }

    /**
     * Test method for {@link com.lastcrusade.soundstream.net.wire.WireSendInputStream#available()}.
     * @throws IOException 
     */
    @Test
    public void testAvailableWithFile() throws IOException {
        int packetSize = 100;
        int messageNo  = 1;
        InputStream test = getTestStream(packetSize * 2);
        File file = getTempTestFile(100);
        InputStream fileStream = new FileInputStream(file);
        try {
            WireSendInputStream is = new WireSendInputStream(packetSize, messageNo, test, fileStream);
            int expectedBytes = test.available() + fileStream.available() + AComplexDataType.SIZEOF_INTEGER;
            assertEquals(computeExpectedSize(expectedBytes, packetSize), is.available());
        } finally {
            file.delete();
        }
    }

    /**
     * Test method for {@link com.lastcrusade.soundstream.net.wire.WireSendInputStream#available()}.
     * @throws IOException 
     */
    @Test
    public void testAvailableWithLargeFile() throws IOException {
        int packetSize = 1024;
        int messageNo  = 1;
        //NOTE: these test values to cover a specific edge case where we weren't calculating the packet count properly
        //...using these values, we will miss 1 packet if 
        InputStream test = getTestStream(100);
        File file = getTempTestFile(17294);
        InputStream fileStream = new FileInputStream(file);
        try {
            WireSendInputStream is = new WireSendInputStream(packetSize, messageNo, test, fileStream);
            int expectedBytes = test.available() + fileStream.available() + AComplexDataType.SIZEOF_INTEGER;
            assertEquals(computeExpectedSize(expectedBytes, packetSize), is.available());
            int expectedPackets = 18;
            byte[] buf = new byte[packetSize];
            for (int ii = 0; ii < expectedPackets; ii++) {
                int read = is.read(buf);
                assertTrue(read > 0);
            }
            //it should be 18 packets exactly
            assertEquals(0, is.available());
            
        } finally {
            file.delete();
        }
    }

    /**
     * Test method for {@link com.lastcrusade.soundstream.net.wire.WireSendInputStream#read()}.
     * @throws IOException 
     */
    @Test
    public void testRead() throws IOException {
        int packetSize = 100;
        int messageNo  = 1;
        InputStream expected = getTestStream(packetSize * 2);
        InputStream test = getTestStream(packetSize * 2);
        WireSendInputStream is = new WireSendInputStream(packetSize, messageNo, test, null);
        int bytesLeft = computeExpectedSize(test.available(), packetSize);
        assertEquals(bytesLeft, is.available());

        for (; bytesLeft > 0; ) {
            int toRead = Math.min(packetSize, bytesLeft);
            int payloadSize = toRead - PacketFormat.getLengthOverhead();
            int dataSize    = payloadSize - PacketFormat.getMessageNoOverhead();
            verifyPacket(is, expected, messageNo, payloadSize, dataSize);
            
            bytesLeft -= toRead;
            assertEquals(bytesLeft, is.available());
        }

        //this should be the last one
        assertEquals(0, is.available());
        assertEquals(-1, is.read());
    }

    /**
     * @param is
     * @param messageNo
     * @param counter
     * @param toRead 
     * @param payloadSize
     * @param dataSize
     * @return
     * @throws IOException
     */
    private void verifyPacket(InputStream toTest, InputStream expectedData,
            int messageNo, int payloadSize, int dataSize) throws IOException {
        verifyPacketFormat(payloadSize, messageNo, toTest);
        for (int ii = 0; ii < dataSize; ii++) {
            assertEquals((byte)expectedData.read(), (byte)toTest.read());
        }
    }

    /**
     * @param is
     * @param expectedFile
     * @param fileBytes
     * @param rest
     * @throws IOException 
     */
    private void verifyFileStart(InputStream toTest,
            InputStream expectedData, int rest) throws IOException {
        byte[] in = new byte[AComplexDataType.SIZEOF_INTEGER];
        for (int ii = 0; ii < AComplexDataType.SIZEOF_INTEGER; ii++) {
            in[ii] = (byte) toTest.read();
        }
        ByteBuffer bb = ByteBuffer.wrap(in);
        int len = bb.getInt();
        assertEquals(len, expectedData.available());
        rest -= 4;
        for (int ii = 0; ii < rest; ii++) {
            assertEquals((byte)expectedData.read(), (byte)toTest.read());
        }
    }


    /**
     * @param messageNo
     * @param messageNo2 
     * @param is
     * @throws IOException
     */
    private void verifyPacketFormat(int length, int messageNo, InputStream is)
            throws IOException {
        byte[] in = new byte[AComplexDataType.SIZEOF_INTEGER];
        for (int ii = 0; ii < AComplexDataType.SIZEOF_INTEGER; ii++) {
            in[ii] = (byte) is.read();
        }
        ByteBuffer bb = ByteBuffer.wrap(in);
        assertEquals(length, bb.getInt());
        in = new byte[AComplexDataType.SIZEOF_INTEGER];
        for (int ii = 0; ii < AComplexDataType.SIZEOF_INTEGER; ii++) {
            in[ii] = (byte) is.read();
        }
        bb = ByteBuffer.wrap(in);
        assertEquals(messageNo, bb.getInt());
    }

    /**
     * Test method for {@link com.lastcrusade.soundstream.net.wire.WireSendInputStream#read()}.
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    @Test
    public void testReadWithFile() throws FileNotFoundException, IOException {
        int packetSize = 100;
        int messageNo  = 1;
        InputStream expected = getTestStream(packetSize * 2);
        InputStream actual   = getTestStream(packetSize * 2);
        File testFile = getTempTestFile(100);
        InputStream expectedFile = new FileInputStream(testFile);
        InputStream actualFile   = new FileInputStream(testFile);
        WireSendInputStream is = new WireSendInputStream(packetSize, messageNo, actual, actualFile);

        int expectedBytes = computeExpectedSize(expected.available() + expectedFile.available() + AComplexDataType.SIZEOF_INTEGER, packetSize);
        int bytesLeft = expectedBytes;
        int messageBytes = computeExpectedSize(expected.available(), packetSize);
        int fileBytes = expectedBytes - messageBytes;
        assertEquals(bytesLeft, is.available());

        for (; bytesLeft > 0; ) {
            int toRead = Math.min(packetSize, bytesLeft);
            if (bytesLeft <= fileBytes) {
                int payloadSize = toRead - PacketFormat.getLengthOverhead();
                int dataSize    = payloadSize - PacketFormat.getMessageNoOverhead();
                verifyPacket(is, expectedFile, messageNo, payloadSize, dataSize);
            } else {
                int partialToRead = Math.min(toRead, bytesLeft - fileBytes);
                int payloadSize = toRead - PacketFormat.getLengthOverhead();
                int dataSize    = partialToRead - PacketFormat.getOverhead();
                verifyPacket(is, expected, messageNo, payloadSize, dataSize);
                int rest = toRead - partialToRead;
                if (rest > 0) {
                    verifyFileStart(is, expectedFile, rest);
                }
            }
            
            bytesLeft -= toRead;
            assertEquals(bytesLeft, is.available());
        }

        assertEquals(0, is.available());
        assertEquals(-1, is.read());
    }

    /**
     * Test method for {@link com.lastcrusade.soundstream.net.wire.WireSendInputStream#read(byte[], int, int)}.
     * @throws IOException 
     */
    @Test
    public void testReadByteArrayIntInt() throws IOException {
        int packetSize = 100;
        int messageNo  = 1;
        InputStream expected = getTestStream(packetSize * 2);
        InputStream test = getTestStream(packetSize * 2);
        WireSendInputStream is = new WireSendInputStream(packetSize, messageNo, test, null);
        int bytesLeft = computeExpectedSize(test.available(), packetSize);
        assertEquals(bytesLeft, is.available());

        byte[] buf = new byte[packetSize];
        for (; bytesLeft > 0; ) {
            int read = is.read(buf, 0, buf.length);
            //verify the same way as testRead
            ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, read);
            int payloadSize = read - PacketFormat.getLengthOverhead();
            int dataSize    = payloadSize - PacketFormat.getMessageNoOverhead();
            verifyPacket(bais, expected, messageNo, payloadSize, dataSize);

            bytesLeft -= read;
            assertEquals(bytesLeft, is.available());
        }

        //this should be the last one
        assertEquals(0, is.available());
        assertEquals(-1, is.read());
    }

    /**
     * Test method for {@link com.lastcrusade.soundstream.net.wire.WireSendInputStream#read(byte[], int, int)}.
     * @throws IOException 
     */
    @Test
    public void testReadByteArrayIntIntOddPacketSize() throws IOException {
//        int packetSize = 100;
//        int messageNo  = 1;
//        InputStream test = getTestStream(packetSize * 2);
//        WireSendInputStream is = new WireSendInputStream(packetSize, messageNo, test, null);
//        int bytesLeft = computeExpectedSize(test.available(), packetSize);
//        assertEquals(bytesLeft, is.available());
//
//        byte[] buf = new byte[(int) (packetSize * 1.5)];
//        int counter = 0;
//        int off = 0;
//        for (; bytesLeft > 0; ) {
//            int read = is.read(buf, off, buf.length - off);
//            int payloadSize = Math.min(read, packetSize) - PacketFormat.getLengthOverhead();
//            int dataSize    = payloadSize - PacketFormat.getMessageNoOverhead();
//            //verify the same way as testRead
//            ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, read);
//            verifyPacketFormat(payloadSize, messageNo, bais);
//            for (int ii = 0; ii < dataSize; counter++, ii++) {
//                assertEquals((byte)counter, (byte)bais.read());
//            }
//
//            if (bais.available() > 0) {
//                off = bais.read(buf);
//            } else {
//                off = 0;
//            }
//            bytesLeft -= read;
//            assertEquals(bytesLeft, is.available());
//        }
//
//        //this should be the last one
//        assertEquals(0, is.available());
//        assertEquals(-1, is.read());
    }

    /**
     * Test method for {@link com.lastcrusade.soundstream.net.wire.WireSendInputStream#read(byte[], int, int)}.
     * @throws IOException 
     */
    @Test
    public void testReadByteArrayIntIntWithFile() throws IOException {
        int packetSize = 100;
        int messageNo  = 1;
        InputStream expected = getTestStream(packetSize * 2);
        InputStream actual   = getTestStream(packetSize * 2);
        File testFile = getTempTestFile(100);
        try {
            InputStream expectedFile = new FileInputStream(testFile);
            InputStream actualFile   = new FileInputStream(testFile);
            WireSendInputStream is = new WireSendInputStream(packetSize, messageNo, actual, actualFile);
    
            int expectedBytes = computeExpectedSize(expected.available() + expectedFile.available() + AComplexDataType.SIZEOF_INTEGER, packetSize);
            int bytesLeft = expectedBytes;
            int messageBytes = computeExpectedSize(expected.available(), packetSize);
            int fileBytes = expectedBytes - messageBytes;
            assertEquals(bytesLeft, is.available());
    
            byte[] buf = new byte[packetSize];
            for (; bytesLeft > 0; ) {
                int read = is.read(buf, 0, buf.length);
                //verify the same way as testRead
                ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, read);
                if (bytesLeft <= fileBytes) {
                    int payloadSize = read - PacketFormat.getLengthOverhead();
                    int dataSize    = payloadSize - PacketFormat.getMessageNoOverhead();
                    verifyPacket(bais, expectedFile, messageNo, payloadSize, dataSize);
                } else {
                    int partialToRead = Math.min(read, bytesLeft - fileBytes);
                    int payloadSize = read - PacketFormat.getLengthOverhead();
                    int dataSize    = partialToRead - PacketFormat.getOverhead();
                    verifyPacket(bais, expected, messageNo, payloadSize, dataSize);
                    int rest = read - partialToRead;
                    if (rest > 0) {
                        verifyFileStart(bais, expectedFile, rest);
                    }
                }
                
                bytesLeft -= read;
                assertEquals(bytesLeft, is.available());
            }
    
            assertEquals(0, is.available());
            assertEquals(-1, is.read());
        } finally {
            testFile.delete();
        }
    }
}
