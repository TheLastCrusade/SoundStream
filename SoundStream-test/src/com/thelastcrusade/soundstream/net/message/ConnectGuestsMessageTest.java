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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ConnectGuestsMessageTest extends SerializationTest<ConnectGuestsMessage> {

    @Test
    public void testSerializeFoundGuestsMessage() throws Exception {
        //NOTE: no fields to check, and the base class will ensure we create the right class.
        List<String> addresses = new ArrayList<String>();
        addresses.add("00:11:22:33:44:55:66");
        addresses.add("00:11:22:33:44:55:67");
        addresses.add("00:11:22:33:44:55:68");
        addresses.add("00:11:22:33:44:55:69");

        ConnectGuestsMessage oldMessage = new ConnectGuestsMessage(addresses);
        ConnectGuestsMessage newMessage = super.testSerializeMessage(oldMessage);
        //verify that the FoundGuest objects are equal and in the same order.
        for (int ii = 0; ii < addresses.size(); ii++) {
            String expected = addresses.get(ii);
            String actual   = newMessage.getAddresses().get(ii);
            assertEquals(expected, actual);
        }

    }
}
