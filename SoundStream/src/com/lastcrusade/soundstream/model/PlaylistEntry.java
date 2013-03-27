package com.lastcrusade.soundstream.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lastcrusade.soundstream.util.BluetoothUtils;
import com.lastcrusade.soundstream.util.DefaultParcelableCreator;
import com.lastcrusade.soundstream.util.SongMetadataUtils;

public class PlaylistEntry extends SongMetadata {

    private boolean loaded   = false;
    private boolean played   = false;
    private String  filePath = null;

    //required for Parcelable to work
    public static final Parcelable.Creator<PlaylistEntry> CREATOR = new Parcelable.Creator<PlaylistEntry>() {
        public PlaylistEntry createFromParcel(Parcel in){
            return new PlaylistEntry(in);
        }

        @Override
        public PlaylistEntry[] newArray(int size) {
            return new PlaylistEntry[size];
        }
    };
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
    
    public PlaylistEntry(SongMetadata metadata, boolean loaded, boolean played, String filePath){
        this(metadata);
        this.loaded = loaded;
        this.played = played;
        this.filePath = filePath;
    }

    public PlaylistEntry(Parcel in){
        super(in);
        boolean[] state = new boolean[2];
        in.readBooleanArray(state);
        this.loaded = state[0];
        this.played = state[1];
        this.filePath = in.readString();
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
    }
}
