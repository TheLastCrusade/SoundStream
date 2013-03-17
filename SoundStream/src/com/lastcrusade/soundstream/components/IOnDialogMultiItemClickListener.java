package com.lastcrusade.soundstream.components;

import java.util.List;

/**
 * This interface defines an API for receiving selections from the ListViewDialog
 * 
 * @author Jesse Rosalia
 *
 * @param <T>
 */
public interface IOnDialogMultiItemClickListener<T> {

    public void onItemsClick(List<T> items);
}
