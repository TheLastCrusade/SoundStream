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
}
