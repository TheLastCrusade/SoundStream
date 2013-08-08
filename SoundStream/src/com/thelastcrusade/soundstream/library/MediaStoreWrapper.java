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

package com.thelastcrusade.soundstream.library;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.thelastcrusade.soundstream.model.SongMetadata;

public class MediaStoreWrapper {

    private Context context;
    private final String ID = MediaStore.Audio.Media._ID,
                      ALBUM = MediaStore.Audio.Media.ALBUM,
                     ARTIST = MediaStore.Audio.Media.ARTIST,
                      TITLE = MediaStore.Audio.Media.TITLE,
                       PATH = MediaStore.Audio.Media.DATA, //NOTE: actually the path to the song, not the raw data
                       SIZE = MediaStore.Video.Media.SIZE;
    private final Uri EC_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    public MediaStoreWrapper(Context context) {
        this.context = context;
    }

    /**
     * Lists all media files on the device
     * @return A list of metadata for all the songs on the device
     */
    public List<SongMetadata> list() {
        String[] proj = {ID, ALBUM, ARTIST, TITLE, SIZE};
        Cursor cursor = context.getContentResolver().query(EC_URI, proj, null, null, null);
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
        Cursor cursor = context.getContentResolver().query(EC_URI, proj, selection, null, null);

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
