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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author thejenix
 *
 */
public class WireSendInputStream extends InputStream {

    private InputStream message;
    private InputStream file;
    private int packetSize;
    private int messageNo;
    private byte[] packet;
    private int packetIndex = 0;
    
    public WireSendInputStream(int packetSize, int messageNo, InputStream message, InputStream file) {
        this.message = message;
        this.file = file;
        this.messageNo = messageNo;
        this.packetSize = packetSize;
        this.packet = null;
    }

    @Override
    public int available() throws IOException {
        int bytesLeft = getMessageBytesLeft();
        int packets = bytesLeft > 0 ? 1 + (bytesLeft % this.packetSize) : 0;
        return bytesLeft + packets * PacketFormat.getOverhead();
    }

    /**
     * @return
     * @throws IOException
     */
    private int getMessageBytesLeft() throws IOException {
        return this.message.available() + (this.file != null ? this.file.available() : 0);
    }

    @Override
    public int read() throws IOException {
        //on every read, check to see if we need to advance to the next packet.
        advanceIfNeeded();
        return packet != null ? packet[packetIndex++] : -1;
    }

    /**
     * @throws IOException 
     * 
     */
    private void advanceIfNeeded() throws IOException {
        if (packet == null || packetIndex >= packet.length) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //grab the next x bytes (up to packetSize)
            byte[] nextBytes = readNextBytes();
            if (nextBytes.length > 0) {
                PacketFormat format = new PacketFormat(this.messageNo, nextBytes);
                format.serialize(baos);
                packet = baos.toByteArray();
                packetIndex = 0;
            } else {
                packet = null;
            }
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
        int len;
        for (len = PacketFormat.getOverhead(); len < this.packetSize; len++) {
            int ret = -1;
            if (this.message.available() > 0) {
                ret = this.message.read();
            } else if (file != null && file.available() > 0) {
                ret = this.file.read();
            } else {
                break;
            }

            baos.write(ret);
        }
        return baos.toByteArray();
    }
}
