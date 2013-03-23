package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.net.message.LibraryMessage;

public class LibraryMessageTest extends SerializationTest<LibraryMessage> {
	
	@Test
	public void testSerializeLibraryMessage() throws IOException {
		List<SongMetadata> library = populateTestLibrary();
		
		LibraryMessage preSerializationLibraryMessage = new LibraryMessage(library);
		LibraryMessage postSerializationLibraryMessage = super.testSerializeMessage(preSerializationLibraryMessage);
		
		for(int i = 0; i < library.size(); i++) {
			SongMetadata preSerializationSongMetadata = library.get(i);
			SongMetadata postSerializationSongMetadata = postSerializationLibraryMessage.getLibrary().get(i);
			
			assertEquals(preSerializationSongMetadata.getId(), postSerializationSongMetadata.getId());
			assertEquals(preSerializationSongMetadata.getTitle(), postSerializationSongMetadata.getTitle());
			assertEquals(preSerializationSongMetadata.getArtist(), postSerializationSongMetadata.getArtist());
			assertEquals(preSerializationSongMetadata.getAlbum(), postSerializationSongMetadata.getAlbum());
			assertEquals(preSerializationSongMetadata.getMacAddress(), postSerializationSongMetadata.getMacAddress());
		}
	}

	public List<SongMetadata> populateTestLibrary() {
        List<SongMetadata> library = new ArrayList<SongMetadata>(Arrays.asList(
                new SongMetadata(69, "Driver that Had a Dick on His Shoulder",
                        "Aziz Ansari", "Dangerously Delicious", 2345, "David"),
                new SongMetadata(1, "Lady with the Puppies", null,
                        "Dangerously Delicious", 23462346, "David"),
                new SongMetadata(23, "Toronto Customs Lady", "Aziz Ansari",
                        null, 3423462, "David"), 
                new SongMetadata(42, "Motley Crue Tour vs. Aziz Tour", "Aziz Ansari",
                        "Dangerously Delicious", 2346236, null)));
        return library;
    }
}