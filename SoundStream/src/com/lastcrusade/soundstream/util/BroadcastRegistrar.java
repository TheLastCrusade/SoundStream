package com.lastcrusade.soundstream.util;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * A registrar for broadcast actions.  This provides an interface to register for individual
 * actions.  It uses a builder to allow for method chaining.
 * 
 * @author Jesse Rosalia
 *
 */
public class BroadcastRegistrar {

    private IntentFilter filter;
    private Map<String, IBroadcastActionHandler> handlerMap =
            new HashMap<String, IBroadcastActionHandler>();
    private BroadcastReceiver internalReceiver;
    private Context registeredContext;

    public BroadcastRegistrar() {
        this.filter = new IntentFilter();
    }
    
    public BroadcastRegistrar addAction(String action, IBroadcastActionHandler handler) {
        //add the action to the filter and store the handler
        this.filter.addAction(action);
        this.handlerMap.put(action, handler);
        return this;
    }

    public void register(Context context) {
        //this receiver routes actions to their registered handler
        this.internalReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                for (String key : handlerMap.keySet()) {
                    if (key.equals(intent.getAction())) {
                        handlerMap.get(key).onReceiveAction(context, intent);
                    }
                }
            }
        };
        //register the receiver, with the assembled filter
        context.registerReceiver(internalReceiver, filter);
        //hold on to the registered context, for unregister
        this.registeredContext = context;
    }
    
    public void unregister() {
        this.registeredContext.unregisterReceiver(internalReceiver);
    }
}
