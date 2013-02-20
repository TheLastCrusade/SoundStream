package com.lastcrusade.fanclub.model;

import java.util.Hashtable;

public class UserList {
    
    /*
     * For testing, this is just a way to keep track of the users and 
     * their color ids. Color could be changed to an int if that works better - 
     * it would increase performance speed if it becomes an issue in parsing colors
     */
    public static Hashtable<String,String> getUsers(){
        Hashtable<String,String> users = new Hashtable<String,String>();
        
        users.put("Reid", "#ffff4444");
        users.put("Greenie", "#ff33b5e5");
        users.put("Sills", "#ffffbb33");
        users.put("Jesse", "#ffaa66cc");
        users.put("Lizziemom", "#ff99cc00");
        
        return users;
    }
    
}
