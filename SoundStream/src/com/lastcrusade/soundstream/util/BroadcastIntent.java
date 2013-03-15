package com.lastcrusade.soundstream.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

/**
 * A helper class for building and sending broadcast intents.
 * 
 * NOTE: as we want to use additional put* methods (for the extras bundle), we may
 * have to override the methods like we do with putParcelableArrayListExtra, so we
 * can keep method chaining functionality.
 * 
 * @author Jesse Rosalia
 *
 */
public class BroadcastIntent extends Intent {

    public BroadcastIntent(String action) {
        super.setAction(action);
    }    
    
    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public BroadcastIntent putExtra(String name, boolean value) {
        return (BroadcastIntent) super.putExtra(name, value);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public BroadcastIntent putExtra(String name, int value) {
        return (BroadcastIntent) super.putExtra(name, value);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public BroadcastIntent putExtra(String name, String value) {
        return (BroadcastIntent) super.putExtra(name, value);
    }

    @Override
    public BroadcastIntent putExtra(String name, Parcelable value) {
        return (BroadcastIntent) super.putExtra(name, value);
    }

    @Override
    public BroadcastIntent putExtra(String name, Parcelable[] values) {
        return (BroadcastIntent) super.putExtra(name, values);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public BroadcastIntent putParcelableArrayListExtra(String name,
            ArrayList<? extends Parcelable> value) {
        return (BroadcastIntent) super.putParcelableArrayListExtra(name, value);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public BroadcastIntent putStringArrayListExtra(String name, ArrayList<String> value) {
        return (BroadcastIntent) super.putStringArrayListExtra(name, value);
    }
    
    public void send(Context context) {
        context.sendBroadcast(this);
    }
}
