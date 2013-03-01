package com.lastcrusade.fanclub.util;

import android.content.Context;
import android.content.Intent;

/**
 * A handler for an individual broadcast action, for use with BroadcastRegistrar.
 * 
 * @author Jesse Rosalia
 *
 */
public interface IBroadcastActionHandler {

    public void onReceiveAction(Context context, Intent intent);
}
