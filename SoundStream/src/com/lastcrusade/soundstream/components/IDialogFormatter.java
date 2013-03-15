package com.lastcrusade.soundstream.components;

/**
 * This interface defines an API for formatting objects to be displayed in the ListViewDialog
 * 
 * @author Jesse Rosalia
 *
 * @param <T>
 */
public interface IDialogFormatter<T> {

    public String format(T object);
}
