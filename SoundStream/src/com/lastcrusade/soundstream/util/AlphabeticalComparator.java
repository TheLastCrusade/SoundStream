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
