package com.lastcrusade.soundstream;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.MenuItem;
import com.lastcrusade.soundstream.components.MenuFragment;
import com.lastcrusade.soundstream.components.PlaybarFragment;
import com.lastcrusade.soundstream.service.ConnectionService;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.ITitleable;
import com.lastcrusade.soundstream.util.Transitions;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;


public class CoreActivity extends SlidingFragmentActivity{
    private final String TAG = CoreActivity.class.getName();

    private Fragment menu;
    private PlaybarFragment playbar;
    private BroadcastRegistrar registrar;
        
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
        
        //We want to start off at the connect page if this is the first time
        // the activity is created
        if(savedInstanceState == null) {
            Transitions.transitionToConnect(this);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            setSlidingActionBarEnabled(false);
            
            playbar = new PlaybarFragment();
        }
        else{
            //if we're not entering for the first time, we want to make sure that 
            //we still have access to the sliding menu
            enableSlidingMenu();
        }

        // setup the sliding bar
        getSlidingMenu().setBehindOffsetRes(R.dimen.show_content);
        
        
        
        
        
        
        
        
        //setTitle(getString(R.string.playlist));

        //Log.i("Core", " " + curApp.getUserList().getUserByMACAddress(BluetoothUtils.getLocalBluetoothMAC()));
        
        registerReceivers();
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

    @Override
    protected void onDestroy() {
        unregisterReceivers();
        super.onDestroy();
    }
    
    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar
            .addAction(ConnectionService.ACTION_HOST_DISCONNECTED, new IBroadcastActionHandler() {
            
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    //after the host has been disconnected, pull the guest back to the connect page
                    Transitions.transitionToConnect(CoreActivity.this);
                    getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
            })
            .register(this);
        
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }

    public void showContent(){
        getSlidingMenu().showContent();
    }
    
    public void enableSlidingMenu(){
     // enables the icon to act as the up
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
    }
    
    public void showPlaybar(){
        getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.playbar, playbar)
        .commit();
    }
}
