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
package com.lastcrusade.soundstream.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lastcrusade.soundstream.model.User;
import com.lastcrusade.soundstream.model.UserList;
import com.lastcrusade.soundstream.util.BluetoothUtils;
import com.lastcrusade.soundstream.util.LocalBroadcastIntent;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;

public class UserListService extends Service {
    private static final String TAG = UserListService.class.getSimpleName();
    
    private BroadcastRegistrar registrar;
    private UserList curUserList;
    private ServiceLocator<MessagingService> messagingServiceLocator;
    private String myMac;
    private String myName;
    
    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class UserListServiceBinder extends Binder implements ILocalBinder<UserListService> {
        public UserListService getService() {
            return UserListService.this;
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();

        curUserList = new UserList();

        messagingServiceLocator = new ServiceLocator<MessagingService>(
                this, MessagingService.class, MessagingService.MessagingServiceBinder.class);
        myName = BluetoothUtils.getLocalBluetoothName();
        myMac = BluetoothUtils.getLocalBluetoothMAC();

        addSelfToUserList();
        registerReceivers();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return new UserListServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceivers();
        return super.onUnbind(intent);
    }

    private void registerReceivers() {
        this.registrar = new BroadcastRegistrar();
        this.registrar.addLocalAction(ConnectionService.ACTION_GUEST_CONNECTED, new IBroadcastActionHandler() {
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                String bluetoothID = intent.getStringExtra(ConnectionService.EXTRA_GUEST_NAME);
                String macAddress  = intent.getStringExtra(ConnectionService.EXTRA_GUEST_ADDRESS);
                curUserList.addUser(bluetoothID, macAddress);
                notifyUserListUpdate();
            }
        })
        .addLocalAction(ConnectionService.ACTION_GUEST_DISCONNECTED, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                String macAddress  = intent.getStringExtra(ConnectionService.EXTRA_GUEST_ADDRESS);
                curUserList.removeUser(macAddress);
                notifyUserListUpdate();
            }
        })
        .addLocalAction(MessagingService.ACTION_NEW_CONNECTED_USERS_MESSAGE, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                //extract the new user list from the intent
                UserList newUserList = (UserList) intent.getParcelableExtra(MessagingService.EXTRA_USER_LIST);
                addSelfToUserList(newUserList);

                //Create list of removed users
                UserList removedUsers = new UserList();
                for(User user : curUserList.getUsers()) {
                    if(!newUserList.getUsers().contains(user)){
                        removedUsers.addUser(user.getBluetoothID(), user.getMacAddress());
                    }
                }

                //Replace current userlist
                curUserList.copyFrom(newUserList);
                Log.i(TAG, "New userlist with length" + curUserList.getUsers().size());
                //tell app to update the user list in all the UI
                new LocalBroadcastIntent(UserList.ACTION_USER_LIST_UPDATE)
                .putExtra(UserList.EXTRA_REMOVED_USERS, removedUsers)
                .send(UserListService.this);
            }
        })
        .register(this);
    }

    public void clearExternalUsers() {
        this.curUserList.clear();
        addSelfToUserList();
        new LocalBroadcastIntent(UserList.ACTION_USER_LIST_UPDATE).send(UserListService.this);
    }

    private void notifyUserListUpdate() {
        new LocalBroadcastIntent(UserList.ACTION_USER_LIST_UPDATE).send(this);
        getMessagingService().sendUserListMessage(curUserList);
    }

    private void addSelfToUserList() {
        addSelfToUserList(curUserList);
    }

    private void addSelfToUserList(UserList list){
        list.addUser(myName, myMac);
    }

    private void unregisterReceivers() {
        this.registrar.unregister();
        messagingServiceLocator.unbind();
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

    public UserList getUserList(){
        return curUserList;
    }

    public String getMyMac() {
        return myMac;
    }

    public String getMyName(){
        return myName;
    }

}
