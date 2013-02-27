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
    private Hashtable<String,String> connectedUsers = new Hashtable<String,String>();
    private int nextColor = 0;
    private String[] colors = {
            "#ffff4444",
            "#ff33b5e5",
            "#ffffbb33",
            "#ffaa66cc",
            "#ff99cc00"
    };

    public UserList(){
        connectedUsers.put("Reid", colors[0]);
        connectedUsers.put("Greenie", colors[1]);
        connectedUsers.put("Sills", colors[2]);
        connectedUsers.put("Jesse", colors[3]);
        connectedUsers.put("Lizziemom", colors[4]);
        nextColor = 5 % colors.length;
    }

    public UserList(String user){
        connectedUsers.put(user, colors[nextColor]);
        nextColor = (nextColor + 1) % colors.length;
    }

    public void addUser(String user){
        connectedUsers.put(user, colors[nextColor]);
        nextColor = (nextColor + 1) % colors.length;
    }

    public void removeUser(String user){
        connectedUsers.remove(user);
    }

    public Hashtable<String,String> getUsers(){
        return connectedUsers;
    }

    public List<String> getUsernames(){
        ArrayList<String> usernames = new ArrayList<String>();
        Iterator<Entry<String, String>> it = connectedUsers.entrySet().iterator();

        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            usernames.add(entry.getKey());
        }
        //Uncomment line below if usernames need to come out in the same order
        //Collections.sort(usernames);
        return usernames;
    }

}
