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

    public UserList(){
        connectedUsers.put("Reid", colors[1]);
        connectedUsers.put("Greenie", colors[7]);
        connectedUsers.put("Sills", colors[8]);
        connectedUsers.put("Jesse", colors[9]);
        connectedUsers.put("Lizziemom", colors[10]);
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
        Iterator<Entry<String, String>> userListIterator = connectedUsers.entrySet().iterator();

        while (userListIterator.hasNext()) {
            Entry<String, String> entry = userListIterator.next();
            usernames.add(entry.getKey());
        }
        //Uncomment line below if usernames need to come out in the same order
        //Collections.sort(usernames);
        return usernames;
    }

}
