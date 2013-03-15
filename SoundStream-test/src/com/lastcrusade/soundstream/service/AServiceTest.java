package com.lastcrusade.soundstream.service;

import android.app.Service;
import android.content.Intent;
import android.test.ServiceTestCase;

public class AServiceTest<T extends Service> extends ServiceTestCase {

    private Class<T> serviceClass;

    public AServiceTest(Class<T> serviceClass) {
        super(serviceClass);
        this.serviceClass = serviceClass;
    }

    protected T getTheService() {
        Intent intent = new Intent();
        intent.setClassName(this.serviceClass.getPackage().getName(), this.serviceClass.getName());
        super.bindService(intent);
        T service = (T) super.getService();
        return service;
    }
}
