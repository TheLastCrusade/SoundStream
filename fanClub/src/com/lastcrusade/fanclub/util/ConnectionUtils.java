package com.lastcrusade.fanclub.util;

import android.content.Context;
import android.content.Intent;

public class ConnectionUtils {

    public static final String ACTION_CONNECTED    = "com.lastcrusade.fanclub.action.CONNECTED";
    public static final String ACTION_DISCONNECTED = "com.lastcrusade.fanclub.action.DISCONNECTED";

    /**
     * Notify the UI or any listeners that the socket is connected 
     * 
     */
    public static void notifyConnected(Context mmContext) {
        Intent intent = new Intent();
        intent.setAction(ConnectionUtils.ACTION_CONNECTED);
        //intent.addCategory(Intent.CATEGORY_DEFAULT);
        mmContext.sendBroadcast(intent);
    }

}
