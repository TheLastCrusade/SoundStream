package com.lastcrusade.fanclub.net.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.fanclub.PlaylistFragment;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.net.message.LibraryMessage;

//import com.lastcrusade.fanclub.service.MusicLibraryService;

public class LibraryMessageTest extends SerializationTest<LibraryMessage> {
	
	static List<SongMetadata> metadataList = new ArrayList<SongMetadata>(
            Arrays.asList(new SongMetadata(), new SongMetadata(), new SongMetadata()));
	
	@Test
	public void testSerializeLibraryMessage() throws IOException {
		LibraryMessage oldMessage = new LibraryMessage();
		oldMessage.setLibrary(metadataList);
		LibraryMessage newMessage = super.testSerializeMessage(oldMessage);
				
		assertEquals(metadataList , newMessage.getLibrary());
	}
}