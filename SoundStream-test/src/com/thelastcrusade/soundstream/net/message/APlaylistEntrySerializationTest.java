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

package com.thelastcrusade.soundstream.net.message;

import static org.junit.Assert.assertEquals;

/**
 * Generic baseclass for APlaylistEntryMessage tests.
 * 
 * @author Jesse Rosalia
 *
 * @param <T>
 */
public class APlaylistEntrySerializationTest<T extends APlaylistEntryMessage>
    extends SerializationTest<T> {

    public T testSerializeMessage(T preSer) throws Exception {
        T postSer = super.testSerializeMessage(preSer);
        //check the standard information in APlaylistEntryMessage
        assertEquals(preSer.getMacAddress(), postSer.getMacAddress());
        assertEquals(preSer.getId(),         postSer.getId());
        //return the new message object
        return postSer;
    }
}
