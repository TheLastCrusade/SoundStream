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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.soundstream.net.message.FoundGuest;
import com.lastcrusade.soundstream.net.message.FoundGuestsMessage;

public class FoundGuestsMessageTest extends SerializationTest<FoundGuestsMessage> {

    @Test
    public void testSerializeFoundGuestsMessage() throws Exception {
        //NOTE: no fields to check, and the base class will ensure we create the right class.
        List<FoundGuest> foundGuests = new ArrayList<FoundGuest>();
        foundGuests.add(new FoundGuest("Test1", "00:11:22:33:44:55:66", true));
        foundGuests.add(new FoundGuest("Test2", "00:11:22:33:44:55:67", true));
        foundGuests.add(new FoundGuest("Test3", "00:11:22:33:44:55:68", false));
        foundGuests.add(new FoundGuest("Test4", "00:11:22:33:44:55:69", false));

        FoundGuestsMessage oldMessage = new FoundGuestsMessage(foundGuests);
        FoundGuestsMessage newMessage = super.testSerializeMessage(oldMessage);
        //verify that the FoundGuests objects are equal and in the same order.
        for (int ii = 0; ii < foundGuests.size(); ii++) {
            FoundGuest expected = foundGuests.get(ii);
            FoundGuest actual   = newMessage.getFoundGuests().get(ii);
            assertEquals(expected, actual);
        }
    }
}
