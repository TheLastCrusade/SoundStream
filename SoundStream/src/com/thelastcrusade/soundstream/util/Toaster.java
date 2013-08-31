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

package com.thelastcrusade.soundstream.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Toaster {
    private final String TAG = Toaster.class.getSimpleName();
    private static int duration = Toast.LENGTH_SHORT;
    private static int methodDepth = 3;

    //LENGTH_SHORT can be any length so this is our best guess at the
    // maximum time we want to go before adding a new toast
    private static final int CHECK_DELTA = 2000; //Magic number
    private static final Map<Object, Long> lastShown = new HashMap<Object, Long>();

    private static boolean isRecent(Object obj) {
        Long last = lastShown.get(obj);
        if (last == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (last + CHECK_DELTA < now) {
            return false;
        }
        return true;
    }

    public static synchronized void toastWorker(Context context, String s) {
        if (isRecent(s)) {
            return;
        }
        lastShown.clear(); // Keep our map from growing in size
        lastShown.put(s, System.currentTimeMillis());
        Context ac = context.getApplicationContext();
        Toast toast = Toast.makeText(ac, s, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void iToast(Context context, String s) {
        Log.i(Thread.currentThread().getStackTrace()[methodDepth].toString(), s);
        toastWorker(context, s);
    }

    public static void iToast(Context context, int resId) {
        iToast(context, context.getString(resId));
    }

    public static void iToast(Context context, int resId, Object... formatArgs) {
        iToast(context, context.getString(resId, formatArgs));
    }

    public static void eToast(Context context, String s) {
        Log.e(Thread.currentThread().getStackTrace()[methodDepth].toString(), s);
        toastWorker(context, s);
    }
    
    public static void eToast(Context context, int resId) {
    	eToast(context, context.getString(resId));
    }
    
    public static void eToast(Context context, int resId, Object... formatArgs) {
    	eToast(context, context.getString(resId, formatArgs));
    }

}
