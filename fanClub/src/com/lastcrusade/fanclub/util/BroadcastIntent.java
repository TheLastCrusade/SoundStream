package com.lastcrusade.fanclub.util;

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
    public BroadcastIntent putParcelableArrayListExtra(String name,
            ArrayList<? extends Parcelable> value) {
        return (BroadcastIntent) super.putParcelableArrayListExtra(name, value);
    }

    public void send(Context context) {
        context.sendBroadcast(this);
    }
}
