package com.lastcrusade.fanclub.util;

import android.support.v4.app.Fragment;

import com.lastcrusade.fanclub.CoreActivity;
import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.components.ConnectFragment;
import com.lastcrusade.fanclub.components.MusicLibraryFragment;
import com.lastcrusade.fanclub.components.NetworkFragment;
import com.lastcrusade.fanclub.components.PlaylistFragment;

/**
 * Singleton class to manage transitions of the active content
 * displayed on the app. 
 */
public class Transitions {
    
    private final static int PLAYLIST = R.string.playlist;
    private final static int MUSIC_LIBRARY = R.string.music_library;
    private final static int NETWORK  = R.string.network;
    private final static int CONNECT = R.string.connect;
    //Home is where you get sent after connecting to the network - for now
    //this is the playlist
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
    
    public static void transitionToConnect(CoreActivity activity){
        switchFragment(CONNECT, activity);
    }
    
    private static void switchFragment(int fragmentName, CoreActivity activity){
        Fragment fragment = getFragment(fragmentName);
        
        activity.getSupportFragmentManager().beginTransaction()
            .replace(R.id.content_frame, fragment)
            .addToBackStack(null).commit();
        activity.showContent();
        String title = activity.getResources().getString(((ITitleable)fragment).getTitle());
        activity.setTitle(title);
    }
    
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
        else if(fragmentName==CONNECT){
            newFragment = new ConnectFragment();
        }
        return newFragment;
    }
}
