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

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Toaster {
    private final String TAG = Toaster.class.getName();
    private static int duration = Toast.LENGTH_SHORT;
    private static int methodDepth = 3;

    public static void iToast(Context context, String s) {
        Log.i(Thread.currentThread().getStackTrace()[methodDepth].toString(), s);
        toastWorker(context, s);
    }

    private static void toastWorker(Context context, String s) {
        Context ac = context.getApplicationContext();
        Toast toast = Toast.makeText(ac, s, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
