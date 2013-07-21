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

package com.lastcrusade.soundstream.service;

import android.app.Service;
import android.content.Intent;
import android.test.ServiceTestCase;

public class AServiceTest<T extends Service> extends ServiceTestCase<T> {

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
