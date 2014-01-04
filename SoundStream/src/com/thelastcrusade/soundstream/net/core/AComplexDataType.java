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
package com.thelastcrusade.soundstream.net.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * A base class that represents a complex data type. Complex data types are
 * composed of fields of simple data types. This class provides generic methods
 * for reading and writing these fields in streams.
 * 
 * @author Jesse Rosalia
 * 
 */
public class AComplexDataType {

    public static final int SIZEOF_BOOLEAN = 1;

    public static final int SIZEOF_INTEGER = 4;
    
    protected void writeByte(byte theByte, OutputStream output)
            throws IOException {
        output.write(theByte);
    }

    protected byte readByte(InputStream input) throws IOException {
        return (byte) input.read();
    }

    protected void writeBytes(byte[] bytes, OutputStream output)
            throws IOException {
        output.write(bytes);
    }

    protected byte[] readBytes(InputStream input, int length)
            throws IOException {
        //length == 0 allows sending control codes only
        if (length < 0) {
            throw new RuntimeException("Length is invalid: " + length);
        }
        // TODO: protection for laaaaaarge byte lengths
        byte[] bytes = new byte[length];
        input.read(bytes);
        return bytes;
    }

    protected void writeBoolean(boolean bool, OutputStream output)
            throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(SIZEOF_BOOLEAN);
        bb.put((byte)(bool ? 1 : 0));
        output.write(bb.array());
    }

    protected boolean readBoolean(InputStream input) throws IOException {
        byte[] in = new byte[SIZEOF_BOOLEAN];
        for (int ii = 0; ii < SIZEOF_BOOLEAN; ii++) {
            in[ii] = (byte) input.read();
        }
        ByteBuffer bb = ByteBuffer.wrap(in);
        return bb.get() != 1;        
    }
    
    protected void writeInteger(int integer, OutputStream output)
            throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(SIZEOF_INTEGER);
        bb.putInt(integer);
        output.write(bb.array());
    }

    protected int readInteger(InputStream input) throws IOException {
        byte[] in = new byte[SIZEOF_INTEGER];
        for (int ii = 0; ii < SIZEOF_INTEGER; ii++) {
            in[ii] = (byte) input.read();
        }
        ByteBuffer bb = ByteBuffer.wrap(in);
        return bb.getInt();
    }
}
