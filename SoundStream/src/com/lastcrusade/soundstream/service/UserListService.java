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

import com.lastcrusade.soundstream.model.UserList;
import com.lastcrusade.soundstream.util.BluetoothUtils;
import com.lastcrusade.soundstream.util.BroadcastIntent;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;

public class UserListService extends Service {
    private static final String TAG = UserListService.class.getSimpleName();
    
    private BroadcastRegistrar registrar;
    private UserList userList;
    private ServiceLocator<MessagingService> messagingServiceLocator;
    
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
        userList = new UserList();

        messagingServiceLocator = new ServiceLocator<MessagingService>(
                this, MessagingService.class, MessagingService.MessagingServiceBinder.class);

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
        this.registrar.addAction(ConnectionService.ACTION_GUEST_CONNECTED, new IBroadcastActionHandler() {
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                String bluetoothID = intent.getStringExtra(ConnectionService.EXTRA_GUEST_NAME);
                String macAddress  = intent.getStringExtra(ConnectionService.EXTRA_GUEST_ADDRESS);
                userList.addUser(bluetoothID, macAddress);
                Log.i(TAG, "Guest Disconnected");
            }
        })
        .addAction(ConnectionService.ACTION_GUEST_DISCONNECTED, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                String macAddress  = intent.getStringExtra(ConnectionService.EXTRA_GUEST_ADDRESS);
                userList.removeUser(macAddress);
                notifyUserListUpdate();
            }
        })
        .addAction(MessagingService.ACTION_NEW_CONNECTED_USERS_MESSAGE, new IBroadcastActionHandler() {
            
            @Override
            public void onReceiveAction(Context context, Intent intent) {
                //extract the new user list from the intent
                userList.copyFrom((UserList) intent.getParcelableExtra(MessagingService.EXTRA_USER_LIST));
                //tell app to update the user list in all the UI
                new BroadcastIntent(UserList.ACTION_USER_LIST_UPDATE).send(UserListService.this);
            }
        })
        .register(this);
    }

    public void clearExternalUsers() {
        this.userList.clear();
        addSelfToUserList();
        new BroadcastIntent(UserList.ACTION_USER_LIST_UPDATE).send(UserListService.this);
    }

    private void notifyUserListUpdate() {
        new BroadcastIntent(UserList.ACTION_USER_LIST_UPDATE).send(this);
        getMessagingService().sendUserListMessage(userList);
    }

    private void addSelfToUserList() {
        userList.addUser(BluetoothUtils.getLocalBluetoothName(), BluetoothUtils.getLocalBluetoothMAC());
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
        return userList;
    }

}
