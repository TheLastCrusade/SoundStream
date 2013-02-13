package com.lastcrusade.fanclub;

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

}
