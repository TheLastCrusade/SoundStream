package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.lastcrusade.soundstream.net.message.FindNewFansMessage;

public class FindNewFansMessageTest extends SerializationTest<FindNewFansMessage> {

    @Test
    public void testSerializeFindNewFansMessage() throws IOException {
        //NOTE: no fields to check, and the base class will ensure we create the right class.
        FindNewFansMessage newMessage = super.testSerializeMessage(new FindNewFansMessage());
    }
}
