package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class PlayStatusMessageTest extends SerializationTest<PlayStatusMessage> {
	
	@Test
	public void testSerializePlayStatusMessage() throws IOException {
		PlayStatusMessage preSerializationPlayStatusMsg = new PlayStatusMessage();
		preSerializationPlayStatusMsg.setString(PlayStatusMessage.PLAY_MESSAGE);
		PlayStatusMessage postSerializationPlayStatusMsg = super.testSerializeMessage(preSerializationPlayStatusMsg);
		
		// PlayStatusMessage possibilities include Play and Pause
		assertEquals(PlayStatusMessage.PLAY_MESSAGE, postSerializationPlayStatusMsg.getString());
		
		preSerializationPlayStatusMsg.setString(PlayStatusMessage.PAUSE_MESSAGE);
		postSerializationPlayStatusMsg = super.testSerializeMessage(preSerializationPlayStatusMsg);
		assertEquals(PlayStatusMessage.PAUSE_MESSAGE, postSerializationPlayStatusMsg.getString());
	}
	

}
