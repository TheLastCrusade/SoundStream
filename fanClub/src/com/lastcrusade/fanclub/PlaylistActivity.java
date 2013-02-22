package com.lastcrusade.fanclub;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.service.MusicLibrary;
import com.lastcrusade.fanclub.service.MusicLibrary.MusicLibraryBinder;
import com.lastcrusade.fanclub.util.MusicListAdapter;

public class PlaylistActivity extends Activity {
    MusicLibrary mMusicLibrary;
    boolean boundToService; //Since you cannot instantly bind, set a boolean
                    // after its safe to call methods

    //for testing purposes so we have songs to show
    Song[] songs = new Song[]{
            new Song(),
            new Song(),
            new Song()
    };

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection musicLibraryConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicLibraryBinder binder = (MusicLibraryBinder) service;
            mMusicLibrary = binder.getService();
            boundToService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundToService = false;
        }
    };
  
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        //for testing purposes
        for(Song s:songs){
            SongMetadata sm = new SongMetadata();
            sm.setTitle("Title");
            sm.setAlbum("Album");
            sm.setArtist("Artist");
            s.setSongMetadata(sm);
         }
         songs[0].getMetadata().setUsername("Reid");
         songs[1].getMetadata().setUsername("Lizziemom");
         songs[2].getMetadata().setUsername("Greenie");
         
        setContentView(R.layout.activity_playlist);
        ListView playlist = (ListView)findViewById(R.id.playlistView);
        playlist.setAdapter(new MusicListAdapter(this,songs));
        
        //Used to test Music Service
        ((Button) findViewById(R.id.btn_music_lib))
        .setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (boundToService) {
                    // Call a method from the MusicLibrary service.
                    // However, if this call were something that might hang, then this request should
                    // occur in a separate thread to avoid slowing down the activity performance.
                    int num = mMusicLibrary.getRandomNumber();
                    Toast.makeText(PlaylistActivity.this, "number: " + num, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        //To test Music service
        Intent intentML = new Intent(this, MusicLibrary.class);
        bindService(intentML, musicLibraryConn, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the MusicLibrary service
        if (boundToService) {
            unbindService(musicLibraryConn);
            boundToService = false;
        }
    }
    
}

