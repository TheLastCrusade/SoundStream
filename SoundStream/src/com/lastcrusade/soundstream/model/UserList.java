package com.lastcrusade.soundstream.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.lastcrusade.soundstream.util.BroadcastIntent;

public class UserList {

    public static final String ACTION_USER_LIST_UPDATE = UserList.class.getName() + ".action.UserList";

    private List<User> connectedUsers;    
    private UserColors userColors;
    private final String TAG = UserList.class.toString();

    public UserList(){
        userColors = new UserColors();
        connectedUsers = new ArrayList<User>();
    }

    public UserList(String bluetoothID, String macAddress){
        this();
        connectedUsers.add(new User(bluetoothID, macAddress, userColors.getNextAvailableColor()));
    }

    public void addUser(String bluetoothID, String macAddress){
        //check to make sure that the user isn't already in the list before adding
        if(getUserByMACAddress(macAddress)==null){
            connectedUsers.add(new User(bluetoothID, macAddress, userColors.getNextAvailableColor()));
        }
    }

    //untested
    public void removeUser(String macAddress){
        int removeIndex = -1;
        for(int i=0; i<connectedUsers.size(); i++){
            if(connectedUsers.get(i).getMacAddress().equals(macAddress)){
                removeIndex = i;
            }
        }
        if(removeIndex >-1){
            int color = connectedUsers.get(removeIndex).getColor();
            userColors.returnColor(color);
            connectedUsers.remove(removeIndex);
        }
    }

    public List<User> getUsers(){
        return connectedUsers;
    }

    //get a list of bluetoothIDs of the connected users
    public List<String> getBluetoothIDs(){
        ArrayList<String> bluetoothIDs = new ArrayList<String>();

        for(User u:connectedUsers){
            bluetoothIDs.add(u.getBluetoothID());
        }
        
        return bluetoothIDs;
    }
    
    //get a list of macAddresses of the connected users
    public List<String> getMacAddresses(){
        ArrayList<String> macAddresses = new ArrayList<String>();

        for(User u:connectedUsers){
            macAddresses.add(u.getMacAddress());
        }
        
        return macAddresses;
    }
    
    //using macAddress instead of bluetooth id to make sure that
    //it is unique
    public User getUserByMACAddress(String macAddress){
        User user = null;
        if(macAddress == null){
            //TODO May want to make sure this is a valid mac as well and
            // possibly throw an exception
            Log.wtf(TAG, "Cannot check for null mac address");
        } else {
            for (User u : connectedUsers) {
                if (u.getMacAddress().equals(macAddress)) {
                    user = u;
                }
            }
        }
        return user;
    }

    public void notifyUpdate(Context context) {
        new BroadcastIntent(ACTION_USER_LIST_UPDATE).send(context);
    }

    @Override
    public String toString() {
        String users;
        if(connectedUsers == null){
            users = "No connected users";
        } else {
            StringBuilder sb = new StringBuilder();
            for(User user: connectedUsers){
                sb.append(user.getBluetoothID());
                sb.append(':');
                sb.append(user.getMacAddress());
                sb.append('\n');
            }
            users = sb.toString();
        }
        return users;
    }
}
