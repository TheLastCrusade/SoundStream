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
    public BroadcastIntent putExtra(String name, byte[] value) {
        return (BroadcastIntent) super.putExtra(name, value);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public BroadcastIntent putExtra(String name, int value) {
        return (BroadcastIntent) super.putExtra(name, value);
    }

    //Overridden to return BroadcastIntent, so we can chain method calls
    @Override
    public BroadcastIntent putExtra(String name, long value) {
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
