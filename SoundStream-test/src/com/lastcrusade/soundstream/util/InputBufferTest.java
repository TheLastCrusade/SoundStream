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
package com.lastcrusade.soundstream.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

/**
 * @author thejenix
 *
 */
public class InputBufferTest {

    /**
     * Test method for {@link com.lastcrusade.soundstream.util.InputBuffer#getInputStream()}.
     * @throws IOException 
     */
    @Test
    public void testInputBuffer() throws IOException {
        InputBuffer buffer = new InputBuffer();
        buffer.write('a');
        buffer.write('b');
        buffer.write('c');
        assertEquals(3, buffer.size());
        assertEquals('a', buffer.getInputStream().read());
        buffer.consume();
        assertEquals(2, buffer.size());
        byte[] bytes = buffer.toByteArray();
        assertEquals('b', bytes[0]);
        assertEquals('c', bytes[1]);
        buffer.close();
    }
    
    /**
     * Test method for {@link com.lastcrusade.soundstream.util.InputBuffer#getInputStream()}.
     * @throws IOException 
     */
    @Test
    public void testInputBufferWithNegOnes() throws IOException {
        InputBuffer buffer = new InputBuffer();
        buffer.write('a');
        buffer.write(-1);
        buffer.write('c');
        assertEquals(3, buffer.size());
        assertEquals('a', buffer.getInputStream().read());
        buffer.consume();
        assertEquals(2, buffer.size());
        byte[] bytes = buffer.toByteArray();
        assertEquals(-1, bytes[0]);
        assertEquals('c', bytes[1]);
        buffer.close();
    }
}
