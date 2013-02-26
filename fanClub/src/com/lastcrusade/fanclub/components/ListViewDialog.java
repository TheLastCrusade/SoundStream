package com.lastcrusade.fanclub.components;

import java.util.List;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;

/**
 * A cancelable dialog that displays a list, and calls a listener when an item is clicked.
 * 
 * @author Jesse Rosalia
 *
 */
public class ListViewDialog<T> {

    private Context context;
    private IOnDialogItemClickListener<T> onClickListener;
    private IDialogFormatter<T> formatter;
    private int titleResId;
    private List<T> items;

    public ListViewDialog(Context context, int titleResId) {
        this.context = context;
        this.titleResId = titleResId;
        this.formatter = new IDialogFormatter<T>() {

            @Override
            public String format(T object) {
                return object.toString();
            }
        };

        this.onClickListener = new IOnDialogItemClickListener<T>() {

            @Override
            public void onItemClick(T item) {
                //NO OP
            }
        };
    }

    public ListViewDialog<T> setFormatter(
            IDialogFormatter<T> formatter) {
        this.formatter = formatter;
        return this;
    }

    public ListViewDialog<T> setItems(List<T> items) {
        this.items = items;
        return this;
    }

    public ListViewDialog<T> setOnClickListener(IOnDialogItemClickListener<T> onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public void show() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this.context);

        // set title
        alertDialogBuilder.setTitle(this.titleResId);

        String[] itemNames = new String[items.size()];
        for (int ii = 0; ii < items.size(); ii++) {
            itemNames[ii] = this.formatter.format(items.get(ii));
        }
        
        // set dialog message
        alertDialogBuilder
//                .setMessage(this.messageResId)
                .setCancelable(true)
                //TODO: add checkboxes to select multiple items at a time
                .setItems(itemNames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index
                        // position the selected item
                        onClickListener.onItemClick(items.get(which));
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
