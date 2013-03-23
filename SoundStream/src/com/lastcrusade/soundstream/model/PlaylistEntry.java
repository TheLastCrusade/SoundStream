package com.lastcrusade.soundstream.model;

import com.lastcrusade.soundstream.util.BluetoothUtils;

public class PlaylistEntry extends SongMetadata {

    private boolean loaded   = false;
    private boolean played   = false;
    private String  filePath = null;

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
        this.setTitle(metadata.getTitle());
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
}
