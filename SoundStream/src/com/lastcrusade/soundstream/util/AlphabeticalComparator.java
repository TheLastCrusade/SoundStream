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

import android.util.Log;

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

        // not the most efficient way of doing things, but it works for now
        // if the first character of songA's artist is a special character,
        // and the first character of songB's artist is a normal letter, songA is ordered
        // after songB
        if(!songA.getArtist().substring(0, 1).matches("[a-zA-Z]") && songB.getArtist().substring(0, 1).matches("[a-zA-Z]")){
            artistComp = 1;
        }
        //handles the opposite - A starts with a normal letter and B starts with a special character
        else if(songA.getArtist().substring(0, 1).matches("[a-zA-Z]") && !songB.getArtist().substring(0, 1).matches("[a-zA-Z]")){
            artistComp = -1;
        }

        if(artistComp!=0){
            return artistComp;
        }
        
        //does the same thing with albums
        if(!songA.getAlbum().substring(0, 1).matches("[a-zA-Z]") && songB.getAlbum().substring(0, 1).matches("[a-zA-Z]")){
            albumComp = 1;
        }
        else if(songA.getAlbum().substring(0, 1).matches("[a-zA-Z]") && !songB.getAlbum().substring(0, 1).matches("[a-zA-Z]")){
            albumComp = -1;
        }
        
        if(albumComp!=0){
            return albumComp;
        }
        
        //does the same thing with the title
        if(!songA.getTitle().substring(0, 1).matches("[a-zA-Z]") && songB.getTitle().substring(0, 1).matches("[a-zA-Z]")){
            titleComp = 1;
        }
        else if(songA.getTitle().substring(0, 1).matches("[a-zA-Z]") && !songB.getTitle().substring(0, 1).matches("[a-zA-Z]")){
            titleComp = -1;
        }
        
        return titleComp;

    }
}
