package com.lastcrusade.fanclub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.util.MusicListAdapter;
import com.lastcrusade.fanclub.util.Titleable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaylistFragment extends SherlockListFragment implements Titleable{
    //for testing purposes so we have songs to show
    static List<SongMetadata> metadataList = new ArrayList<SongMetadata>(
            Arrays.asList(new SongMetadata(), new SongMetadata(), new SongMetadata()));
    

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setListAdapter(new MusicListAdapter(this.getActivity(), metadataList));
    }
    
    public PlaylistFragment(){
        for(SongMetadata s : metadataList){
            s.setTitle("Title");
            s.setAlbum("Album");
            s.setArtist("Artist");
         }
         metadataList.get(0).setUsername("Reid");
         metadataList.get(1).setUsername("Lizziemom");
         metadataList.get(2).setUsername("Greenie");
        
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
