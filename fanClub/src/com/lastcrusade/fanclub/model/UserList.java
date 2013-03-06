package com.lastcrusade.fanclub.model;

import java.util.ArrayList;
import java.util.List;

import com.lastcrusade.fanclub.util.BluetoothUtils;

public class UserList {

    private List<User> connectedUsers;    
    private UserColors userColors;

    public UserList(){
        userColors = new UserColors();
        connectedUsers = new ArrayList<User>();
        connectedUsers.add(new User("Reid", userColors.getNextAvailableColor()));
        connectedUsers.add(new User("Greenie", userColors.getNextAvailableColor()));
        connectedUsers.add(new User("Sills", userColors.getNextAvailableColor()));
        connectedUsers.add(new User("Jesse", userColors.getNextAvailableColor()));
        connectedUsers.add(new User("Lizziemom", userColors.getNextAvailableColor()));
    }

    public UserList(String user){
        userColors = new UserColors();
        connectedUsers = new ArrayList<User>();
        connectedUsers.add(new User(user, userColors.getNextAvailableColor()));
    }

    public void addUser(String username){
        if(getUserByName(username)==null)
            connectedUsers.add(new User(username, userColors.getNextAvailableColor()));
    }

    public void removeUser(String user){
        int removeIndex = -1;
        for(int i=0; i<connectedUsers.size(); i++){
            if(connectedUsers.get(i).getBluetoothID().equals(user)){
                removeIndex = i;
            }
        }
        if(removeIndex>-1){
            int color = connectedUsers.get(removeIndex).getColor();
            userColors.returnColor(color);
            connectedUsers.remove(removeIndex);
        }
    }

    public List<User> getUsers(){
        return connectedUsers;
    }

    
    public List<String> getUsernames(){
        ArrayList<String> usernames = new ArrayList<String>();

        for(User u:connectedUsers){
            usernames.add(u.getBluetoothID());
        }
        //Uncomment line below if usernames need to come out in the same order
        //Collections.sort(usernames);
        return usernames;
    }
    
    public User getUserByName(String username){
        User user = null;
        
        for(User u:connectedUsers){
            if(u.getBluetoothID().equals(username)){
                user = u;
            }
        }
        
        return user;
    }
}
