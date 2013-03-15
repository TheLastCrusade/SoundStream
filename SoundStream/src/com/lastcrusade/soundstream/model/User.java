package com.lastcrusade.soundstream.model;

import android.util.Log;

public class User {
    private final static String TAG = User.class.toString();
    //keep track of bluetoothID for display name
    private String bluetoothID;
    //keep track of macAddress for associating data 
    //with the correct user
    private String macAddress;
    private int color;
    
    public User(String bluetoothID, String macAddress, int color){
        this.bluetoothID = bluetoothID;
        this.macAddress = macAddress;
        this.color = color;
    }
    
    public String getBluetoothID(){
        return bluetoothID;
    }
    
    public int getColor(){
        return color;
    } 
    
    public String getMacAddress(){
        if(macAddress == null){
            Log.wtf(TAG, "Mac address null");
        }
        return macAddress;

    }
}
