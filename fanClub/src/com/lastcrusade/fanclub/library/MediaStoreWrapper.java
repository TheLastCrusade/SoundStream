package com.lastcrusade.fanclub.library;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;
import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.model.SongMetadata;

import java.util.ArrayList;
import java.util.List;

public class MediaStoreWrapper {

    private Activity activity;

    public MediaStoreWrapper(Activity activity) {
        this.activity = activity;
    }

    /**
     * Lists all media files on the device
     * @return A list of metadata for all the songs on the device
     */
    public List<SongMetadata> list() {
        String[] proj = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE};
        Cursor cursor = this.activity.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                proj, null, null, null);
        List<SongMetadata> metadataList = new ArrayList<SongMetadata>();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            SongMetadata metadata = new SongMetadata();
            metadata.setId(cursor.getLong(0));
            metadata.setAlbum(cursor.getString(1));
            metadata.setArtist(cursor.getString(2));
            metadata.setTitle(cursor.getString(3));
            metadataList.add(metadata);
            cursor.moveToNext();
        }

        cursor.close();
        return metadataList;
    }

    /**
     * Gets a song from the MediaStore, else errors with SongNotFoundException
     * @param metadata metadata for the requested song
     * @return song object
     */
    public Song loadSongData(SongMetadata metadata) throws SongNotFoundException {
        String[] proj = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA, //NOTE: actually the path to the song, not the raw data
                MediaStore.Video.Media.SIZE
        };
        String selection = MediaStore.Audio.Media._ID + "=" + Long.toString(metadata.getId());
        Cursor cursor = this.activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                proj, selection, null, null);
        Song song = null;

        if (cursor.moveToFirst()) {
            song = new Song(metadata);
            song.setFilePath(cursor.getString(1));
            song.setSize(cursor.getLong(2));
        }
        cursor.close();

        if (song == null) {
            throw new SongNotFoundException("Song not found: " + metadata.getArtist() + " - " + metadata.getTitle());
        }
        return song;
    }
}
