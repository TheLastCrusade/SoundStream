package com.lastcrusade.fanclub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.util.MusicListAdapter;
import com.slidingmenu.lib.SlidingMenu;

public class PlaylistFragment extends SherlockFragment{
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
         
    }
    
    public PlaylistFragment(){
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
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // construct the RelativeLayout
        View v = inflater.inflate(R.layout.activity_playlist, container, false);
        ListView playlist = (ListView)v.findViewById(R.id.playlistView);
        playlist.setAdapter(new MusicListAdapter(this.getActivity(),songs));
        return v;
    }
    
    
}
