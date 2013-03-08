package com.lastcrusade.fanclub.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private List<SongMetadata> unPlayed;
    private List<SongMetadata> played;

    public Playlist() {
        unPlayed = new ArrayList<SongMetadata>();
        played = new ArrayList<SongMetadata>();
    }

    public Playlist(SongMetadata meta) {
        unPlayed = new ArrayList<SongMetadata>();
        played = new ArrayList<SongMetadata>();
        unPlayed.add(meta);
    }
    
    public Playlist(List<SongMetadata> unPlayed, List<SongMetadata> played){
        this.unPlayed = unPlayed;
        this.played = played;
    }

    public void add(SongMetadata meta) {
        unPlayed.add(meta);
    }

    public void addToNext(SongMetadata meta) {
        unPlayed.add(1, meta);
    }

    public SongMetadata remove(SongMetadata meta) {
        SongMetadata removeMeta = null;
        int index = unPlayed.indexOf(meta);
        if (index >= 0) {
            removeMeta = unPlayed.remove(index);
        }
        return removeMeta;
    }

    public SongMetadata remove(int index) {
        SongMetadata removeMeta = null;
        if (index >= 0) {
            removeMeta = unPlayed.remove(index);
        }
        return removeMeta;
    }

    public List<SongMetadata> getUnPlayedList() {
        return unPlayed;
    }
    
    public List<SongMetadata> getPlayedList(){
        return played;
    }

    public void setUnPlayedList(List<SongMetadata> list) {
        this.unPlayed = list;
    }
    
    public void setPlaylist(List<SongMetadata> unPlayed, List<SongMetadata> played){
        this.unPlayed = unPlayed;
        this.played = played;
    }
    
    public int size(){
        return unPlayed.size();
    }
    
    public SongMetadata getHead(){
        if(unPlayed.size() > 0){
            return unPlayed.get(0);
        } else {
            return null;
        }
    }

    /**
     * Call this method to progress the playlist
     * @return new top of unplayed list
     */
    public SongMetadata moveNext(){
        if(unPlayed.size() > 0){
            //Add top of the unPlayed list to played list
            played.add(unPlayed.get(0));
            //Remove the top of unplayed list
            unPlayed.remove(0);
        }
        if(unPlayed.size() > 0){
            //return the new top of the unplayed list
            return unPlayed.get(0);
        } else {
            return null;
        }
    }
}
