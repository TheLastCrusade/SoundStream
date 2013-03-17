package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.soundstream.model.Playlist;
import com.lastcrusade.soundstream.model.SongMetadata;
import static com.lastcrusade.soundstream.util.CustomAssert.*;

public class PlaylistMessageTest extends SerializationTest<PlaylistMessage> {
		
	@Test
	public void testSerializePlaylistMessage() throws IOException {
		Playlist playlist = populateTestPlaylist();
		
		PlaylistMessage preSerializationPlaylistMessage = new PlaylistMessage(playlist);
		PlaylistMessage postSerializationPlaylistMessage = 
				super.testSerializeMessage(preSerializationPlaylistMessage);

		for(int i = 0; i < playlist.size(); i++) {
		    assertSongMetaEquals(playlist.getSong(i), postSerializationPlaylistMessage.getPlaylist().getSong(i));
		}
	}
	
	public Playlist populateTestPlaylist() {
	    List<SongMetadata> library = new ArrayList<SongMetadata>(Arrays.asList(
                new SongMetadata(69, "Driver that Had a Dick on His Shoulder", 
                        "Aziz Ansari", "Dangerously Delicious", "David"),
                new SongMetadata(1, "Lady with the Puppies", null, 
                        "Dangerously Delicious", "David"), 
                new SongMetadata(23, "Toronto Customs Lady", "Aziz Ansari", null,
                        "David")));     
                new SongMetadata(42, "Motley Crue Tour vs. Aziz Tour", 
                        "Aziz Ansari", "Dangerously Delicious", null);
        return new Playlist(library);
	}
}
