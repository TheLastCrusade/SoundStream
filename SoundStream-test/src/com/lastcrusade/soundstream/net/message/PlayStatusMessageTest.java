package com.lastcrusade.soundstream.net.message;

import static com.lastcrusade.soundstream.util.CustomAssert.assertSongMetaEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.lastcrusade.soundstream.model.SongMetadata;

public class PlayStatusMessageTest extends SerializationTest<PlayStatusMessage> {
	
    @Test
    public void testSerializePlayStatusMessage() throws IOException {
        PlayStatusMessage preSerializationPlayStatusMsg = new PlayStatusMessage();

        preSerializationPlayStatusMsg.setString(PlayStatusMessage.PLAY_MESSAGE);
        preSerializationPlayStatusMsg.setCurrentSong(getSomeSongMetadata());
        PlayStatusMessage postSerializationPlayStatusMsg = super.testSerializeMessage(preSerializationPlayStatusMsg);
		
        // Testing play with song
        assertEquals(PlayStatusMessage.PLAY_MESSAGE, postSerializationPlayStatusMsg.getString());
        assertSongMetaEquals(getSomeSongMetadata(), postSerializationPlayStatusMsg.getCurrentSong());

        // Testing pause with song
        preSerializationPlayStatusMsg.setString(PlayStatusMessage.PAUSE_MESSAGE);
	    preSerializationPlayStatusMsg.setCurrentSong(getSomeSongMetadata());
	    postSerializationPlayStatusMsg = super.testSerializeMessage(preSerializationPlayStatusMsg);
	    assertEquals(PlayStatusMessage.PAUSE_MESSAGE, postSerializationPlayStatusMsg.getString());
	    assertSongMetaEquals(getSomeSongMetadata(), postSerializationPlayStatusMsg.getCurrentSong());
	}
	
	private SongMetadata getSomeSongMetadata(){
	    return new SongMetadata(69, "Driver that Had a Dick on His Shoulder",
	            "Aziz Ansari", "Dangerously Delicious", 2345, "David");
	}
}
