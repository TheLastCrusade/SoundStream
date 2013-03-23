package com.lastcrusade.soundstream.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lastcrusade.soundstream.util.BluetoothUtils;
import com.lastcrusade.soundstream.util.DefaultParcelableCreator;

public class SongMetadata implements Parcelable{

    //this is REQUIRED for Parcelable to work properly
    public static final Parcelable.Creator<SongMetadata> CREATOR = new DefaultParcelableCreator(SongMetadata.class);

    private long id;

    private String title;

    private String artist;

    private String album;

    private long fileSize;

    // macAddress of person who added song to playlist
    private String macAddress; 
    
    public SongMetadata() {}

    public SongMetadata(long aId, String aTitle, String aArtist, String aAlbum, long aFileSize, String aMacAddress) {
    	//be careful setting the song ID
        this.id = aId;
    	this.title = aTitle;
    	this.artist = aArtist;
    	this.album = aAlbum;
    	this.fileSize = aFileSize;
    	this.macAddress = aMacAddress;
    }

    public SongMetadata(Parcel in) {
        this.id         = in.readLong();
        this.title      = in.readString();
        this.artist     = in.readString();
        this.album      = in.readString();
        this.fileSize   = in.readLong();
        this.macAddress = in.readString();
    }

    @Override
    public int describeContents() {
        return 0; //default, no special kind of objects
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeString(this.album);
        dest.writeLong(this.fileSize);
        dest.writeString(this.macAddress);
    }
    
    @Override
    public String toString() {
        return String.format("%s by %s on %s from %s", this.title, this.artist, this.album, this.macAddress);
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }    
}
