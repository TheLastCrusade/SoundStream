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

package com.lastcrusade.soundstream.net.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple implementation of IMessage for sending strings back and forth.
 * 
 * @author Jesse Rosalia
 *
 */
public class StringMessage implements IMessage {
    private final String TAG = StringMessage.class.getName();
    private String string;

    @Override
    public void deserialize(InputStream input) throws IOException {
        byte[] bytes = new byte[1024];
        int read = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((read = input.read(bytes)) > 0) {
            out.write(bytes, 0, read);
        }
        this.setString(out.toString());
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        output.write(this.getString().getBytes());
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
