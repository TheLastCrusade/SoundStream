package com.lastcrusade.fanclub.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * A generic class for launching/locating services.  As a general pattern, create locators
 * in onCreate or onResume for any services you may need in that activity.
 * 
 * After the service is bound, use getService to get the service object.
 * 
 * NOTE: The binding/launching process happens in the message loop, so you MUST return from the
 * on* method for the service to actually launch.  We've seen that services bound in onCreate
 * will not be launched/bound by onResume, which means that any intent receivers or action/click
 * handlers that may get fired before or after onResume will not have access to the service.  If
 * you need to use the service to load data for the UI, or need to register receivers that use the
 * service, use an OnBindListener
 * 
 * @author Jesse Rosalia
 *
 * @param <T> The service to locate.
 */
public class ServiceLocator<T extends Service> implements ServiceConnection {

    /**
     * Used to listen for when a service is bound.  This is similar to Android's
     * ServiceConnection, but much simpler.
     * 
     * @author Jesse Rosalia
     *
     */
    public interface IOnBindListener {
        public void onServiceBound();
    }

    private T service;
    private boolean bound;
    private Class<? extends ILocalBinder<T>> serviceBinderClass;
    private Class<T> serviceClass;
    private Context context;
    private IOnBindListener onBindListener;

    public ServiceLocator(Context context, Class<T> serviceClass, Class<? extends ILocalBinder<T>> binderClass) {
        this.context      = context;
        this.serviceClass = serviceClass;
        this.serviceBinderClass = binderClass;
        Intent intent = new Intent();
        intent.setClass(context, serviceClass);
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder iservice) {
        service = serviceBinderClass.cast(iservice).getService();
        bound = true;
        if (onBindListener != null) {
            this.onBindListener.onServiceBound();
        }
    }

    public void onServiceDisconnected(ComponentName className) {
        service = null;
        bound = false;
    }
    
    public void setOnBindListener(IOnBindListener onBindListener) {
        this.onBindListener = onBindListener;
    }

    public T getService() throws ServiceNotBoundException {
        if (!this.bound) {
            throw new ServiceNotBoundException(this.serviceClass);
        }
        return this.service;
    }

    public void unbind() {
        this.context.unbindService(this);
    }
}
