package com.lastcrusade.soundstream.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * A cancelable dialog that displays a list, allows the user to select multiple items
 * and  calls a listener when an item is clicked.
 * 
 * @author Jesse Rosalia
 *
 */
public class MultiSelectListDialog<T> {

    private Activity activity;
    private IOnDialogMultiItemClickListener<T> onClickListener;
    private IDialogFormatter<T> formatter;
    private int titleResId;
    private int okButtonResId;
    private List<T> items;

    public MultiSelectListDialog(Activity activity, int titleResId, int okButtonResId) {
        this.activity = activity;
        this.titleResId = titleResId;
        this.okButtonResId = okButtonResId;
        this.formatter = new IDialogFormatter<T>() {

            @Override
            public String format(T object) {
                return object.toString();
            }
        };

        this.onClickListener = new IOnDialogMultiItemClickListener<T>() {

            @Override
            public void onItemsClick(List<T> items) {
                //NO OP
            }
        };
    }

    public MultiSelectListDialog<T> setFormatter(
            IDialogFormatter<T> formatter) {
        this.formatter = formatter;
        return this;
    }

    public MultiSelectListDialog<T> setItems(List<T> items) {
        this.items = items;
        return this;
    }

    public MultiSelectListDialog<T> setOnClickListener(IOnDialogMultiItemClickListener<T> onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public void show() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this.activity);

        // set title
        alertDialogBuilder.setTitle(this.titleResId);

        String[]  itemNames = new String[items.size()];
        boolean[] states = new boolean[items.size()];
        for (int ii = 0; ii < items.size(); ii++) {
            itemNames[ii] = this.formatter.format(items.get(ii));
            states[ii] = false;
        }

        //hold the selection, until the positive button is pressed
        //NOTE: this must not be a list, because we are dealing with integer data
        // and List#remove can get confused with integer data (it may attempt to remove
        // the item at position n), instead of with value n.
        final Set<Integer> selectedIndices = new HashSet<Integer>();

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setMultiChoiceItems(itemNames, states, new DialogInterface.OnMultiChoiceClickListener() {
                    //this gets called every time you click on an item, so we want to keep a set of
                    // selected items to be processed when the user pressed connect
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // The 'which' argument contains the index
                        // position the selected item
                        if (isChecked) {
                            selectedIndices.add(which);
                        } else {
                            selectedIndices.remove(which);
                        }
                    }
                })
                .setPositiveButton(this.okButtonResId, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<T> selectedItems = new ArrayList<T>();
                        for (int ind : selectedIndices) {
                            selectedItems.add(items.get(ind));
                        }
                        onClickListener.onItemsClick(selectedItems);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
