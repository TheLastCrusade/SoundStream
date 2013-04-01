package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.lastcrusade.soundstream.net.message.FindNewGuestsMessage;

public class FindNewGuestsMessageTest extends SerializationTest<FindNewGuestsMessage> {

    @Test
    public void testSerializeFindNewGuestsMessage() throws Exception {
        //NOTE: no fields to check, and the base class will ensure we create the right class.
        FindNewGuestsMessage newMessage = super.testSerializeMessage(new FindNewGuestsMessage());
    }
}
