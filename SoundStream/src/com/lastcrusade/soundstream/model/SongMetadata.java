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

package com.lastcrusade.soundstream.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lastcrusade.soundstream.util.BluetoothUtils;
import com.lastcrusade.soundstream.util.DefaultParcelableCreator;

public class SongMetadata implements Parcelable{

    //this is REQUIRED for Parcelable to work properly
    public static final Parcelable.Creator<SongMetadata> CREATOR = new DefaultParcelableCreator(SongMetadata.class);

    public static final long UNKNOWN_SONG = -1;

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
