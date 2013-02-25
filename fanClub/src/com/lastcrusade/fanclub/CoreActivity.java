package com.lastcrusade.fanclub;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class CoreActivity extends SlidingFragmentActivity{
    private Fragment mainContent;
    
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            mainContent = getSupportFragmentManager().getFragment(savedInstanceState, "mainContent");
        if (mainContent == null)
            mainContent = new PlaylistFragment();  
        
        setContentView(R.layout.content_frame);
       // PlaylistFragment pFragment = new PlaylistFragment();
        
        getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.content_frame, mainContent)
        .commit();
        
        setBehindContentView(R.layout.menu_frame);
        
        MenuFragment mFragment = new MenuFragment();
        getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.menu_frame, mFragment)
        .commit();
        
        getSlidingMenu().setBehindOffset(60);
        setSlidingActionBarEnabled(false);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Music");
    }
    
    public void switchContent(Fragment f){
        mainContent = f;
        
        getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.content_frame, mainContent)
        .commit();
        
        getSlidingMenu().showContent();
        
        if(f instanceof MusicLibraryFragment)
            setTitle("Music Library");
    }
    
    
    
    public boolean onOptionsItemSelected(MenuItem item) {
       if(item.getItemId()==android.R.id.home){
           toggle();
           return true;
   
       }
       
       return false;
    }
}
