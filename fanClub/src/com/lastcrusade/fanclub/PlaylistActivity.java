package com.lastcrusade.fanclub;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.util.MusicListAdapter;

//JR, 02/21/13, NOTE: FragmentActivity required for "support" fragments...it shouldnt hurt anything to leave this in here now.
public class PlaylistActivity extends FragmentActivity {
    //for testing purposes so we have songs to show
    Song[] songs = new Song[]{
            new Song(new SongMetadata()),
            new Song(new SongMetadata()),
            new Song(new SongMetadata())
    };

  
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        //for testing purposes
        for(Song s:songs){
            SongMetadata sm = s.getMetadata();
            sm.setTitle("Title");
            sm.setAlbum("Album");
            sm.setArtist("Artist");
//            s.setSongMetadata(sm);
         }
         songs[0].getMetadata().setUsername("Reid");
         songs[1].getMetadata().setUsername("Lizziemom");
         songs[2].getMetadata().setUsername("Greenie");
         
        setContentView(R.layout.activity_playlist);
        ListView playlist = (ListView)findViewById(R.id.playlistView);
        playlist.setAdapter(new MusicListAdapter(this,songs));
    }
}
