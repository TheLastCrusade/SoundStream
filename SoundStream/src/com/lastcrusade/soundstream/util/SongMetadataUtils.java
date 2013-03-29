package com.lastcrusade.soundstream.util;

import com.lastcrusade.soundstream.model.PlaylistEntry;
import com.lastcrusade.soundstream.model.SongMetadata;

public class SongMetadataUtils {

    /**
     * Create a unique key for this song.  This unique key consists of:
     *  Mac address (uniquely identifies a device)
     *  Song id (uniquely identifies a song on the device)
     * 
     * @param song
     * @return
     */
    public static String getUniqueKey(SongMetadata song) {
        return getUniqueKey(song.getMacAddress(), song.getId());
    }
    
    public static String getUniqueKey(String songSourceAddress, long songId) {
        return songSourceAddress.replace(":", "") + "_" + songId;
    }

    public static boolean isTheSameSong(SongMetadata lhs, SongMetadata rhs) {
        return lhs.getMacAddress().equals(rhs.getMacAddress()) &&
               lhs.getId() == rhs.getId();
    }
    
    public static String getUniqueKey(PlaylistEntry song) {
        return getUniqueKey(song.getMacAddress(), song.getId(), song.getCount());
    }
    
    public static String getUniqueKey(String songSourceAddress, long songId, int count) {
        return songSourceAddress.replace(":", "") + "_" + songId + "_" + count;
    }

}
