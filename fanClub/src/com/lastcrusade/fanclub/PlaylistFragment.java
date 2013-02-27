package com.lastcrusade.fanclub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.util.MusicListAdapter;
import com.lastcrusade.fanclub.util.Titleable;

public class PlaylistFragment extends SherlockListFragment implements Titleable{
    //for testing purposes so we have songs to show
    static Song[] songs = new Song[]{
            new Song(),
            new Song(),
            new Song()
    };
    

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        CustomApp curApp = (CustomApp) this.getActivity().getApplication();
        setListAdapter(new MusicListAdapter(this.getActivity(), songs, curApp.getUserList().getUsers()));
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
        View v = inflater.inflate(R.layout.list, container, false);
        return v;
    }
    
    public void onListItemClick(ListView lv, View v, int position, long id){
        //code for expanding the particular song
    }

    @Override
    public String getTitle() {
        return getString(R.string.playlist);
    }
    
    
}
