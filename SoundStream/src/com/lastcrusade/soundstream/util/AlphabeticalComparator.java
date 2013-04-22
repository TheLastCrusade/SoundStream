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
        
        //compare the songs by artist - if they are the same, check album
        int artistComp = compareStringsWithSpecialChars(songA.getArtist(), songB.getArtist());
        if(artistComp!=0){
            return artistComp;
        }
        
        //compare the songs by album - if they are the same, check title
        int albumComp  = compareStringsWithSpecialChars(songA.getAlbum(), songB.getAlbum());
        if(albumComp!=0){
            return albumComp;
        }
        
        //compare the songs by title
        int titleComp  = compareStringsWithSpecialChars(songA.getTitle(), songB.getTitle());
        return titleComp;

    }
    
    private int compareStringsWithSpecialChars(String a, String b){
        int comp = a.compareTo(b);
        
        //only check for special characters if they are not the same
        if(comp != 0){
            // doing it this way instead of just passing in the first character
            // in case we want to handle more than just the first character
            String firstA = a.substring(0, 1);
            String firstB = b.substring(0, 1);
            
            if(!firstA.matches("[a-zA-Z]") && firstB.matches("[a-zA-Z]")){
                comp = 1;
            }
            else if(firstA.matches("[a-zA-Z]") && !firstB.matches("[a-zA-Z]")){
                comp = -1;
            }
        }
        
        return comp;
    }
}
