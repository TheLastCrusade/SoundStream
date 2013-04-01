/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lastcrusade.soundstream.net.message;

import static com.lastcrusade.soundstream.util.CustomAssert.assertSongMetaEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.soundstream.model.PlaylistEntry;
import com.lastcrusade.soundstream.model.SongMetadata;

public class PlaylistMessageTest extends SerializationTest<PlaylistMessage> {

    @Test
    public void testSerializePlaylistMessage() throws Exception {
        List<PlaylistEntry> songsToPlay = populateTestPlaylist();

        PlaylistMessage preSerializationPlaylistMessage = new PlaylistMessage(
                songsToPlay);
        PlaylistMessage postSerializationPlaylistMessage = super
                .testSerializeMessage(preSerializationPlaylistMessage);

        assertEquals(songsToPlay.size(), postSerializationPlaylistMessage.getSongsToPlay().size());
        for (int i = 0; i < songsToPlay.size(); i++) {
            assertSongMetaEquals(songsToPlay.get(i),
                    postSerializationPlaylistMessage.getSongsToPlay().get(i));
        }
    }

    public List<PlaylistEntry> populateTestPlaylist() {
        List<PlaylistEntry> library = new ArrayList<PlaylistEntry>(Arrays.asList(
                new PlaylistEntry(
                        new SongMetadata(69, "Driver that Had a Dick on His Shoulder",
                        "Aziz Ansari", "Dangerously Delicious", 2345, "David"),
                        true, true, "/some/such/path"),
                new PlaylistEntry(
                        new SongMetadata(1, "Lady with the Puppies", null,
                        "Dangerously Delicious", 23462346, "David"),
                        true, false, "/some/other/path"),
                new PlaylistEntry(new SongMetadata(23, "Toronto Customs Lady", "Aziz Ansari",
                        null, 3423462, "David"),
                        false, false, ""),
                new PlaylistEntry(new SongMetadata(42, "Motley Crue Tour vs. Aziz Tour", "Aziz Ansari",
                        "Dangerously Delicious", 2346236, null),
                        false, true, "./")
        ));
        return library;
    }
}
