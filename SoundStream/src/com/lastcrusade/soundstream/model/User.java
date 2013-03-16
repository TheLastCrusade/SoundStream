package com.lastcrusade.soundstream.model;

import com.lastcrusade.soundstream.util.DefaultParcelableCreator;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class User implements Parcelable{
    
    public static final Parcelable.Creator<SongMetadata> CREATOR = new DefaultParcelableCreator(User.class);

    
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
    
    public User(Parcel in){
        bluetoothID = in.readString();
        macAddress = in.readString();
        color = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bluetoothID);
        dest.writeString(macAddress);
        dest.writeInt(color);
    }
    
    
}
