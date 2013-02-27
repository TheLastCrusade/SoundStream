package com.lastcrusade.fanclub.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private List<SongMetadata> list;

    public Playlist() {
        list = new ArrayList<SongMetadata>();
    }

    public Playlist(SongMetadata meta) {
        list = new ArrayList<SongMetadata>();
        list.add(meta);
    }

    public void add(SongMetadata meta) {
        list.add(meta);
    }

    public void addToNext(SongMetadata meta) {
        list.add(1, meta);
    }

    public SongMetadata remove(SongMetadata meta) {
        SongMetadata removeMeta = null;
        int index = list.indexOf(meta);
        if (index >= 0) {
            removeMeta = list.remove(index);
        }
        return removeMeta;
    }

    public SongMetadata remove(int index) {
        SongMetadata removeMeta = null;
        if (index >= 0) {
            removeMeta = list.remove(index);
        }
        return removeMeta;
    }

    public List<SongMetadata> getList() {
        return list;
    }

    public void setList(List<SongMetadata> list) {
        this.list = list;
    }
}
