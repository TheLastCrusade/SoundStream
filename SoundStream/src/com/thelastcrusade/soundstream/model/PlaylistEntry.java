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

package com.thelastcrusade.soundstream.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.thelastcrusade.soundstream.util.BluetoothUtils;
import com.thelastcrusade.soundstream.util.DefaultParcelableCreator;
import com.thelastcrusade.soundstream.util.SongMetadataUtils;

public class PlaylistEntry extends SongMetadata {

    private boolean loaded   = false;
    private boolean played   = false;
    private String  filePath = null;
    //used to keep track of whether this song exists in the playlist multiple times
    private int entryId = 0;
    //required for Parcelable to work
    public static final Parcelable.Creator<PlaylistEntry> CREATOR = new DefaultParcelableCreator(PlaylistEntry.class);

    public PlaylistEntry() {
    }
    
    /**
     * Copy constructor, used to convert a SongMetadata into a
     * PlaylistEntry object when adding a song to the playlist.
     * 
     * @param metadata
     */
    public PlaylistEntry(SongMetadata metadata) {
        this.setAlbum(metadata.getAlbum());
        this.setArtist(metadata.getArtist());
        this.setId(metadata.getId());
        this.setMacAddress(metadata.getMacAddress());
        this.setFileSize(metadata.getFileSize());
        this.setTitle(metadata.getTitle());
    }
    
    public PlaylistEntry(SongMetadata metadata, boolean loaded, boolean played, String filePath, int entryId){
        this(metadata);
        this.loaded = loaded;
        this.played = played;
        this.filePath = filePath;
        this.entryId = entryId;
    }

    public PlaylistEntry(Parcel in){
        super(in);
        boolean[] state = new boolean[2];
        in.readBooleanArray(state);
        this.loaded = state[0];
        this.played = state[1];
        this.filePath = in.readString();
        this.entryId = in.readInt();
    }

    public boolean isLocalFile() {
        //TODO: this should not reference bluetoothutils...instead, we should look at the user list or some
        // other source of information 
        return BluetoothUtils.getLocalBluetoothMAC().equals(getMacAddress());
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isPlayed() {
        return played;
    }
    
    public void setPlayed(boolean played) {
        this.played = played;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
        this.setLoaded(filePath != null);
    }
    
    public void setEntryId(int entryId){
        this.entryId = entryId;
    }
    
    public int getEntryId(){
        return entryId;
    }
    
    @Override
    public int hashCode() {
        return SongMetadataUtils.getUniqueKey(this).hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlaylistEntry)) {
            return false;
        }
        return SongMetadataUtils.getUniqueKey(this).equals(SongMetadataUtils.getUniqueKey((PlaylistEntry)o));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        boolean [] state = new boolean[2];
        state[0] = this.loaded;
        state[1] = this.played;
        dest.writeBooleanArray(state);
        dest.writeString(this.filePath);
        dest.writeInt(entryId);
    }
}
