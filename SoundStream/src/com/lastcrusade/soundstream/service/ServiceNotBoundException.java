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
