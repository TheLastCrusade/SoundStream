package com.lastcrusade.soundstream.net.message;

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
