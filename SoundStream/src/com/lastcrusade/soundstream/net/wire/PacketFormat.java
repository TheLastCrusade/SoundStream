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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lastcrusade.soundstream.net.core.AComplexDataType;
import com.lastcrusade.soundstream.net.core.ISerializable;

/**
 * This file represents the format for a packet sent across the wire.
 * Messages are chunked into several packets and sent one at a time, and
 * the receiver can use the message number to aggregate data for multiple
 * packets at a time.  This allows us to still send commands while we're
 * transferring long file data over a single communications channel.
 * 
 * A packet consists of:
 *  integer length of the message, not including the length (using java.nio.ByteBuffer, size defined by {@link AComplexDataType#SIZEOF_INTEGER})
 *  integer byte packet number (to disambiguate packets, size defined by {@link AComplexDataType#SIZEOF_INTEGER})
 *  n bytes of packet data
 *  
 * @author Jesse Rosalia
 *
 */
public class PacketFormat extends AComplexDataType implements ISerializable {

    public  static int HEADER_LEN = 2 * SIZEOF_INTEGER;

    private int packetLength;
    private int messageNo;
    private byte[] bytes;

    public PacketFormat() {
        //
    }
    /**
     * @param messageNo
     * @param nextBytes
     */
    public PacketFormat(int messageNo, byte[] bytes) {
        this.packetLength = bytes.length + SIZEOF_INTEGER; //for message no
        this.messageNo = messageNo;
        this.bytes = bytes;
    }

    public static int getLengthOverhead() {
        return SIZEOF_INTEGER;
    }
    public static int getMessageNoOverhead() {
        return SIZEOF_INTEGER;
    }

    public static int getOverhead() {
        return HEADER_LEN;
    }

    @Override
    public void deserialize(InputStream input) throws IOException, MessageNotCompleteException {
        this.packetLength     = readInteger(input);
        if (input.available() < this.packetLength) {
            throw new MessageNotCompleteException();
        }
        this.messageNo = readInteger(input);
        this.bytes     = readBytes(input, this.packetLength - SIZEOF_INTEGER);
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        writeInteger(packetLength,   output);
        writeInteger(this.messageNo, output);
        writeBytes(  this.bytes,     output);
    }
    
    /**
     * @return the bytes
     */
    public byte[] getBytes() {
        return bytes;
    }
    /**
     * @return the messageNo
     */
    public int getMessageNo() {
        return messageNo;
    }
    
    /**
     * @return the packetLength
     */
    public int getPacketLength() {
        return packetLength;
    }
}
