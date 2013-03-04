package com.lastcrusade.fanclub.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class UserList {
    /*
     * This is a way to keep track of the users and
     * their color ids. Color could be changed to an int if that works better - 
     * it would increase performance speed if it becomes an issue in parsing colors
     */
    private List<User> connectedUsers;
    private int nextColor = 0;
    private String[] colors = {
            /*"#ffff4444",
            "#ff33b5e5",
            "#ffffbb33",
            "#ffaa66cc",
            "#ff99cc00"*/
            "#FFB3ED00",
            "#FF02AD8C",
            "#FFFFC600",
            "#FFC10091",
            "#FFF99500",
            "#FFF94000",
            "#FF91B22C",
            "#FF1E7766",
            "#FFCAA72B",
            "#FF9B217D",
            "#FFC88C32"
    };
    
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

    public void addUser(String user){
        connectedUsers.add(new User(user, userColors.getNextAvailableColor()));
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
