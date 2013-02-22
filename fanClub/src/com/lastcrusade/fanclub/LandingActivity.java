package com.lastcrusade.fanclub;

import com.lastcrusade.fanclub.service.MusicLibrary;
import com.lastcrusade.fanclub.util.Toaster;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LandingActivity extends Activity {
    private final String TAG = "LandingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        ((Button) findViewById(R.id.btn_host))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        transitionTo(HostActivity.class);
                    }
                });

        ((Button) findViewById(R.id.btn_fan))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        transitionTo(FanActivity.class);
                    }
                });
        

        //temporary button for testing purposes
        ((Button) findViewById(R.id.btn_playlist))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        transitionTo(PlaylistActivity.class);
                    }
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_landing, menu);
        return true;
    }

    protected void transitionTo(Class<? extends Activity> activityClass) {
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Make sure to destroy services when the application closes
        Toaster.iToast(this, "Destroying MusicLibrary Service");
        Intent intent = new Intent(this, MusicLibrary.class);
        stopService(intent);

    }

}
