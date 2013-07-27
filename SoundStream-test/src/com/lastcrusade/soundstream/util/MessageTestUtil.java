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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author thejenix
 *
 */
public class MessageTestUtil {

    /**
     * Get a test stream, which can be used as a message.
     * 
     * This will write the byte number (0 based) to each byte
     * in the file, for easy verification.
     * 
     * @return
     */
    public static InputStream getTestStream(int byteCount) {
        @SuppressWarnings("resource")
		InputBuffer buffer = new InputBuffer();
        for (int ii = 0; ii < byteCount; ii++) {
            buffer.write(ii);
        }
        return buffer.getInputStream();
    }


    /**
     * Get a temporary file of a specified size.  The data
     * will be n number of the letter "a" (without quotes).
     * 
     * NOTE: even though this is a temporary file, be a good
     * citizen and delete your files when youre done!
     * 
     * @return
     * @throws IOException 
     */
    public static File getTempTestFile(int size) throws IOException {
        File file = File.createTempFile("test", ".tst");
        FileWriter writer = new FileWriter(file);
        for (int ii = 0; ii < size; ii++) {
            writer.append("a");
        }
        writer.close();
        return file;
    }
}
