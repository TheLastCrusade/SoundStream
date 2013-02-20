package com.lastcrusade.fanclub.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;

import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.model.SongMetadata;

public class MediaStoreWrapper {

    private Activity mActivity;

    public MediaStoreWrapper(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public List<SongMetadata> list() {
        String[] proj = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE};
        Cursor cursor = this.mActivity.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null,
                null);
        List<SongMetadata> newSongList = new ArrayList<SongMetadata>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SongMetadata song = new SongMetadata();
            song.setId(cursor.getLong(0));
            song.setAlbum(cursor.getString(1));
            song.setArtist(cursor.getString(2));
            song.setTitle(cursor.getString(3));
            newSongList.add(song);
            cursor.moveToNext();
        }

        Collections.sort(newSongList, new Comparator<SongMetadata>() {

            @Override
            public int compare(SongMetadata lhs, SongMetadata rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }

        });
        return newSongList;
    }

    public Song loadSongData(SongMetadata metadata) {
        String[] proj = { MediaStore.Audio.Media._ID,
              MediaStore.Audio.Media.DATA, //NOTE: actually the path to the song, not the raw data
              MediaStore.Video.Media.SIZE 
              };
        String selection = MediaStore.Audio.Media._ID + "=" + Long.toString(metadata.getId());
        Cursor cursor = this.mActivity.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, selection, null,
                null);
        List<Song> newSongList = new ArrayList<Song>();
        cursor.moveToFirst();
        
        Song song = new Song(metadata);
        song.setFilePath(cursor.getString(1));
        song.setSize(cursor.getLong(2));
        return song;
    }
}
