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
package com.lastcrusade.soundstream.net.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lastcrusade.soundstream.net.wire.MessageNotCompleteException;

/**
 * @author thejenix
 *
 */
public interface ISerializable {

    /**
     * Deserialize a message that has been received in an input stream.  This will be
     * called if a message of this type is received.
     * 
     * NOTE: it can be assumed that this input stream has all required data and therefore
     * will not block.  The framework will ensure this.
     * 
     * @param input
     * @throws IOException 
     * @throws MessageNotCompleteException 
     */
    public void deserialize(InputStream  input) throws IOException, MessageNotCompleteException;

    public void serialize(  OutputStream output) throws IOException;

}
