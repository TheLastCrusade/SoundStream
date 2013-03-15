package com.lastcrusade.fanclub.util;

import android.support.v4.app.Fragment;

import com.lastcrusade.fanclub.CoreActivity;
import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.components.MusicLibraryFragment;
import com.lastcrusade.fanclub.components.NetworkFragment;
import com.lastcrusade.fanclub.components.PlaylistFragment;

public class Transitions {
    
    private final static int PLAYLIST = R.string.playlist;
    private final static int MUSIC_LIBRARY = R.string.music_library;
    private final static int NETWORK  = R.string.network;
    private final static int HOME = PLAYLIST;
    
    public static void transitionToHome(CoreActivity activity){
        switchFragment(HOME, activity);
    }
    
    public static void transitionToPlaylist(CoreActivity activity){
        switchFragment(PLAYLIST,activity);
    }
    
    public static void transitionToMusicLibrary(CoreActivity activity){
        switchFragment(MUSIC_LIBRARY, activity);
    }
    
    public static void transitionToNetwork(CoreActivity activity){
        switchFragment(NETWORK, activity);
    }
    
    private static void switchFragment(int fragmentName, CoreActivity activity){
        Fragment fragment = getFragment(fragmentName);
        
        activity.getSupportFragmentManager().beginTransaction()
            .replace(R.id.content_frame, fragment)
            .addToBackStack(null).commit();
        
        activity.showContent();
    }
    
    /*
     * Get the fragment associated with the name passed in.
     * If later we decide we want to save the state of the fragments we can
     * rework this to get saved states instead of creating a new fragment
     * every time
     */
    private static Fragment getFragment(int fragmentName) {
        Fragment newFragment = null;
        
        if(fragmentName==PLAYLIST){
            newFragment = new PlaylistFragment();
        }
        else if(fragmentName==MUSIC_LIBRARY){
            newFragment = new MusicLibraryFragment();
        }
        else if(fragmentName==NETWORK){
            newFragment = new NetworkFragment();
        }
        
        return newFragment;
    }
}
