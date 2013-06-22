/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lastcrusade.soundstream.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

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
public class LocalBroadcastIntent extends Intent {

    public LocalBroadcastIntent(String action) {
        super.setAction(action);
    }    
    
    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public LocalBroadcastIntent putExtra(String name, boolean value) {
        return (LocalBroadcastIntent) super.putExtra(name, value);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public LocalBroadcastIntent putExtra(String name, byte[] value) {
        return (LocalBroadcastIntent) super.putExtra(name, value);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public LocalBroadcastIntent putExtra(String name, int value) {
        return (LocalBroadcastIntent) super.putExtra(name, value);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public LocalBroadcastIntent putExtra(String name, long value) {
        return (LocalBroadcastIntent) super.putExtra(name, value);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public LocalBroadcastIntent putExtra(String name, String value) {
        return (LocalBroadcastIntent) super.putExtra(name, value);
    }

    @Override
    public LocalBroadcastIntent putExtra(String name, Parcelable value) {
        return (LocalBroadcastIntent) super.putExtra(name, value);
    }

    @Override
    public LocalBroadcastIntent putExtra(String name, Parcelable[] values) {
        return (LocalBroadcastIntent) super.putExtra(name, values);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public LocalBroadcastIntent putParcelableArrayListExtra(String name,
            ArrayList<? extends Parcelable> value) {
        return (LocalBroadcastIntent) super.putParcelableArrayListExtra(name, value);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public LocalBroadcastIntent putStringArrayListExtra(String name, ArrayList<String> value) {
        return (LocalBroadcastIntent) super.putStringArrayListExtra(name, value);
    }
    
    public void send(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(this);
    }
}
