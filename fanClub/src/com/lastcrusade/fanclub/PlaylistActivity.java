package com.lastcrusade.fanclub;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.util.MusicListAdapter;

public class PlaylistActivity extends Activity {
    Song[] songs = new Song[]{
            new Song(),
            new Song(),
            new Song()
    };
  
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        ListView playlist = (ListView)findViewById(R.id.playlistView);
        playlist.setAdapter(new MusicListAdapter(this,songs));
    }
}
