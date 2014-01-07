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
package com.thelastcrusade.soundstream.net.wire;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.thelastcrusade.soundstream.net.core.AComplexDataType;
import com.thelastcrusade.soundstream.net.core.ISerializable;

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

    //an enumeration of control codes...the ordinal value is used as bit positions
    // in controlCodes where 1 indicates that code is present
    public enum ControlCode {
        Cancelled //2^0 => 0x1
        ;

        public int bitPosition() {
            return (int)Math.pow(2, this.ordinal());
        }
    }

    private int packetLength;
    private int controlCodes;
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
        this.packetLength = bytes.length + PacketFormat.getOverhead() - PacketFormat.getLengthOverhead(); //for message no
        this.messageNo = messageNo;
        this.bytes = bytes;
    }

    public static int getControlCodeOverhead() {
        return SIZEOF_INTEGER;
    }

    public static int getLengthOverhead() {
        return SIZEOF_INTEGER;
    }

    public static int getMessageNoOverhead() {
        return SIZEOF_INTEGER;
    }

    public static int getOverhead() {
        return getControlCodeOverhead() + getLengthOverhead() + getMessageNoOverhead();
    }

    public static int getOverheadWithoutLength() {
        return getOverhead() - getLengthOverhead();
    }

    /**
     * @param input
     * @param lengthOverhead
     * @throws MessageNotCompleteException 
     * @throws IOException 
     */
    private void checkAvailable(InputStream input, int required) throws IOException, MessageNotCompleteException {
        if (input.available() < required) {
            throw new MessageNotCompleteException();
        }
    }

    @Override
    public void deserialize(InputStream input) throws IOException, MessageNotCompleteException {
        checkAvailable(input, getLengthOverhead());
        this.packetLength     = readInteger(input);
        checkAvailable(input, this.packetLength);

        this.controlCodes = readInteger(input);
        this.messageNo    = readInteger(input);
        this.bytes        = readBytes(input, this.packetLength - getOverheadWithoutLength());
    }


    @Override
    public void serialize(OutputStream output) throws IOException {
        writeInteger(packetLength,        output);
        writeInteger(this.controlCodes,   output);
        writeInteger(this.messageNo,      output);
        writeBytes(  this.bytes,          output);
    }
    
    public void addControlCode(ControlCode code) {
        this.controlCodes |= code.bitPosition();
    }

    public void clearControlCodes() {
        this.controlCodes = 0;
    }
    
    public boolean isControlCodeSet(ControlCode code) {
        return (this.controlCodes & code.bitPosition()) != 0;
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
