package com.lastcrusade.fanclub.components;

/**
 * This interface defines an API for receiving selections from the ListViewDialog
 * 
 * @author Jesse Rosalia
 *
 * @param <T>
 */
public interface IOnDialogItemClickListener<T> {

    public void onItemClick(T item);
}
