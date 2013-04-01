package com.lastcrusade.soundstream.net.message;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AddToPlaylistMessageTest.class,
        BumpSongOnPlaylistMessageTest.class, ConnectGuestsMessageTest.class,
        FindNewGuestsMessageTest.class, FoundGuestsMessageTest.class,
        LibraryMessageTest.class, MessengerTest.class, PauseMessageTest.class,
        PlaylistMessageTest.class, PlayMessageTest.class,
        PlayStatusMessageTest.class, RemoveFromPlaylistMessageTest.class,
        RequestSongMessageTest.class, SkipMessageTest.class,
        SongStatusMessageTest.class, TransferSongMessageTest.class,
        UserListMessageTest.class })
public class AllMessageTests {

}
