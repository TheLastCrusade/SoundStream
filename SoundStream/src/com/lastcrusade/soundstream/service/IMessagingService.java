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

package com.lastcrusade.soundstream.service;

import java.util.List;

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
    
    public void sendAddToPlaylistMessage(PlaylistEntry song);
    
    public void sendBumpSongOnPlaylistMessage(PlaylistEntry song);

    public void sendRemoveFromPlaylistMessage(PlaylistEntry song);

    public void sendPlayStatusMessage(PlaylistEntry currentSong, boolean isPlaying);

    public void sendPlaylistMessage(List<? extends PlaylistEntry> songsToPlay);

    public void sendSongStatusMessage(PlaylistEntry currentSong);

    public void sendRequestSongMessage(String address, long songId);

    public void sendTransferSongMessage(String address, long songId, String fileName, String filePath);

    public void sendUserListMessage(UserList userlist);
}
