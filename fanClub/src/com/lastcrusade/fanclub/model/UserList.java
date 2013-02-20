package com.lastcrusade.fanclub.model;

import java.util.Hashtable;

public class UserList {
    
    
    public static Hashtable<String,String> getUsers(){
        Hashtable<String,String> users = new Hashtable<String,String>();
        users.put("Reid", "#ffff4444");
        users.put("Greenie", "#ff33b5e5");
        users.put("Sills", "#ffffbb33");
        users.put("Jesse", "#ffaa66cc");
        users.put("Lizziemom", "ff99cc00");
        
        return users;
    }
    
}
