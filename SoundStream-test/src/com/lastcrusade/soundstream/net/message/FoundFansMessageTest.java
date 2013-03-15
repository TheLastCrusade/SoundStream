package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.soundstream.net.message.FoundFan;
import com.lastcrusade.soundstream.net.message.FoundFansMessage;

public class FoundFansMessageTest extends SerializationTest<FoundFansMessage> {

    @Test
    public void testSerializeFoundFansMessage() throws IOException {
        //NOTE: no fields to check, and the base class will ensure we create the right class.
        List<FoundFan> foundFans = new ArrayList<FoundFan>();
        foundFans.add(new FoundFan("Test1", "00:11:22:33:44:55:66"));
        foundFans.add(new FoundFan("Test2", "00:11:22:33:44:55:67"));
        foundFans.add(new FoundFan("Test3", "00:11:22:33:44:55:68"));
        foundFans.add(new FoundFan("Test4", "00:11:22:33:44:55:69"));

        FoundFansMessage oldMessage = new FoundFansMessage(foundFans);
        FoundFansMessage newMessage = super.testSerializeMessage(oldMessage);
        //verify that the FoundFan objects are equal and in the same order.
        for (int ii = 0; ii < foundFans.size(); ii++) {
            FoundFan expected = foundFans.get(ii);
            FoundFan actual   = newMessage.getFoundFans().get(ii);
            assertEquals(expected, actual);
        }
    }
}
