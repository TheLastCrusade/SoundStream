package com.lastcrusade.fanclub;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.util.MusicListAdapter;
import com.lastcrusade.fanclub.util.Titleable;

import java.util.List;

public class MusicLibraryFragment extends SherlockListFragment implements Titleable {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        //whatever our song list is needs to be passed to the musiclistadapter
        setListAdapter(new MusicAdapter( this.getActivity(), PlaylistFragment.songs ));
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // construct the RelativeLayout
        
        View v = inflater.inflate(R.layout.list, container, false);

        return v;
    }
    
    public void onResume(Bundle savedInstanceState){
        getActivity().setTitle(R.string.music_library);
    }
    
    private class MusicAdapter extends MusicListAdapter{

        public MusicAdapter(Context mContext, List<SongMetadata> songs) {
            super(mContext, songs);
        }
        
        public View getView(int position, View convertView, ViewGroup parent){
            View v = super.getView(position, convertView, parent);
            
            v.findViewById(R.id.add_to_playlist).setVisibility(View.VISIBLE);
            
            
            return v;
        }
        
    }
    
    @Override
    public String getTitle() {
        return getString(R.string.music_library);
    }
    
    
}
