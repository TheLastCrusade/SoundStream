package com.lastcrusade.fanclub;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.lastcrusade.fanclub.util.Titleable;

//This will probably change to a regular fragment instead of a list one
//once I (@ejohnson44) get into the layout more
public class MenuFragment extends SherlockListFragment implements Titleable {
    private final String PLAYLIST = "Playlist";
    private final String MUSICLIBRARY = "MusicLibrary";
    private final Map<String, Integer> DRAWER =new HashMap<String, Integer>(){
        {
            put(PLAYLIST, 0);
            put(MUSICLIBRARY,1);
        }
    }; 
    
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        String[] options = new String[] {"Playlist", "Music Library"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,android.R.id.text1, options);
        setListAdapter(adapter);
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container,false);
    }
    
    public void onListItemClick(ListView lv, View v, int position, long id) {
        if(DRAWER.get(PLAYLIST) == position){
            //might not always make a new fragment - need to learn more about the 
            //fragment manager and how to get the saved instances of fragments
            switchFragment(new PlaylistFragment());
        }
        else if(DRAWER.get(MUSICLIBRARY) == position){
            switchFragment(new MusicLibraryFragment());
        }
    }
    
    private void switchFragment(Fragment fragment) {
        /*getActivity() would be null if the fragment somehow 
         *got unattached from its managing activity
         */
        if (getActivity() != null){
            ((CoreActivity)getActivity()).switchContent(fragment);
        }    
    }
    
    @Override
    public String getTitle() {
        return getString(R.string.app_name);
    }
}
