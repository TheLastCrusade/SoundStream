package com.lastcrusade.soundstream.components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.CoreActivity;
import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.util.ITitleable;
import com.lastcrusade.soundstream.util.Transitions;
import com.lastcrusade.soundstream.util.UserListAdapter;


public class MenuFragment extends SherlockFragment implements ITitleable {
 
 
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
        users.setAdapter(new UserListAdapter(getActivity(), 
                ((CustomApp)getActivity().getApplication()).getUserList(),true ));
        
        return v;
    }
    
    @Override
    public int getTitle() {
        return R.string.app_name;
    }
}
