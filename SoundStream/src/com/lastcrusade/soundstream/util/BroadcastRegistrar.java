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

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * A registrar for broadcast actions.  This provides an interface to register for individual
 * actions.  It uses a builder to allow for method chaining.
 * 
 * @author Jesse Rosalia
 *
 */
public class BroadcastRegistrar {

    private IntentFilter localFilter;
    private IntentFilter globalFilter;
    private Map<String, IBroadcastActionHandler> localHandlerMap =
            new HashMap<String, IBroadcastActionHandler>();
    private Map<String, IBroadcastActionHandler> globalHandlerMap =
            new HashMap<String, IBroadcastActionHandler>();

    private BroadcastReceiver localReceiver;
    private BroadcastReceiver globalReceiver;
    private Context registeredContext;

    public BroadcastRegistrar() {
        this.localFilter  = new IntentFilter();
        this.globalFilter = new IntentFilter();
    }

    public BroadcastRegistrar addAction(String action, IBroadcastActionHandler handler) {
        //add the action to the filter and store the handler
        this.localFilter.addAction(action);
        this.localHandlerMap.put(action, handler);
        return this;
    }

    public BroadcastRegistrar addGlobalAction(String action, IBroadcastActionHandler handler){
        this.globalFilter.addAction(action);
        this.globalHandlerMap.put(action, handler);
        return this;
    }

    public void register(Context context) {
        //this receiver routes actions to their registered handler
        this.localReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                for (String key : localHandlerMap.keySet()) {
                    if (key.equals(intent.getAction())) {
                        localHandlerMap.get(key).onReceiveAction(context, intent);
                    }
                }
            }
        };

        this.globalReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                for (String key : globalHandlerMap.keySet()) {
                    if (key.equals(intent.getAction())) {
                        globalHandlerMap.get(key).onReceiveAction(context, intent);
                    }
                }
            }
        };

        //register the local receiver, with the assembled filter
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(localReceiver, localFilter);

        //register the global receiver, with the assembled filter
        context.registerReceiver(globalReceiver, globalFilter);

        //hold on to the registered context, for unregister
        this.registeredContext = context;
    }

    public void unregister() {
        LocalBroadcastManager.getInstance(registeredContext)
            .unregisterReceiver(localReceiver);
        this.registeredContext.unregisterReceiver(globalReceiver);
    }
}
