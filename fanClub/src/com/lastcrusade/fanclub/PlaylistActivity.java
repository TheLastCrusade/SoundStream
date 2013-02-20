package com.lastcrusade.fanclub;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.util.MusicListAdapter;

public class PlaylistActivity extends Activity {
    //for testing purposes so we have songs to show
    Song[] songs = new Song[]{
            new Song(),
            new Song(),
            new Song()
    };

  
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        //for testing purposes
        for(Song s:songs){
            SongMetadata sm = new SongMetadata();
            sm.setTitle("Title");
            sm.setAlbum("Album");
            sm.setArtist("Artist");
            s.setSongMetadata(sm);
         }
         songs[0].getMetadata().setUsername("Reid");
         songs[1].getMetadata().setUsername("Lizziemom");
         songs[2].getMetadata().setUsername("Greenie");
         
        setContentView(R.layout.activity_playlist);
        ListView playlist = (ListView)findViewById(R.id.playlistView);
        playlist.setAdapter(new MusicListAdapter(this,songs));
    }
}
