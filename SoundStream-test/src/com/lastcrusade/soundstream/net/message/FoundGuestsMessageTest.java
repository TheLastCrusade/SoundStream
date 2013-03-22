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
    public void testSerializeFoundGuestsMessage() throws IOException {
        //NOTE: no fields to check, and the base class will ensure we create the right class.
        List<FoundGuest> foundFans = new ArrayList<FoundGuest>();
        foundFans.add(new FoundGuest("Test1", "00:11:22:33:44:55:66"));
        foundFans.add(new FoundGuest("Test2", "00:11:22:33:44:55:67"));
        foundFans.add(new FoundGuest("Test3", "00:11:22:33:44:55:68"));
        foundFans.add(new FoundGuest("Test4", "00:11:22:33:44:55:69"));

        FoundGuestsMessage oldMessage = new FoundGuestsMessage(foundFans);
        FoundGuestsMessage newMessage = super.testSerializeMessage(oldMessage);
        //verify that the FoundFan objects are equal and in the same order.
        for (int ii = 0; ii < foundFans.size(); ii++) {
            FoundGuest expected = foundFans.get(ii);
            FoundGuest actual   = newMessage.getFoundGuests().get(ii);
            assertEquals(expected, actual);
        }
    }
}
