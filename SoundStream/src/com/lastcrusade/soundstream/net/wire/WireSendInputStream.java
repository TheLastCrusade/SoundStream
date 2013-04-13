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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.lastcrusade.soundstream.net.core.AComplexDataType;
import com.lastcrusade.soundstream.net.core.IntegerLength;
import com.lastcrusade.soundstream.util.LogUtil;

/**
 * An input stream used for reading packets to send via a network.
 * 
 * This class takes in a serialized message and an optional file
 * stream, and will chunk the data into packets.  These packets
 * can then be read from the stream and transmitted to the remote
 * system.
 * 
 * @author Jesse Rosalia
 *
 */
public class WireSendInputStream extends InputStream {

    private static final String TAG = WireSendInputStream.class.getSimpleName();

    private InputStream message;
    private InputStream file;
    private int packetSize;
    private int messageNo;
    private byte[] packet;
    private int packetIndex = 0;
    private ByteArrayInputStream fileLengthStream;
    private int available;
    
    public WireSendInputStream(int packetSize, int messageNo, InputStream message, InputStream file) throws IOException {
        this.message = message;
        this.file = file;
        this.messageNo = messageNo;
        this.packetSize = packetSize;
        this.packet = null; 

        this.available = this.message.available();
        //create an input stream to hold the file length, only once (first time through)
        if (this.file != null && this.fileLengthStream == null) {
            this.available += file.available() + AComplexDataType.SIZEOF_INTEGER;
            IntegerLength length = new IntegerLength(file.available());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            length.serialize(baos);
            this.fileLengthStream = new ByteArrayInputStream(baos.toByteArray());
        }

        //precompute the total number of bytes available from this input stream
        int payloadSize = this.packetSize - PacketFormat.getOverhead();
        int packets = this.available > 0 ? 1 + (this.available / payloadSize) : 0;
        this.available += packets * PacketFormat.getOverhead();

        if (LogUtil.isLogAvailable()) {
            Log.d(TAG, "Preparing to send " + this.available + " bytes across the wire.");
        }
    }

    @Override
    public int available() throws IOException {
        return this.available;
    }

    @Override
    public int read(byte[] buffer, int off, int maxLen) throws IOException {
        if (buffer == null) {
            throw new NullPointerException();
        } else if (off < 0 || maxLen < 0 || maxLen > buffer.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (maxLen == 0) {
            return 0;
        }
        if (this.available() <= 0) {
            return -1;
        }

        advanceIfNeeded();

        int read   = 0;
        int toRead = Math.min(this.available(), maxLen);
        while (availableInPacket() <= toRead && available() > 0) {
//            int 
            int partial = Math.min(availableInPacket(), toRead);
            System.arraycopy(packet, packetIndex, buffer, off + read, partial);
            packetIndex += partial;
            available   -= partial;
            advanceIfNeeded();
            read        += partial;
            toRead      -= partial;
        }

//        packetIndex += toRead;
//        int len = 0;
//        for (; len < toRead; len++) {
//            int c = read();
//            buffer[off + len] = (byte) c;
//        }
//        return len;
        return read;
    }

    /**
     * @return
     */
    private int availableInPacket() {
        return packet != null ? packet.length - packetIndex : 0;
    }

    @Override
    public int read() throws IOException {
        //on every read, check to see if we need to advance to the next packet.
        advanceIfNeeded();
        return readNext();
    }

    /**
     * @throws IOException 
     * 
     */
    private void advanceIfNeeded() throws IOException {
        if (packet == null || packetIndex >= packet.length) {
            //grab the next x bytes (up to packetSize)
            byte[] nextBytes = readNextBytes();
            if (nextBytes.length > 0) {
                PacketFormat format = new PacketFormat(this.messageNo, nextBytes);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                format.serialize(baos);
                packet = baos.toByteArray();
                packetIndex = 0;
            } else {
                packet = null;
            }
        }
    }

    /**
     * Read the next byte from the packet, or return -1 if a next byte doesnt exist.
     * 
     * This should only return -1 when we're done with a stream, as read will
     * call advanceIfNeeded to line up the next set of bytes before calling this.
     * @return
     */
    private int readNext() {
        if (packet != null && packetIndex < packet.length) {
            this.available--;
            return packet[packetIndex++];
        } else {
            return -1;
        }
    }
    /**
     * Read the next segment of bytes, making sure we leave room in the packet
     * for the header info at the front.
     * 
     * @return A byte array of the next segment of bytes to write
     * @throws IOException
     */
    private byte[] readNextBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (this.available() <= 0) {
            return baos.toByteArray();
        }

        int len;
        
        for (len = PacketFormat.getOverhead(); len < this.packetSize && this.message.available() > 0; len++) {
            int ret = -1;
            ret = this.message.read();
            baos.write(ret);
        }
        if (this.fileLengthStream != null) {
            for (; len < this.packetSize && this.fileLengthStream.available() > 0; len++) {
                baos.write(this.fileLengthStream.read());
            }
        }
        
        if (this.file != null) {
            byte[] buf = new byte[this.packetSize - len];
            int read;
            if ((read = this.file.read(buf)) > 0) {
                baos.write(buf, 0, read);
            }
        }

        return baos.toByteArray();
    }
}
