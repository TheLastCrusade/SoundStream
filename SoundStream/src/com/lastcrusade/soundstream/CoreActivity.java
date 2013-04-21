/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lastcrusade.soundstream;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.lastcrusade.soundstream.components.MenuFragment;
import com.lastcrusade.soundstream.components.PlaybarFragment;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.service.ConnectionService;
import com.lastcrusade.soundstream.service.IMessagingService;
import com.lastcrusade.soundstream.service.MessagingService;
import com.lastcrusade.soundstream.service.MusicLibraryService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
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

    private ServiceLocator<MusicLibraryService>   musicLibraryLocator;
    private ServiceLocator<MessagingService>    messagingServiceLocator;

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
        
        playbar = new PlaybarFragment();
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.playbar, playbar)
            .commit();
        
        //We want to start off at the connect page if this is the first time
        // the activity is created
        if(savedInstanceState == null) {
            Transitions.transitionToConnect(this);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            hidePlaybar();
        }
        else{
            //if we're not entering for the first time, we want to make sure that 
            //we still have access to the sliding menu
            enableSlidingMenu();
            showPlaybar();
        }

        // setup the sliding bar
        setSlidingActionBarEnabled(false);
        getSlidingMenu().setBehindWidthRes(R.dimen.show_menu);
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

    @Override
    public void onStart() {
      super.onStart();
      //For Google Analytics
      EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
      super.onStop();
      //For Google Analytics
      EasyTracker.getInstance().activityStop(this);
    }
    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar
            .addAction(ConnectionService.ACTION_HOST_DISCONNECTED, new IBroadcastActionHandler() {
            
                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    //Send the user to a page where they can start a network or join a different network
                    Transitions.transitionToConnect(CoreActivity.this);
                }
            })
            .addAction(ConnectionService.ACTION_HOST_CONNECTED, new IBroadcastActionHandler() {

                @Override
                public void onReceiveAction(Context context, Intent intent) {
                    //send the library to the connected host
                    List<SongMetadata> metadata = getMusicLibraryService().getMyLibrary();
                    getMessagingService().sendLibraryMessageToHost(metadata);
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
        .show(playbar)
        .commit();
    }
    
    public void hidePlaybar(){
        getSupportFragmentManager()
        .beginTransaction()
        .hide(playbar)
        .commit();
    }

    private IMessagingService getMessagingService() {
        MessagingService messagingService = null;
        try {
            messagingService = this.messagingServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return messagingService;
    }

    private MusicLibraryService getMusicLibraryService() {
        MusicLibraryService musicLibraryService = null;
        try {
            musicLibraryService = this.musicLibraryLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return musicLibraryService;
    }
}
