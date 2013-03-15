package com.lastcrusade.fanclub;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.MenuItem;
import com.lastcrusade.fanclub.components.MenuFragment;
import com.lastcrusade.fanclub.components.PlaybarFragment;
import com.lastcrusade.fanclub.util.BluetoothUtils;
import com.lastcrusade.fanclub.util.ITitleable;
import com.lastcrusade.fanclub.util.Transitions;
import com.slidingmenu.lib.app.SlidingFragmentActivity;


public class CoreActivity extends SlidingFragmentActivity{
    private Fragment activeContent;
    private Fragment menu;
    private final String TAG = CoreActivity.class.getName();
        
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //set the layout for the content - this is just a placeholder
        setContentView(R.layout.content_frame);
        
        //set the layout for the menu
        setBehindContentView(R.layout.menu_frame);
        
        //add the menu
        menu = new MenuFragment();
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.menu_frame, menu)
            .commit();
        
        //We want to start off at the playlist if this is the first time
        // the activity is created
        if(savedInstanceState == null){
            //add the initial content fragment and set the title on the action bar
            Transitions.transitionToHome(this);
            setTitle(getString(R.string.playlist));
        }

        // setup the sliding bar
        getSlidingMenu().setBehindOffsetRes(R.dimen.show_content);
        setSlidingActionBarEnabled(false);
        
        //add the playbar fragment onto the active content view
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.playbar, new PlaybarFragment())
            .commit();
        
        // enables the icon to act as the up
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        //setTitle(getString(R.string.playlist));

        //Add user to user list
        CustomApp curApp = ((CustomApp)getApplication());
        
        //TODO: Move this to something like connect activity or the connection fragment
        curApp.getUserList().addUser(BluetoothUtils.getLocalBluetoothName(), BluetoothUtils.getLocalBluetoothMAC());
        //Log.i("Core", " " + curApp.getUserList().getUserByMACAddress(BluetoothUtils.getLocalBluetoothMAC()));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // home references the app icon
        if (item.getItemId() == android.R.id.home) {
            toggle(); // toggles the state of the sliding menu
            if(getSlidingMenu().isMenuShowing() && menu.isAdded()){
                setTitle(((ITitleable)menu).getTitle());
            }
            return true;
        }
        return false;
    }
    
    public void showContent(){
        getSlidingMenu().showContent();
    }
}
