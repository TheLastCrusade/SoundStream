package com.lastcrusade.soundstream.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Playlist {
    private final static String TAG = Playlist.class.getName();
    private Queue<PlaylistEntry> playedList;
    private Queue<PlaylistEntry> musicList;
    private int index;

    public Playlist() {
        playedList = new LinkedList<PlaylistEntry>();
        musicList  = new LinkedList<PlaylistEntry>();
        index = 0;
    }

    public void add(PlaylistEntry entry) {
        musicList.add(entry);
    }

    public void clear() {
        musicList.clear();
        index = 0;
    }

    public SongMetadata remove(SongMetadata meta) {
        SongMetadata removeMeta = meta;
        boolean removed = musicList.remove(meta);
        if (!removed) {
            removeMeta = null;
        }
        return removeMeta;
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
}
