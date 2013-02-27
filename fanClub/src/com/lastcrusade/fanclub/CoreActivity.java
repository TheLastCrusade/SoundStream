package com.lastcrusade.fanclub;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.MenuItem;
import com.lastcrusade.fanclub.util.Titleable;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class CoreActivity extends SlidingFragmentActivity {
    private Fragment activeContent;
    private Fragment menu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set the layout for the content - this is just a placeholder
        setContentView(R.layout.content_frame);
        
        //add the menu fragment
        setBehindContentView(R.layout.menu_frame);
        switchFragment(getString(R.string.menu), false);
        
        //add the initial content fragment and set the title on the action bar
        switchActiveContent(getString(R.string.playlist));
        setTitle(getString(R.string.playlist));

       
        // setup the sliding bar
        getSlidingMenu().setBehindOffset(60);
        setSlidingActionBarEnabled(false);

        // enables the icon to act as the up
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        setTitle(getString(R.string.playlist));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // home references the app icon
        if (item.getItemId() == android.R.id.home) {
            toggle(); // toggles the state of the sliding menu
            if(getSlidingMenu().isMenuShowing() && menu.isAdded())
                setTitle(((Titleable)menu).getTitle());
            return true;
        }
        return false;
    }
    
    

    public void switchActiveContent(String fragmentName) {
        // switch out the content fragments
        switchFragment(fragmentName, true);

        // close the sliding menu and show the full content fragment
        getSlidingMenu().showContent();
    }

    /*
     * Switches out the current fragment with the fragment represented by the name
     * that is passed in. If content is true, the fragment is replaced as the main content
     *  - if it is false, the fragment is replaced as the menu
     */
    private void switchFragment(String fragmentName, boolean content) {
        if (content) {
            activeContent = getFragment(fragmentName);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, activeContent)
                    .addToBackStack(null).commit();
        } else {
            menu = getFragment(fragmentName);
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame, menu)
                .commit();
        }
    }

    /*
     * Get the fragment associated with the name passed in.
     * If later we decide we want to save the state of the fragments we can
     * rework this to get saved states instead of creating a new fragment
     * every time
     */
    private Fragment getFragment(String fragmentName) {
        
        if(fragmentName.equals(getString(R.string.playlist)))
            return new PlaylistFragment();
        
        if(fragmentName.equals(getString(R.string.music_library)))
            return new MusicLibraryFragment();
        
        if(fragmentName.equals(getString(R.string.menu)))
            return new MenuFragment();
        
        return null;
    }

}
