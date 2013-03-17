package com.lastcrusade.soundstream.service;

import java.util.List;

import com.lastcrusade.soundstream.model.Playlist;
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
     * Send the library to all currently connected fans.
     * 
     * @param library
     */
    public void sendLibraryMessageToFans(List<SongMetadata> library);
    
    /**
     * Send a find new fans message to the host.
     * 
     */
    public void sendFindNewFansMessage();
    
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
     * Send a string message to all connected devices (host or fan).
     * 
     * @param message
     */
    public void sendStringMessage(String message);

    public void sendPlaylistMessage(Playlist playlist);

    public void sendPlayStatusMessage(String playStatusMessage);

    public void sendUserListMessage(UserList userlist);
}
