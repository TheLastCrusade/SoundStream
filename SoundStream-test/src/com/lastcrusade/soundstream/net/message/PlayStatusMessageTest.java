package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class PlayStatusMessageTest extends SerializationTest<PlayStatusMessage> {
	
	@Test
	public void testSerializePlayStatusMessage() throws IOException {
		PlayStatusMessage preSerializationPlayStatusMsg = new PlayStatusMessage();
		preSerializationPlayStatusMsg.setString("Play");
		PlayStatusMessage postSerializationPlayStatusMsg = super.testSerializeMessage(preSerializationPlayStatusMsg);
		
		// PlayStatusMessage possibilities include Play and Pause
		assertEquals("Play", postSerializationPlayStatusMsg.getString());
		
		preSerializationPlayStatusMsg.setString("Pause");
		postSerializationPlayStatusMsg = super.testSerializeMessage(preSerializationPlayStatusMsg);
		assertEquals("Pause", postSerializationPlayStatusMsg.getString());
	}
	

}
