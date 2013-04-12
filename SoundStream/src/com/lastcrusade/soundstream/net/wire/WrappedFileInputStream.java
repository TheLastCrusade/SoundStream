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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author thejenix
 *
 */
public class WrappedFileInputStream extends InputStream {

    private FileInputStream fis;
    private ByteArrayInputStream lengthStream;

    /**
     * @throws IOException 
     * 
     */
    public WrappedFileInputStream(FileInputStream fis) throws IOException {
        this.fis = fis;
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(fis.available());
        this.lengthStream = new ByteArrayInputStream(bb.array());
    }

    @Override
    public int available() throws IOException {
        return 4 + fis.available();
    }
    @Override
    public int read() throws IOException {
        int read = -1;
        if (lengthStream.available() > 0) {
            read = lengthStream.read();
        } else {
            read = this.fis.read();
        }
        return read;
    }
}
