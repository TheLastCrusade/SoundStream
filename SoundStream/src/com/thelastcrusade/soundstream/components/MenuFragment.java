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

package com.thelastcrusade.soundstream.components;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.thelastcrusade.soundstream.CoreActivity;
import com.thelastcrusade.soundstream.R;
import com.thelastcrusade.soundstream.model.UserList;
import com.thelastcrusade.soundstream.service.ServiceLocator;
import com.thelastcrusade.soundstream.service.ServiceLocator.IOnBindListener;
import com.thelastcrusade.soundstream.service.ServiceNotBoundException;
import com.thelastcrusade.soundstream.service.UserListService;
import com.thelastcrusade.soundstream.util.BroadcastRegistrar;
import com.thelastcrusade.soundstream.util.ContentDescriptionUtils;
import com.thelastcrusade.soundstream.util.IBroadcastActionHandler;
import com.thelastcrusade.soundstream.util.ITitleable;
import com.thelastcrusade.soundstream.util.Transitions;
import com.thelastcrusade.soundstream.util.UserListAdapter;


public class MenuFragment extends SherlockFragment implements ITitleable {
    private static final String TAG = MenuFragment.class.getSimpleName();
    private BroadcastRegistrar registrar;
    private UserListAdapter userAdapter;
    private ServiceLocator<UserListService> userListServiceLocator;
    private LinearLayout userView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAdapter = new UserListAdapter(getActivity(), new UserList(), true);
        userView = new LinearLayout(getActivity());
        
        userListServiceLocator = new ServiceLocator<UserListService>(
                this.getActivity(), UserListService.class, UserListService.UserListServiceBinder.class);

        //OnServiceBound needs the userAdapter so to avoid a race condition it
        // should be placed after the userAdapter is made.
        userListServiceLocator.setOnBindListener(new IOnBindListener() {
            @Override
            public void onServiceBound() {
                updateUserView();
            }
        });
        registerReceivers();
    }
 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.menu, container,false);
        
        Button playlist = (Button)v.findViewById(R.id.playlist_btn);
        playlist.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Transitions.transitionToPlaylist((CoreActivity)getActivity());
                
            }
        });
        playlist.setContentDescription(ContentDescriptionUtils.PLAYLIST);
        
        Button musicLibrary = (Button)v.findViewById(R.id.music_library_btn);
        musicLibrary.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Transitions.transitionToMusicLibrary((CoreActivity)getActivity());
                
            }
        });
        musicLibrary.setContentDescription(ContentDescriptionUtils.MUSIC_LIBRARY);
        
        Button network = (Button)v.findViewById(R.id.network_btn);
        network.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Transitions.transitionToNetwork((CoreActivity)getActivity());
            }
        });
        network.setContentDescription(ContentDescriptionUtils.NETWORK);
        
        userView = (LinearLayout)v.findViewById(R.id.connected_users);
        updateUserView();
        
        Button about = (Button)v.findViewById(R.id.about_btn);
        about.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Transitions.transitionToAbout((CoreActivity)getActivity());
            }
        });
        
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(userAdapter != null){
            updateUserView();
        }
        getActivity().setTitle(getTitle());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userListServiceLocator.unbind();
        unregisterReceivers();
    }

    @Override
    public int getTitle() {
        return R.string.app_name_no_spaces;
    }

    /**
     * Register intent receivers to control this service
     *
     */
    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar.addLocalAction(UserList.ACTION_USER_LIST_UPDATE, new IBroadcastActionHandler() {
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                updateUserView();
            }
        }).register(this.getActivity());
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
    }

    private UserList getUserListFromService(){
        UserList activeUsers;
        UserListService userService = getUserListService();
        if(userService != null){
            activeUsers = userService.getUserList();
        } else {
            activeUsers = new UserList();
            Log.i(TAG, "UserListService null, returning empty userlist");
        }
        return activeUsers;
    }

    private UserListService getUserListService(){
        UserListService userService = null;
        try{
            userService = userListServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.w(TAG, "UserListService not bound");
        }
        return userService;
    }
    
    private void updateUserView(){
        userView.removeAllViews();
        userAdapter.updateUsers(getUserListFromService());
        for(int i=0; i<userAdapter.getCount(); i++){
            View user = userAdapter.getView(i, null, userView);
            userView.addView(user);
        }
        
    }
}
