package com.lastcrusade.fanclub.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;

import com.lastcrusade.fanclub.CustomApp;
import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.util.MusicListAdapter;

public class PlaylistFragment extends MusicListFragment{
    //for testing purposes so we have songs to show
    static List<SongMetadata> metadataList = new ArrayList<SongMetadata>(
            Arrays.asList(new SongMetadata(), new SongMetadata(), new SongMetadata(),
                    new SongMetadata(),new SongMetadata(),new SongMetadata(),new SongMetadata(),
                    new SongMetadata(),new SongMetadata(),new SongMetadata(),new SongMetadata(),
                    new SongMetadata(),new SongMetadata(),new SongMetadata(),new SongMetadata()));
    
    public PlaylistFragment(){
        for(SongMetadata s : metadataList){
            s.setTitle("This is a really really really loooooooooooooooooooooo" +
            		"ooooooooooooooooooooooooong Title");
            s.setAlbum("Album is super freaking long too");
            s.setArtist("Artist is too, but not as much");
            s.setUsername("Reid");
         }
         metadataList.get(0).setUsername("Reid");
         metadataList.get(1).setUsername("Lizziemom");
         metadataList.get(2).setUsername("Greenie");
        
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        CustomApp curApp = (CustomApp) this.getActivity().getApplication();
        setListAdapter(new MusicListAdapter(this.getActivity(), metadataList, curApp.getUserList()));
    }  
    
    @Override
    public String getTitle() {
        return getString(R.string.playlist);
    }
}
