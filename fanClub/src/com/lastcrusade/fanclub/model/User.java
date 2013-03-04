package com.lastcrusade.fanclub.model;

public class User {
    private String bluetoothID;
    private int color;
    
    public User(String bluetoothID, int color){
        this.bluetoothID = bluetoothID;
        this.color = color;
    }
    
    public String getBluetoothID(){
        return bluetoothID;
    }
    
    public int getColor(){
        return color;
    }
    
    
}
