package com.lastcrusade.fanclub.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Toaster {


    public static void tToast(Context context, String s) {
        Context ac = context.getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(ac, s, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void tToast(Context context, int resId) {
        Context ac = context.getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(ac, resId, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void tToast(Context context, int resId, Object ... formatArgs) {
        tToast(context, context.getString(resId, formatArgs));
    }
}
