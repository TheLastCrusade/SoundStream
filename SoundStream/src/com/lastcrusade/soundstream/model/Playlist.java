package com.lastcrusade.soundstream.model;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.lastcrusade.soundstream.util.SongMetadataUtils;

/**
 * A data structure for holding the playlist.  It keeps track of two queues of PlaylistEntry
 * objects; the seam between them represents the current play position.
 * 
 * @author Jesse Rosalia
 *
 */
public class Playlist {
    
    private final static String TAG = Playlist.class.getName();
    
    private Deque<PlaylistEntry> playedList;
    private Deque<PlaylistEntry> musicList;

    public Playlist() {
        playedList = new LinkedList<PlaylistEntry>();
        musicList  = new LinkedList<PlaylistEntry>();
    }

    public void add(PlaylistEntry entry) {
        musicList.add(entry);
    }

    public void clear() {
        playedList.clear();
        musicList.clear();
    }

    public PlaylistEntry findEntryForSong(PlaylistEntry song) {
        PlaylistEntry found = null;
        for (PlaylistEntry entry : getSongsToPlay()) {
            if (SongMetadataUtils.isTheSameEntry(entry, song)) {
                found = entry;
                break;
            }
        }

        return found;
    }
    
    public PlaylistEntry findEntryByAddressIDandCount(String macAddress, long id, int count){
        PlaylistEntry found = null;
        for(PlaylistEntry entry: getSongsToPlay()){
            if(entry.getMacAddress().equals(macAddress) && entry.getId() == id && entry.getCount() == count){
                found = entry;
                break;
            }
        }
        return found;
    }
    
    /**
     *Counts the number of times the given song occurs in the playlist
     *
     * @param song
     * @return
     */
    public int countSongOccurence(PlaylistEntry song){
        PlaylistEntry found = null;
        for(PlaylistEntry entry: getSongsToPlay()){
            if(SongMetadataUtils.isTheSameSongByTitle(entry, song)){
                found = entry;
            }
        }
        if(found != null){
            return found.getCount();
        }
        else{
            return 0;
        }
    }

    public PlaylistEntry remove(PlaylistEntry entry) {
        boolean success;
        PlaylistEntry removeEntry = entry;

        if(musicList.contains(entry)) {
            success = musicList.remove(entry);
        } else if(playedList.contains(entry)){
            success = playedList.remove(entry);
        } else {
            success = false; 
            Log.wtf(TAG, "Asked to remove unknown object");
        }

        if(!success){
            removeEntry = null;
        }
        return removeEntry;
    }

    public List<PlaylistEntry> getSongsToPlay() {
        List<PlaylistEntry> songsToPlay = new ArrayList<PlaylistEntry>();
        songsToPlay.addAll(playedList);
        songsToPlay.addAll(musicList);
        return songsToPlay;
    }

    public int size(){
        return playedList.size() + musicList.size();
    }

    public PlaylistEntry getNextAvailableSong() {
        PlaylistEntry nextAvail = null;
        for (PlaylistEntry entry : musicList) {
            if (entry.isLoaded()) {
                nextAvail = entry;
                break;
            }
        }
        
        if (nextAvail != null) {
            musicList.remove(nextAvail);
            playedList.add(nextAvail);
        }
        return nextAvail;
    }

    public void reset() {
        playedList.addAll(musicList);
        musicList = playedList;
        playedList = new LinkedList<PlaylistEntry>();
        //reset the play status on all of the entries
        for (PlaylistEntry entry : musicList) {
            entry.setPlayed(false);
        }
    }
    
    public void bumpSong(PlaylistEntry entry){
        if(musicList.contains(entry)){
            //remove the entry from the queue
            musicList.remove(entry);
            musicList.push(entry);
        }
    }
}
