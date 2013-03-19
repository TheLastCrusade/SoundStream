package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.soundstream.net.message.ConnectGuestsMessage;

public class ConnectGuestsMessageTest extends SerializationTest<ConnectGuestsMessage> {

    @Test
    public void testSerializeFoundFansMessage() throws IOException {
        //NOTE: no fields to check, and the base class will ensure we create the right class.
        List<String> addresses = new ArrayList<String>();
        addresses.add("00:11:22:33:44:55:66");
        addresses.add("00:11:22:33:44:55:67");
        addresses.add("00:11:22:33:44:55:68");
        addresses.add("00:11:22:33:44:55:69");

        ConnectGuestsMessage oldMessage = new ConnectGuestsMessage(addresses);
        ConnectGuestsMessage newMessage = super.testSerializeMessage(oldMessage);
        //verify that the FoundFan objects are equal and in the same order.
        for (int ii = 0; ii < addresses.size(); ii++) {
            String expected = addresses.get(ii);
            String actual   = newMessage.getAddresses().get(ii);
            assertEquals(expected, actual);
        }

    }
}
