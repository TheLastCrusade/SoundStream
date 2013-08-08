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
package com.thelastcrusade.soundstream.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents an input buffer, which will collect bytes from a network
 * or another process and hold them for further processing.  To access the bytes,
 * one can use the underlying ByteArrayOutputStream functions, or get an input
 * stream that exposes the underlying byte array as a stream object.  This also
 * allows the caller to remove bytes consumed through the input stream, to keep
 * memory low and code clean.
 * 
 * @author Jesse Rosalia
 *
 */
public class InputBuffer extends ByteArrayOutputStream {

    private int index = 0;
    
    private class InputBufferInputStream extends InputStream {

        @Override
        public int available() throws IOException {
            return size() - index;
        }

        @Override
        public int read(byte[] buffer, int offset, int length)
                throws IOException {
            if (buffer == null) {
                throw new NullPointerException();
            } else if (offset < 0 || length < 0 || length > buffer.length - offset) {
                throw new IndexOutOfBoundsException();
            } else if (length == 0) {
                return 0;
            }

            if (available() <= 0) {
                return -1;
            }
            int toCopy = Math.min(length, available());
            System.arraycopy(buf, index, buffer, offset, toCopy);
            index += toCopy;
            return toCopy;
        }
        @Override
        public int read() throws IOException {
            return index < size() ? buf[index++] : -1;
        }
    }
    
    /**
     * Get an input stream to read in the underlying data.
     * 
     * This input stream will reset the internal index
     * pointer to 0, to start at the beginning of the
     * byte array.
     */
    public InputStream getInputStream() {
        index = 0;
        return new InputBufferInputStream();
    }

    /**
     * 
     */
    public void consume() {
        //consume all bytes read in using the input stream
        byte[] save = super.buf;
        int len = super.count;
        super.reset();
        write(save, index, len - index);
        index = 0;
    }
}
