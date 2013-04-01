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

/**
 * Attempting to get a service that has not been bound (@see ServiceLocator)
 * 
 * @author Jesse Rosalia
 *
 */
public class ServiceNotBoundException extends Exception {

    public ServiceNotBoundException(Class<?> serviceClass) {
        super(formatMessage(serviceClass));
    }

    private static String formatMessage(Class<?> serviceClass) {
        return String.format("Attempting to access unbound service, class: " + serviceClass.getCanonicalName());
    }
    
}
