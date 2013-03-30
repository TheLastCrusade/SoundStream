package com.lastcrusade.soundstream.service;

import java.util.List;

import com.lastcrusade.soundstream.model.Playlist;
import com.lastcrusade.soundstream.model.PlaylistEntry;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.model.UserList;

public interface IMessagingService {

    /**
     * Send the library to the currently connected host.
     * 
     * @param library
     */
    public void sendLibraryMessageToHost(List<SongMetadata> library);
    
    /**
     * Send the library to all currently connected guests.
     * 
     * @param library
     */
    public void sendLibraryMessageToGuests(List<SongMetadata> library);
    
    /**
     * Send a pause message to the host.
     * 
     */
    public void sendPauseMessage();
    
    /**
     * Send a play message to the host.
     * 
     */
    public void sendPlayMessage();
    
    /**
     * Send a skip message to the host.
     * 
     */
    public void sendSkipMessage();
    
    /**
     * FOR TESTING
     * 
     * Send a string message to all connected devices (host or guests).
     * 
     * @param message
     */
    public void sendStringMessage(String message);

    public void sendAddToPlaylistMessage(SongMetadata song);
    
    public void sendBumpSongOnPlaylistMessage(SongMetadata song);

    public void sendRemoveFromPlaylistMessage(SongMetadata song);

    public void sendPlaylistMessage(List<? extends PlaylistEntry> songsToPlay);

    public void sendSongStatusMessage(PlaylistEntry currentSong);

    public void sendPlayStatusMessage(String playStatusMessage);

    public void sendPlayStatusMessage(String playStatusMessage, SongMetadata currentSong);

    public void sendRequestSongMessage(String address, long songId);

    public void sendTransferSongMessage(String address, long songId, String fileName, String filePath);

    public void sendUserListMessage(UserList userlist);
}
