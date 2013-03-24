package com.lastcrusade.soundstream.library;

import android.app.Service;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.lastcrusade.soundstream.model.PlaylistEntry;
import com.lastcrusade.soundstream.model.SongMetadata;

import java.util.ArrayList;
import java.util.List;

public class MediaStoreWrapper {

    private Service service;
    private final String ID = MediaStore.Audio.Media._ID,
                      ALBUM = MediaStore.Audio.Media.ALBUM,
                     ARTIST = MediaStore.Audio.Media.ARTIST,
                      TITLE = MediaStore.Audio.Media.TITLE,
                       PATH = MediaStore.Audio.Media.DATA, //NOTE: actually the path to the song, not the raw data
                       SIZE = MediaStore.Video.Media.SIZE;
    private final Uri EC_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    public MediaStoreWrapper(Service ser) {
        service = ser;
    }

    /**
     * Lists all media files on the device
     * @return A list of metadata for all the songs on the device
     */
    public List<SongMetadata> list() {
        String[] proj = {ID, ALBUM, ARTIST, TITLE, SIZE};
        Cursor cursor = service.getContentResolver().query(EC_URI, proj, null, null, null);
        List<SongMetadata> metadataList = new ArrayList<SongMetadata>();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            SongMetadata metadata = new SongMetadata();
            metadata.setId(cursor.getLong(cursor.getColumnIndex(ID)));
            metadata.setAlbum(cursor.getString(cursor.getColumnIndex(ALBUM)));
            metadata.setArtist(cursor.getString(cursor.getColumnIndex(ARTIST)));
            metadata.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
            metadata.setFileSize(cursor.getLong(cursor.getColumnIndex(SIZE)));
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
    public String getSongFilePath(SongMetadata metadata) throws SongNotFoundException {
        
        String[] proj = {ID, PATH};
        String selection = ID + "=" + Long.toString(metadata.getId());
        Cursor cursor = service.getContentResolver().query(EC_URI, proj, selection, null, null);

        try {
            String filePath = null;
            
            if (cursor.moveToFirst()) { //moves cursor to first element in result set. false if no first element
                filePath = cursor.getString(cursor.getColumnIndex(PATH));
            }

            if (filePath == null) {
                throw new SongNotFoundException("Song not found: "
                        + metadata.getArtist() + " - " + metadata.getTitle());
            }
            return filePath;
        } finally { //prevents memory leaks
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
