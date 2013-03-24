package com.lastcrusade.soundstream.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private final static String TAG = Playlist.class.getName();
    private List<SongMetadata> musicList;
    private int index;

    public Playlist() {
        musicList = new ArrayList<SongMetadata>();
        index = 0;
    }

    public Playlist(List<SongMetadata> aMusicList){
        musicList = aMusicList;
        index = 0;
    }

    public void add(SongMetadata meta) {
        musicList.add(meta);
    }

    public SongMetadata remove(int index) {
        SongMetadata removeMeta = null;
        if (index >= 0) {
            removeMeta = musicList.remove(index);
        }
        return removeMeta;
    }

    public List<SongMetadata> getSongsToPlay(){
        return musicList;
    }

    public int size(){
        return musicList.size();
    }

    public int getIndex(){
        return index;
    }

    public SongMetadata getNextSong(){
        index = index % musicList.size();
        return musicList.get(index);
    }

    /**
     * Call this method to progress the playlist
     */
    public void moveNext(){
            index++;
    }
    
    public SongMetadata getSong(int index){
        return musicList.get(index);
    }
}
