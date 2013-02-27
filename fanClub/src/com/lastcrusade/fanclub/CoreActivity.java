package com.lastcrusade.fanclub;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.MenuItem;
import com.lastcrusade.fanclub.service.MusicLibraryService;
import com.lastcrusade.fanclub.service.MusicLibraryService.MusicLibraryServiceBinder;
import com.lastcrusade.fanclub.util.BluetoothUtils;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class CoreActivity extends SlidingFragmentActivity{
    private Fragment mainContent; //TODO find a better name for this

    MusicLibraryService mMusicLibraryService;
    boolean boundToService; //Since you cannot instantly bind, set a boolean
                    // after its safe to call methods

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection musicLibraryConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicLibraryServiceBinder binder = (MusicLibraryServiceBinder) service;
            mMusicLibraryService = binder.getService();
            boundToService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundToService = false;
        }
    };
    
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            mainContent = getSupportFragmentManager().getFragment(savedInstanceState, "mainContent");
        }
        if (mainContent == null){
            mainContent = new PlaylistFragment(); 
        }
        
        setContentView(R.layout.content_frame);
        switchContent(mainContent);
        
        setBehindContentView(R.layout.menu_frame);
        
        MenuFragment menuFragment = new MenuFragment();
        switchFragment(menuFragment, false);
        
        //setup the sliding bar
        getSlidingMenu().setBehindOffset(60);
        setSlidingActionBarEnabled(false);
        
        //enables the icon to act as the up 
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Add user to user list
        CustomApp curApp = ((CustomApp)getApplication());
        curApp.getUserList().addUser(BluetoothUtils.getLocalBluetoothName());
    }
    
    
    public boolean onOptionsItemSelected(MenuItem item) {
        //references the app icon
       if(item.getItemId()==android.R.id.home){
           toggle(); //toggles the state of the sliding menu
           setTitle(getString(R.string.app_name)); 
           return true;
   
       }
       
       return false;
    }
    
    
    public void switchContent(Fragment content){
        //switch out the content fragments
        switchFragment(content, true);
        
        //close the sliding menu and show the full content fragment
        getSlidingMenu().showContent();
        
        //update the activity title
        /*if(content instanceof Titleable){
            setTitle(((Titleable)content).getTitle());
        }*/
    }
    
    /*
     * Switches out the current fragment with the fragment
     * that is passed in. If content is true, the fragment is replaced
     * as the main content - if it is false, the fragment is replaced as the menu
     */
    private void switchFragment(Fragment newFragment, boolean content){
        if(content){
            mainContent = newFragment;
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mainContent)
                .commit(); 
        }
        else{
            getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.menu_frame, newFragment)
            .commit(); 
        }
        
    }

    public MusicLibraryService getMusicLibraryService(){
        return mMusicLibraryService;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //To test Music service
        Intent intentML = new Intent(this, MusicLibraryService.class);
        bindService(intentML, musicLibraryConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the MusicLibrary service
        if (boundToService) {
            unbindService(musicLibraryConn);
            boundToService = false;
        }
    }
}
