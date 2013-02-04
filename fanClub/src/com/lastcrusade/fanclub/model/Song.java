package com.lastcrusade.fanclub.model;

public class Song {

    private long id;

    private String name;

    private String artist;

    private String album;

    private long size;

    private byte[] data;

    private boolean fullRecord = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
        this.fullRecord = true;
    }

    public boolean isFullRecord() {
        return fullRecord;
    }

    public void setFullRecord(boolean fullRecord) {
        this.fullRecord = fullRecord;
    }
}