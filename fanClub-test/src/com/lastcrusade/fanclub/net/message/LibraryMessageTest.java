package com.lastcrusade.fanclub.net.message;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.net.message.LibraryMessage;

public class LibraryMessageTest extends SerializationTest<LibraryMessage> {
	
	@Test
	public void testSerializeLibraryMessage() throws IOException {
		// library populated with some metadata of the last few songs I was
		// listening to at that time
		List<SongMetadata> library = new ArrayList<SongMetadata>(Arrays.asList(
				new SongMetadata(69, "Driver that Had a Dick on his Shoulder", 
						"Aziz Ansari", "Dangerously Delicious", "David"),
	            new SongMetadata(1, "Lady with the Puppies", 
	            		"Aziz Ansari", "Dangerously Delicious", "David"), 
	            new SongMetadata(23, "Toronto Customs Lady", 
	            		"Aziz Ansari", "Dangerously Delicious", "David")));		
		
		LibraryMessage oldMessage = new LibraryMessage(library);
		LibraryMessage newMessage = super.testSerializeMessage(oldMessage);
		
		for(int i = 0; i < library.size(); i++) {
			assertEquals(library.get(i).getId() , newMessage.getLibrary().get(i).getId());
			assertEquals(library.get(i).getTitle() , newMessage.getLibrary().get(i).getTitle());
			assertEquals(library.get(i).getArtist() , newMessage.getLibrary().get(i).getArtist());
			assertEquals(library.get(i).getAlbum() , newMessage.getLibrary().get(i).getAlbum());
			assertEquals(library.get(i).getUsername() , newMessage.getLibrary().get(i).getUsername());
		}
	}
}