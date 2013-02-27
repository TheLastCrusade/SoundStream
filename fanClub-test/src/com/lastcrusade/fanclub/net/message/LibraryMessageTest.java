package com.lastcrusade.fanclub.net.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Test;

import com.lastcrusade.fanclub.PlaylistFragment;
import com.lastcrusade.fanclub.net.message.LibraryMessage;

//import com.lastcrusade.fanclub.service.MusicLibraryService;

public class LibraryMessageTest extends SerializationTest<LibraryMessage>{
	
	@Test
	public void testSerializeLibraryMessage() throws IOException {
		LibraryMessage oldMessage = new LibraryMessage();
		LibraryMessage newMessage = super.testSerializeMessage(oldMessage);
		
		//assertEquals( , newMessage.getLibraryString());
	}

}
