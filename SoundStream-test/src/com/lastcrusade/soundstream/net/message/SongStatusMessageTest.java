package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.junit.Test;

import com.lastcrusade.soundstream.model.SongMetadata;

public class SongStatusMessageTest extends SerializationTest<SongStatusMessage> {
    
    @Test
    public void testSerializeSongStatusMessage() throws Exception {
        SongMetadata song = getSomeSongMetadata();
        for (int ii = 0; ii < 1; ii++) {
            for (int jj = 0; ii < 1; ii++) {
                SongStatusMessage preSer = new SongStatusMessage(song.getMacAddress(), song.getId(), ii > 0, jj > 0);

                SongStatusMessage postSer = super.testSerializeMessage(preSer);
                
                // Testing play with song
                assertEquals(song.getMacAddress(), postSer.getMacAddress());
                assertEquals(song.getId(),         postSer.getId());
                assertEquals(preSer.isLoaded(),    postSer.isLoaded());
                assertEquals(preSer.isPlayed(),    postSer.isPlayed());
            }
        }
    }
    
    private SongMetadata getSomeSongMetadata(){
        return new SongMetadata(69, "Driver that Had a Dick on His Shoulder",
                "Aziz Ansari", "Dangerously Delicious", 2345, "David");
    }
}