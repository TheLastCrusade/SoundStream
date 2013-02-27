package com.lastcrusade.fanclub.service;

/**
 * A generic interface for local service binders.  Local service binders provide direct
 * access to the bound service.
 * 
 * @author Jesse Rosalia
 *
 * @param <T> The class/type of service to be bound.
 */
public interface ILocalBinder<T> {

    public T getService();
}
