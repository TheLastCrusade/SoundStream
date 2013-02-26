package com.lastcrusade.fanclub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.lastcrusade.fanclub.util.Titleable;

public class MenuFragment extends SherlockListFragment implements Titleable {
    
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        String[] options = new String[] {"Playlist", "Music Library"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,android.R.id.text1, options);
        setListAdapter(adapter);
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container,false);
    }
    
    public void onListItemClick(ListView lv, View v, int position, long id) {
        if(position == 0){
            switchFragment(new PlaylistFragment());
        }
        else if(position == 1)
            switchFragment(new MusicLibraryFragment());
    }
    
    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;
        
        ((CoreActivity)getActivity()).switchContent(fragment);
        
      
    }
    
    @Override
    public String getTitle() {
        return getString(R.string.app_name);
    }
}
