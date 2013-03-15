package com.lastcrusade.fanclub.components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.lastcrusade.fanclub.CoreActivity;
import com.lastcrusade.fanclub.CustomApp;
import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.util.ITitleable;
import com.lastcrusade.fanclub.util.Transitions;
import com.lastcrusade.fanclub.util.UserListAdapter;

//This will probably change to a regular fragment instead of a list one
//once I (@ejohnson44) get into the layout more
public class MenuFragment extends SherlockFragment implements ITitleable {
 
   
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.menu, container,false);
        
        Button playlist = (Button)v.findViewById(R.id.playlist_btn);
        playlist.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Transitions.transitionToPlaylist((CoreActivity)getActivity());
                
            }
        });
        
        Button musicLibrary = (Button)v.findViewById(R.id.music_library_btn);
        musicLibrary.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Transitions.transitionToMusicLibrary((CoreActivity)getActivity());
                
            }
        });
      
        
        Button network = (Button)v.findViewById(R.id.network_btn);
        network.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Transitions.transitionToNetwork((CoreActivity)getActivity());
            }
        });
        
        ListView users = (ListView)v.findViewById(R.id.connected_users);
        
        users.setAdapter(new UserListAdapter(getActivity(), ((CustomApp)getActivity().getApplication()).getUserList(),true ));
        
        return v;
    }
    
    @Override
    public String getTitle() {
        return getString(R.string.app_name);
    }
}
