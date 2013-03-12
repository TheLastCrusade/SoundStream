package com.lastcrusade.fanclub;

import com.lastcrusade.fanclub.model.UserList;
import com.lastcrusade.fanclub.service.ConnectionService;
import com.lastcrusade.fanclub.service.IMessagingService;
import com.lastcrusade.fanclub.service.MessagingService;
import com.lastcrusade.fanclub.service.ServiceLocator;
import com.lastcrusade.fanclub.service.ServiceNotBoundException;
import com.lastcrusade.fanclub.service.ConnectionService.ConnectionServiceBinder;
import com.lastcrusade.fanclub.service.MessagingService.MessagingServiceBinder;

import android.app.Application;
import android.util.Log;

public class CustomApp extends Application {
    private final String TAG = CustomApp.class.getName();
    
    private UserList userList;
    
    private ServiceLocator<ConnectionService> connectionServiceLocator;
    private ServiceLocator<MessagingService>  messagingServiceLocator;

    public CustomApp() {
        super();
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        userList = new UserList();
        
        connectionServiceLocator = new ServiceLocator<ConnectionService>(
                this, ConnectionService.class, ConnectionServiceBinder.class);
        
        messagingServiceLocator = new ServiceLocator<MessagingService>(
                this, MessagingService.class, MessagingServiceBinder.class);
    }
    
    public UserList getUserList(){
        return userList;
    }
    
    public ConnectionService getConnectionService() {
        ConnectionService connectionService = null;
        try {
            connectionService = this.connectionServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return connectionService;
    }

    public IMessagingService getMessagingService() {
        MessagingService messagingService = null;
        try {
            messagingService = this.messagingServiceLocator.getService();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
        return messagingService;
    }

}
