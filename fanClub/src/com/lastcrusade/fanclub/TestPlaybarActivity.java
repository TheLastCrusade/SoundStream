package com.lastcrusade.fanclub;

import java.util.List;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.lastcrusade.fanclub.audio.SingleFileAudioPlayer;
import com.lastcrusade.fanclub.components.PlaybarFragment;
import com.lastcrusade.fanclub.components.PlaybarFragment.PlayControlListener;
import com.lastcrusade.fanclub.library.MediaStoreWrapper;
import com.lastcrusade.fanclub.library.SongNotFoundException;
import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.model.SongMetadata;
import com.lastcrusade.fanclub.service.AudioPlayerService;
import com.lastcrusade.fanclub.service.AudioPlayerService.AudioPlayerBinder;
import com.lastcrusade.fanclub.service.ServiceLocator;
import com.lastcrusade.fanclub.service.ServiceNotBoundException;
import com.lastcrusade.fanclub.util.Toaster;

/**
 * Test activity for the playbar.  This will display the playbar in an activity, and respond to play button clicks
 * by playing the first sound it can find on the phone.
 * 
 * @author Jesse Rosalia
 *
 */
public class TestPlaybarActivity extends FragmentActivity {

    private static final String TAG = "TestPlaybarActivity";
    
    private SingleFileAudioPlayer player;
    private MediaStoreWrapper mediaStore;
    private AudioPlayerService mService;
    protected boolean mBound;

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder iservice) {
            mService = AudioPlayerService.class.cast(iservice);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }

    };
    private ServiceLocator<AudioPlayerService> serviceLocator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_test_playbar);
        
        //create the service locator for the AudioPlayerService (which will bind to the service, launching if needed).
        this.serviceLocator =
                new ServiceLocator(TestPlaybarActivity.this, AudioPlayerService.class, AudioPlayerBinder.class);

        this.player = new SingleFileAudioPlayer();
        //JR, 02/27/13, NOTE: this code no longer works as written, because MediaStoreWrapper is written to use a Service
        // however it is still a great example of how to use the PlaybarFragment as it is currently written, and also
        // how to use the service locator

        //        this.mediaStore = new MediaStoreWrapper(this);
//        PlaybarFragment pb = (PlaybarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_playbar);
//        
//        pb.setPlayControlListener(new PlayControlListener() {
//
//            @Override
//            public void onPlay() {
//                List<SongMetadata> songMetas = mediaStore.list();
//                SongMetadata meta = songMetas.get(0);
//                try {
//                    Song song = mediaStore.loadSongData(meta);
//                    //play thru the audio player service.
//                    serviceLocator.getService().setSongByPath(song.getFilePath());
//                    serviceLocator.getService().play();
//                } catch (ServiceNotBoundException e) {
//                    Log.wtf(TAG, e);
//                    Toaster.eToast(TestPlaybarActivity.this, "Audio Player Service is not bound.");
//                } catch (SongNotFoundException e) {
//                    Log.wtf(TAG, e);
//                    Toaster.eToast(TestPlaybarActivity.this, "Song not found.");
//                }
//            }
//
//            @Override
//            public void onPause() {
//                player.pause();                
//            }
//
//            @Override
//            public void onSkip() {
//                // TODO Auto-generated method stub
//                
//            }
//            
//        });
    }
}
