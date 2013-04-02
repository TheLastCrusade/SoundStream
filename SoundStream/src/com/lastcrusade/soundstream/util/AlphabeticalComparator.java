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

import java.util.Comparator;

import com.lastcrusade.soundstream.model.SongMetadata;

/**
 * 
 * Compares song metadata based on an alphabetical ordering.
 * 
 * Songs are ordered first by Artist, then by Album, and then
 * by Title
 *
 */
public class AlphabeticalComparator implements Comparator<SongMetadata> {

    @Override
    public int compare(SongMetadata songA, SongMetadata songB) {
        int artistComp = songA.getArtist().compareTo(songB.getArtist());
        int albumComp =  songA.getAlbum().compareTo(songB.getAlbum());
        int titleComp = songA.getTitle().compareTo(songB.getTitle());

        if(artistComp!=0){
            return artistComp;
        }
        
        if(albumComp!=0){
            return albumComp;
        }
        
        return titleComp;

    }
}
