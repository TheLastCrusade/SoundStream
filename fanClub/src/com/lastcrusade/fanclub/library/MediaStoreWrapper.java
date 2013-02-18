package com.lastcrusade.fanclub.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;

import com.lastcrusade.fanclub.model.Song;

public class MediaStoreWrapper {

    private Activity mActivity;

    public MediaStoreWrapper(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public List<Song> list() {
        String[] proj = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE};
        Cursor cursor = this.mActivity.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null,
                null);
        List<Song> newSongList = new ArrayList<Song>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Song song = new Song();
            song.setId(cursor.getLong(0));
            song.setAlbum(cursor.getString(1));
            song.setArtist(cursor.getString(2));
            song.setName(cursor.getString(3));
            newSongList.add(song);
            cursor.moveToNext();
        }

        Collections.sort(newSongList, new Comparator<Song>() {

            @Override
            public int compare(Song lhs, Song rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }

        });
        return newSongList;
    }

    public Song loadSongData(Song song) {
        String[] proj = { MediaStore.Audio.Media._ID,
              MediaStore.Audio.Media.DATA, //NOTE: actually the path to the song, not the raw data
              MediaStore.Video.Media.SIZE 
              };
        String selection = MediaStore.Audio.Media._ID + "=" + Long.toString(song.getId());
        Cursor cursor = this.mActivity.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, selection, null,
                null);
        List<Song> newSongList = new ArrayList<Song>();
        cursor.moveToFirst();
        
        song.setFilePath(cursor.getString(1));
        song.setSize(cursor.getLong(2));
        return song;
    }
}
