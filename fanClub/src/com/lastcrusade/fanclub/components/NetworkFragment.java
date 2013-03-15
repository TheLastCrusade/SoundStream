package com.lastcrusade.fanclub.components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.lastcrusade.fanclub.CustomApp;
import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.util.UserListAdapter;
/*
 * This fragment handles the ability for members to add new members to 
 * the network and to view the currently connected members
 */
public class NetworkFragment extends SherlockFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_network, container,false);
       
        
        Button add = (Button)v.findViewById(R.id.add_members);
        add.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Jesse - make this button actually find the discoverable fans and present 
                // them to the user - I(@ejohnson44) will clean it up visually once it gets here
                
            }
        });
        
        ListView users = (ListView)v.findViewById(R.id.connected_users);
        
        users.setAdapter(new UserListAdapter(getActivity(), ((CustomApp)getActivity().getApplication()).getUserList(), false ));
        
        
        return v;
    }
}
