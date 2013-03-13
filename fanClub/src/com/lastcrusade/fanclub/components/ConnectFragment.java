package com.lastcrusade.fanclub.components;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.lastcrusade.fanclub.R;

/*
 * This fragment should be what is first presented to the user when
 * they enter the app and are not connected to any network
 */
public class ConnectFragment extends SherlockFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Button create = (Button)getView().findViewById(R.id.btn_create);
        create.setOnClickListener( new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //TODO Jesse - Create a new network 
                
            }
        });
        
        Button connect = (Button)getView().findViewById(R.id.btn_connect);
        connect.setOnClickListener( new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //TODO Jesse - Connect to an existing network
                
            }
        });
    }
}
